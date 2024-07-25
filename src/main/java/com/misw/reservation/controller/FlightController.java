package com.misw.reservation.controller;

import java.text.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.misw.reservation.service.FlightService;
import com.misw.reservation.util.BadRequest;
import com.misw.reservation.util.ExceptionHandle;
import com.misw.reservation.util.Response;

@RestController
public class FlightController {

	@Autowired
	private FlightService flightService;

	@RequestMapping(
		value = "/flight/{flightNumber}",
		method = RequestMethod.GET,
		produces = { "application/json", "application/xml" }
	)
	public ResponseEntity<?> getFlightByNumber(
			@PathVariable("flightNumber") String flightNumber,
			@RequestParam(value = "xml", required = false) String xml) {
		try {
			return flightService.getFlightByNumber(flightNumber);
		} catch (IllegalArgumentException e) {
			return ResponseEntity
					.badRequest()
					.body(new ExceptionHandle(new BadRequest(404, e.getMessage())));
		}
	}

	@RequestMapping(
		value = "/airline/{flightNumber}",
		method = RequestMethod.DELETE,
		produces = { "application/json", "application/xml" }
	)
	public ResponseEntity<?> deleteFlight(
			@PathVariable("flightNumber") String flightNumber,
			@RequestParam(value = "xml", required = false) String xml) {
		try {
			flightService.deleteFlight(flightNumber);
			return ResponseEntity
					.status(HttpStatus.OK)
					.body(new Response(200, "Flight " + flightNumber + " has been deleted successfully."));
		} catch (IllegalArgumentException e) {
			return ResponseEntity
					.badRequest()
					.body(new ExceptionHandle(new BadRequest(400, e.getMessage())));
		}
	}

	@RequestMapping(
		value = "/flight/{flightNumber}",
		method = RequestMethod.POST,
		produces = { "application/json", "application/xml" }
	)
	public ResponseEntity<?> updateFlight(
			@PathVariable("flightNumber") String flightNumber,
			@RequestParam("price") int price,
			@RequestParam("origin") String origin,
			@RequestParam("destination") String destination,
			@RequestParam("departureTime") String departureTime,
			@RequestParam("arrivalTime") String arrivalTime,
			@RequestParam("description") String description,
			@RequestParam("capacity") int capacity,
			@RequestParam("model") String model,
			@RequestParam("manufacturer") String manufacturer,
			@RequestParam("yearOfManufacture") int yearOfManufacture,
			@RequestParam(value = "xml", required = false) String xml) {
		try {
			return flightService.updateFlight(
					flightNumber,
					price,
					origin,
					destination,
					departureTime,
					arrivalTime,
					description,
					capacity,
					model,
					manufacturer,
					yearOfManufacture);
		} catch (IllegalArgumentException e) {
			return ResponseEntity
					.badRequest()
					.body(new ExceptionHandle(new BadRequest(400, e.getMessage())));
		} catch (ParseException e) {
			return ResponseEntity
					.badRequest()
					.body(new ExceptionHandle(new BadRequest(400, e.getMessage())));
		}
	}

}
