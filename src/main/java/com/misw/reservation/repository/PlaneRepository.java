package com.misw.reservation.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.misw.reservation.entity.Plane;

public interface PlaneRepository extends JpaRepository<Plane, String> {

    Optional<Plane> findOptionalById(String id);

}
