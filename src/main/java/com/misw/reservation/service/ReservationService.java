package com.misw.reservation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.misw.reservation.entity.Reservation;
import com.misw.reservation.repository.ReservationRepository;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    public ResponseEntity<?> getReservation(String id) {
        return reservationRepository.findById(id)
                .map(reservation -> new ResponseEntity<>(reservation, HttpStatus.OK))
                .orElseThrow(() -> new IllegalArgumentException("Reservation with number " + id + " does not exist"));
    }

    public ResponseEntity<?> cancelReservation(String reservationNumber) {
        Reservation res = reservationRepository.findByReservationNumber(reservationNumber);

        if (res != null) {
            res.getPassenger().getReservations().remove(res);
            reservationRepository.delete(res);
            return new ResponseEntity<>(
                    "Reservation with number " + reservationNumber + " is canceled successfully ",
                    HttpStatus.OK);
        } else {
            throw new IllegalArgumentException("Reservation with number " + reservationNumber + " does not exist");
        }
    }

}
