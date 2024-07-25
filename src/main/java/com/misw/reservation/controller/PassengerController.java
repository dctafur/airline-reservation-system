package com.misw.reservation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.misw.reservation.service.PassengerService;
import com.misw.reservation.util.BadRequest;
import com.misw.reservation.util.ExceptionHandle;
import com.misw.reservation.util.Response;

@Transactional
@RestController
public class PassengerController {

	@Autowired
	private PassengerService service;

	@RequestMapping(
		value = "/passenger/{id}",
		method = RequestMethod.PUT,
		produces = { "application/json", "application/xml" }
	)
	public ResponseEntity<?> updatePassenger(
			@PathVariable String id,
			@RequestParam("firstName") String firstName,
			@RequestParam("lastName") String lastName,
			@RequestParam("age") String age,
			@RequestParam("gender") String gender,
			@RequestParam("phone") String phone,
			@RequestParam(value = "xml", required = false) String xml) {
		try {
			return service.updatePassenger(
					id,
					firstName,
					lastName,
					age,
					gender,
					phone);
		} catch (IllegalArgumentException e) {
			return ResponseEntity
					.badRequest()
					.body(new ExceptionHandle(new BadRequest(400, e.getMessage())));
		}
	}

	@RequestMapping(
		value = "/passenger/{id}",
		method = RequestMethod.DELETE,
		produces = { "application/json", "application/xml" }
	)
	public ResponseEntity<?> deletePassenger(
			@PathVariable String id,
			@RequestParam(value = "xml", required = false) String xml) {
		try {
			service.deletePassenger(id);
			return ResponseEntity
					.status(HttpStatus.OK)
					.body(new Response(200, "Passenger with id" + id + " is deleted successfully"));
		} catch (IllegalArgumentException e) {
			return ResponseEntity
					.badRequest()
					.body(new ExceptionHandle(new BadRequest(404, e.getMessage())));
		}
	}

	@RequestMapping(
		value = "/passenger",
		method = RequestMethod.POST,
		produces = { "application/json", "application/xml" }
	)
	public ResponseEntity<?> createPassenger(
			@RequestParam("firstName") String firstName,
			@RequestParam("lastName") String lastName,
			@RequestParam("age") String age,
			@RequestParam("gender") String gender,
			@RequestParam("phone") String phone,
			@RequestParam(value = "xml", required = false) String xml) {
		try {
			return service.createPassenger(
					firstName,
					lastName,
					age,
					gender,
					phone);
		} catch (IllegalArgumentException e) {
			return ResponseEntity
					.badRequest()
					.body(new ExceptionHandle(new BadRequest(400, e.getMessage())));
		}
	}

	@RequestMapping(
		value = "/passenger/{id}",
		method = RequestMethod.GET,
		produces = { "application/json", "application/xml" })
	public ResponseEntity<?> getPassenger(
			@PathVariable("id") String id,
			@RequestParam(value = "xml", required = false) String xml) {
		try {
			return service.getPassenger(id);
		} catch (IllegalArgumentException e) {
			return ResponseEntity
					.badRequest()
					.body(new ExceptionHandle(new BadRequest(404, e.getMessage())));
		}
	}

}
