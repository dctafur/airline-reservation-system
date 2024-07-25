package com.misw.reservation.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.misw.reservation.entity.Flight;
import com.misw.reservation.entity.Passenger;
import com.misw.reservation.entity.Reservation;
import com.misw.reservation.repository.ReservationRepository;

@Service
public class ReservationUpdateService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private FlightService flightService;

    public ResponseEntity<?> updateReservation(
            String number,
            List<String> flightsAdded,
            List<String> flightsRemoved) {
        Reservation existingReservation = reservationRepository.findByReservationNumber(number);

        if (existingReservation != null) {
            List<Flight> existingFlightList = existingReservation.getFlights();
            if (CollectionUtils.isEmpty(flightsAdded) &&
                    !CollectionUtils.isEmpty(flightsRemoved) &&
                    existingFlightList.size() <= flightsRemoved.size()) {
                throw new IllegalArgumentException("Cannot remove all flights.");
            }

            Passenger passenger = existingReservation.getPassenger();
            String passengerId = passenger.getId();
            int existingPrice = existingReservation.getPrice();

            if (!CollectionUtils.isEmpty(flightsRemoved)) {
                List<Flight> flightListToRemove = flightService.getFlightList(
                        flightsRemoved.stream().map(String::trim).collect(Collectors.toList()));

                existingFlightList.removeAll(flightListToRemove);
                flightService.increaseAvailableFlightSeats(flightListToRemove);
                existingReservation.setPrice(existingPrice - flightService.calculatePrice(flightListToRemove));
            }

            if (!CollectionUtils.isEmpty(flightsAdded)) {
                List<Flight> flightListToAdd = flightService.getFlightList(
                        flightsAdded.stream().map(String::trim).collect(Collectors.toList()));

                if (flightListToAdd.size() > 1 &&
                        (flightService.isTimeOverlapWithinReservation(flightListToAdd) ||
                                flightService.isTimeOverlapForSamePerson(passengerId, flightListToAdd))) {
                    throw new IllegalArgumentException("Time overlap detected.");
                }

                if (flightService.isSeatsAvailable(flightListToAdd)) {
                    existingFlightList.addAll(flightListToAdd);
                    existingReservation.setPrice(existingPrice + flightService.calculatePrice(flightListToAdd));
                    flightService.reduceAvailableFlightSeats(flightListToAdd);
                } else {
                    throw new IllegalArgumentException("No seats available.");
                }
            }

            Reservation resUpdate = reservationRepository.save(existingReservation);
            return new ResponseEntity<>(resUpdate, HttpStatus.OK);
        } else {
            throw new IllegalArgumentException("No reservation found for given reservation number");
        }
    }

}
