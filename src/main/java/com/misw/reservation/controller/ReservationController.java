package com.misw.reservation.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.misw.reservation.service.ReservationCreationService;
import com.misw.reservation.service.ReservationService;
import com.misw.reservation.service.ReservationUpdateService;
import com.misw.reservation.util.BadRequest;
import com.misw.reservation.util.ExceptionHandle;
import com.misw.reservation.util.Response;

@Transactional
@RestController
public class ReservationController {

	@Autowired
	private ReservationService reservationService;

	@Autowired
	private ReservationCreationService reservationCreationService;

	@Autowired
	private ReservationUpdateService reservationUpdateService;

	@RequestMapping(
		value = "/reservation/{number}",
		method = RequestMethod.GET,
		produces = { "application/json", "application/xml" }
	)
	public ResponseEntity<?> getReservation(
			@PathVariable String number,
			@RequestParam(value = "xml", required = false) String xml) {
		try {
			return reservationService.getReservation(number);
		} catch (IllegalArgumentException e) {
			return ResponseEntity
					.badRequest()
					.body(new ExceptionHandle(new BadRequest(404, e.getMessage())));
		}
	}

	@RequestMapping(
		value = "/reservation",
		method = RequestMethod.POST,
		produces = { "application/json", "application/xml" }
	)
	public ResponseEntity<?> createReservation(
			@RequestParam("passengerId") String passengerId,
			@RequestParam("flightNumbers") List<String> flightNumbers,
			@RequestParam(value = "xml", required = false) String xml) {
		try {
			return reservationCreationService.createReservation(passengerId, flightNumbers);
		} catch (IllegalArgumentException e) {
			return ResponseEntity
					.badRequest()
					.body(new ExceptionHandle(new BadRequest(400, e.getMessage())));
		}
	}

	@RequestMapping(
		value = "/reservation/{number}",
		method = RequestMethod.POST,
		produces = { "application/json", "application/xml" }
	)
	public ResponseEntity<?> updateReservation(
			@PathVariable String number,
			@RequestParam(required = false) List<String> flightsAdded,
			@RequestParam(required = false) List<String> flightsRemoved,
			@RequestParam(value = "xml", required = false) String xml) {
		try {
			return reservationUpdateService.updateReservation(number, flightsAdded, flightsRemoved);
		} catch (IllegalArgumentException e) {
			return ResponseEntity
					.badRequest()
					.body(new ExceptionHandle(new BadRequest(400, e.getMessage())));
		}
	}

	@RequestMapping(
		value = "/reservation/{number}",
		method = RequestMethod.DELETE,
		produces = { "application/json", "application/xml" }
	)
	public ResponseEntity<?> cancelReservation(
			@PathVariable String number,
			@RequestParam(value = "xml", required = false) String xml) {
		try {
			reservationService.cancelReservation(number);
			return ResponseEntity
					.status(HttpStatus.OK)
					.body(new Response(200, "Reservation with number " + number + " is cancelled successfully"));
		} catch (IllegalArgumentException e) {
			return ResponseEntity
					.badRequest()
					.body(new ExceptionHandle(new BadRequest(404, e.getMessage())));
		}
	}

}
