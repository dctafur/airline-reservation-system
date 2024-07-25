package com.misw.reservation.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.misw.reservation.entity.Flight;
import com.misw.reservation.entity.Passenger;
import com.misw.reservation.entity.Reservation;
import com.misw.reservation.repository.PassengerRepository;
import com.misw.reservation.repository.ReservationRepository;

@Service
public class PassengerService {

	@Autowired
	private PassengerRepository passengerRepository;

	@Autowired
	private ReservationRepository reservationRepository;

	public ResponseEntity<?> createPassenger(
			String firstName,
			String lastName,
			String age,
			String gender,
			String phone) {
		Passenger isPassengerExists = passengerRepository.findByPhone(phone);

		if (isPassengerExists == null) {
			Passenger newPassenger = new Passenger(
					firstName,
					lastName,
					Integer.parseInt(age),
					gender,
					phone);

			Passenger res = passengerRepository.save(newPassenger);
			return new ResponseEntity<>(res, HttpStatus.OK);
		} else {
			throw new IllegalArgumentException("Another passenger with the same number already exists.");
		}
	}

	public ResponseEntity<?> updatePassenger(
			String id,
			String firstName,
			String lastName,
			String age,
			String gender,
			String phone) {
		Optional<Passenger> existingPass = passengerRepository.findById(id);

		if (existingPass.isPresent()) {
			try {
				Passenger isPassengerExists = passengerRepository.findByPhone(phone);

				if (isPassengerExists == null) {
					Passenger passenger = existingPass.get();
					passenger.setAge(Integer.parseInt(age));
					passenger.setLastName(lastName);
					passenger.setFirstName(firstName);
					passenger.setGender(gender);
					passenger.setPhone(phone);
					Passenger res = passengerRepository.save(passenger);
					return new ResponseEntity<>(res, HttpStatus.OK);
				} else {
					throw new IllegalArgumentException("Passenger with same phone number already exist");
				}
			} catch (Exception ex) {
				throw new IllegalArgumentException("Passenger with same phone number already exist");
			}
		} else {
			throw new IllegalArgumentException("Passenger with id " + id + " does not exist");
		}
	}

	public void deleteReservation(Reservation reservation, Passenger passenger) {
		try {
			for (Flight flight : reservation.getFlights()) {
				updateFlightSeats(flight);
				flight.getPassengers().remove(passenger);
			}

			passenger.getReservations().remove(reservation);
			reservationRepository.delete(reservation);
		} catch (Exception ignored) {
		}
	}

	public void updateFlightSeats(Flight flight) {
		try {
			flight.setSeatsLeft(flight.getSeatsLeft() + 1);
		} catch (Exception ignored) {
		}
	}

	public ResponseEntity<?> deletePassenger(String id) {
		Optional<Passenger> existingPass = passengerRepository.findById(id);

		if (existingPass.isPresent()) {
			List<Reservation> reservations = reservationRepository.findByPassenger(existingPass.get());

			for (Reservation reservation : reservations) {
				deleteReservation(reservation, existingPass.get());
			}

			passengerRepository.deleteById(id);
			return new ResponseEntity<>("Passenger with id" + id + " is deleted successfully ", HttpStatus.OK);
		} else {
			throw new IllegalArgumentException("Passenger with id " + id + " does not exist");
		}
	}

	public ResponseEntity<?> getPassenger(String id) {
		Optional<Passenger> existingPass = passengerRepository.findById(id);
		if (existingPass.isPresent()) {
			Passenger passenger = existingPass.get();
			return new ResponseEntity<>(passenger, HttpStatus.OK);
		} else {
			throw new IllegalArgumentException("Passenger with id " + id + " does not exist");
		}
	}

}
