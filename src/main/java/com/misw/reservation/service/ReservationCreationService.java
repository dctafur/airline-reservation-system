package com.misw.reservation.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.misw.reservation.entity.Flight;
import com.misw.reservation.entity.Passenger;
import com.misw.reservation.entity.Reservation;
import com.misw.reservation.repository.PassengerRepository;
import com.misw.reservation.repository.ReservationRepository;

@Service
public class ReservationCreationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private FlightService flightService;

    public ResponseEntity<?> createReservation(String passengerId, List<String> flightNumbers) {
        Optional<Passenger> passenger = passengerRepository.findById(passengerId);

        if (passenger.isPresent() && !CollectionUtils.isEmpty(flightNumbers)) {
            List<String> trimmedFlightNumbers = flightNumbers
                    .stream()
                    .map(String::trim)
                    .collect(Collectors.toList());

            List<Flight> flightList = flightService.getFlightList(trimmedFlightNumbers);

            if (flightList.size() > 1) {
                if (flightService.isTimeOverlapWithinReservation(flightList) ||
                        flightService.isTimeOverlapForSamePerson(passengerId, flightList)) {
                    throw new IllegalArgumentException("Time overlap detected");
                }
            }

            if (flightService.isSeatsAvailable(flightList)) {
                int fare = flightService.calculatePrice(flightList);

                Reservation newReservation = new Reservation(
                        flightList.get(0).getOrigin(),
                        flightList.get(flightList.size() - 1).getDestination(),
                        fare,
                        passenger.get(),
                        flightList);

                passenger.get().getReservations().add(newReservation);
                flightList.forEach(flight -> flight.getPassengers().add(passenger.get()));

                flightService.reduceAvailableFlightSeats(flightList);
                Reservation res = reservationRepository.save(newReservation);
                return new ResponseEntity<>(res, HttpStatus.OK);
            } else {
                throw new IllegalArgumentException("No seats available.");
            }
        } else {
            throw new IllegalArgumentException("Invalid passenger or flight number.");
        }
    }

}
