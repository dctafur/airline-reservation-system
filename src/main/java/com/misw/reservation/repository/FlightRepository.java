package com.misw.reservation.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.misw.reservation.entity.Flight;

public interface FlightRepository extends JpaRepository<Flight, Integer> {

	Optional<Flight> findOptionalByFlightNumber(String flightNumber);

}
