package com.misw.reservation.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.misw.reservation.entity.Passenger;

public interface PassengerRepository extends JpaRepository<Passenger, String> {

	Passenger findByPhone(String phone);

	Optional<Passenger> findOptionalById(String id);

}
