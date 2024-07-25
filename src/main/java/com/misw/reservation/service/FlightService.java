package com.misw.reservation.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.misw.reservation.entity.Flight;
import com.misw.reservation.entity.Passenger;
import com.misw.reservation.entity.Plane;
import com.misw.reservation.entity.Reservation;
import com.misw.reservation.repository.FlightRepository;
import com.misw.reservation.repository.PassengerRepository;
import com.misw.reservation.repository.ReservationRepository;

@Service
public class FlightService {

	@Autowired
	private FlightRepository flightRepository;

	@Autowired
	private ReservationRepository reservationRepository;

	@Autowired
	private PassengerRepository passengerRepository;

	public ResponseEntity<?> getFlightByNumber(String flightNumber) {
		Optional<Flight> res = flightRepository.findOptionalByFlightNumber(flightNumber);

		if (res.isPresent()) {
			Flight flight = res.get();
			return new ResponseEntity<>(flight, HttpStatus.OK);
		} else {
			throw new IllegalArgumentException("Flight with number " + flightNumber + " does not exist");
		}
	}

	public ResponseEntity<?> updateFlight(
			String flightNumber,
			int price,
			String origin,
			String destination,
			String departureTime,
			String arrivalTime,
			String description,
			int capacity,
			String model,
			String manufacturer,
			int yearOfManufacture) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH");
		Date dTime = formatter.parse(departureTime);
		Date aTime = formatter.parse(arrivalTime);

		if (origin.equals(destination) || aTime.compareTo(dTime) <= 0) {
			throw new IllegalArgumentException("Illegal arguments entered to create/update flight.");
		}

		Optional<Flight> res = flightRepository.findOptionalByFlightNumber(flightNumber);
		Flight flight;
		Plane plane;

		if (res.isPresent()) {
			flight = res.get();
			Flight finalFlight = flight;

			List<Reservation> reservationList = reservationRepository.findAllByFlightsIn(
					new ArrayList<Flight>() {
						{
							add(finalFlight);
						}
					});

			if (reservationList.size() > capacity) {
				throw new IllegalArgumentException("Target capacity less than active reservations");
			}

			if (!checkValidUpdate(flight, aTime, dTime)) {
				throw new IllegalArgumentException("Flight timings overlapping with other passenger reservations");
			}

			flight.setPrice(price);
			flight.setOrigin(origin);
			flight.setDestination(destination);
			flight.setDepartureTime(dTime);
			flight.setArrivalTime(aTime);
			flight.setDescription(description);
			flight.setSeatsLeft(capacity);
			flight.getPlane().setCapacity(capacity);
			flight.getPlane().setModel(model);
			flight.getPlane().setManufacturer(manufacturer);
			flight.getPlane().setYearOfManufacture(yearOfManufacture);
		} else {
			plane = new Plane(capacity, model, manufacturer, yearOfManufacture);
			flight = new Flight(
					flightNumber,
					price,
					origin,
					destination,
					dTime,
					aTime,
					capacity,
					description,
					plane,
					new ArrayList<>());
		}

		flight = flightRepository.save(flight);
		return new ResponseEntity<>(flight, HttpStatus.OK);
	}

	public void deleteFlight(String flightNumber) {
		Optional<Flight> res = flightRepository.findOptionalByFlightNumber(flightNumber);

		if (res.isPresent()) {
			Flight flight = res.get();

			List<Reservation> reservationList = reservationRepository.findAllByFlightsIn(
					new ArrayList<Flight>() {
						{
							add(flight);
						}
					});

			if (!reservationList.isEmpty()) {
				throw new IllegalArgumentException("Flight " + flightNumber + " has active reservations");
			} else {
				flightRepository.delete(flight);
				new ResponseEntity<>(HttpStatus.OK);
			}
		} else {
			throw new IllegalArgumentException("Flight with number " + flightNumber + " does not exist");
		}
	}

	private boolean checkValidUpdate(
			Flight currentFlight,
			Date currentFlightArrivalTime,
			Date currentFlightDepartureTime) {
		for (Passenger passenger : passengerRepository.findAll()) {
			Set<Flight> flights = new HashSet<Flight>();

			for (Reservation reservation : passenger.getReservations()) {
				flights.addAll(reservation.getFlights());
			}

			if (flights.contains(currentFlight)) {
				flights.remove(currentFlight);

				for (Flight flight : flights) {
					Date flightDepartureTime = flight.getDepartureTime();
					Date flightArrivalTime = flight.getArrivalTime();

					if (currentFlightArrivalTime.compareTo(flightDepartureTime) >= 0
							&& currentFlightDepartureTime.compareTo(flightArrivalTime) <= 0) {
						return false;
					}
				}
			}
		}

		return true;
	}

	public List<Flight> getFlightList(List<String> flightNumbers) {
		List<Flight> flightList = new ArrayList<>();
		for (String flightNumber : flightNumbers) {
			flightRepository.findOptionalByFlightNumber(flightNumber).ifPresent(flightList::add);
		}
		return flightList;
	}

	public boolean isSeatsAvailable(List<Flight> flightList) {
		return flightList.stream().allMatch(flight -> flight.getSeatsLeft() > 0);
	}

	public void reduceAvailableFlightSeats(List<Flight> flightList) {
		flightList.forEach(flight -> flight.setSeatsLeft(flight.getSeatsLeft() - 1));
	}

	public void increaseAvailableFlightSeats(List<Flight> flightList) {
		flightList.forEach(flight -> flight.setSeatsLeft(flight.getSeatsLeft() + 1));
	}

	public int calculatePrice(List<Flight> flightList) {
		return flightList.stream().mapToInt(Flight::getPrice).sum();
	}

	public boolean isTimeOverlapWithinReservation(List<Flight> flightList) {
		for (int i = 0; i < flightList.size(); i++) {
			for (int j = i + 1; j < flightList.size(); j++) {
				Date startDate1 = flightList.get(i).getDepartureTime();
				Date endDate1 = flightList.get(i).getArrivalTime();
				Date startDate2 = flightList.get(j).getDepartureTime();
				Date endDate2 = flightList.get(j).getArrivalTime();
				if (startDate1.compareTo(endDate2) <= 0 && endDate1.compareTo(startDate2) >= 0) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isTimeOverlapForSamePerson(String passengerId, List<Flight> flightList) {
		Optional<Passenger> passenger = passengerRepository.findById(passengerId);
		List<Reservation> reservationList = passenger.get().getReservations();
		List<Flight> currentFlightList = new ArrayList<>();

		for (Reservation res : reservationList) {
			currentFlightList.addAll(res.getFlights());
		}

		for (Flight newFlight : flightList) {
			for (Flight existingFlight : currentFlightList) {
				Date startDate1 = newFlight.getDepartureTime();
				Date endDate1 = newFlight.getArrivalTime();
				Date startDate2 = existingFlight.getDepartureTime();
				Date endDate2 = existingFlight.getArrivalTime();
				if (startDate1.compareTo(endDate2) <= 0 && endDate1.compareTo(startDate2) >= 0) {
					return true;
				}
			}
		}
		return false;
	}

}
