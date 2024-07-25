package com.misw.reservation.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.misw.reservation.entity.Flight;
import com.misw.reservation.entity.Passenger;
import com.misw.reservation.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, String> {

	Reservation findByReservationNumber(String reservationNumber);

	List<Reservation> findAllByFlightsIn(List<Flight> flights);

	List<Reservation> findByPassenger(Passenger passenger);

}
