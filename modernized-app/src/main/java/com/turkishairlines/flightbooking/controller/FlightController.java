package com.turkishairlines.flightbooking.controller;

import com.turkishairlines.flightbooking.dto.FlightSearchRequest;
import com.turkishairlines.flightbooking.dto.FlightResponse;
import com.turkishairlines.flightbooking.entity.Flight;
import com.turkishairlines.flightbooking.service.FlightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/flights")
@Tag(name = "Flight Management", description = "Flight search and management operations")
public class FlightController {
    
    private final FlightService flightService;
    
    @Autowired
    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }
    
    @PostMapping("/search")
    @Operation(summary = "Search flights", description = "Search for available flights based on criteria")
    public ResponseEntity<List<FlightResponse>> searchFlights(@Valid @RequestBody FlightSearchRequest request) {
        List<Flight> flights = flightService.searchFlights(
            request.getDepartureCity(),
            request.getArrivalCity(),
            request.getDepartureDate()
        );
        
        List<FlightResponse> response = flights.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "Get all active flights", description = "Retrieve all active flights")
    public ResponseEntity<List<FlightResponse>> getAllFlights() {
        List<Flight> flights = flightService.getAllActiveFlights();
        List<FlightResponse> response = flights.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{flightNumber}")
    @Operation(summary = "Get flight by number", description = "Retrieve flight details by flight number")
    public ResponseEntity<FlightResponse> getFlightByNumber(@PathVariable String flightNumber) {
        return flightService.findByFlightNumber(flightNumber)
            .map(flight -> ResponseEntity.ok(convertToResponse(flight)))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/cities/departure")
    @Operation(summary = "Get departure cities", description = "Get all available departure cities")
    public ResponseEntity<List<String>> getDepartureCities() {
        List<String> cities = flightService.getAllDepartureCities();
        return ResponseEntity.ok(cities);
    }
    
    @GetMapping("/cities/arrival")
    @Operation(summary = "Get arrival cities", description = "Get all available arrival cities")
    public ResponseEntity<List<String>> getArrivalCities() {
        List<String> cities = flightService.getAllArrivalCities();
        return ResponseEntity.ok(cities);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create flight", description = "Create a new flight (Admin only)")
    public ResponseEntity<FlightResponse> createFlight(@Valid @RequestBody Flight flight) {
        Flight createdFlight = flightService.createFlight(flight);
        return ResponseEntity.ok(convertToResponse(createdFlight));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update flight", description = "Update an existing flight (Admin only)")
    public ResponseEntity<FlightResponse> updateFlight(@PathVariable Long id, @Valid @RequestBody Flight flight) {
        flight.setId(id);
        Flight updatedFlight = flightService.updateFlight(flight);
        return ResponseEntity.ok(convertToResponse(updatedFlight));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete flight", description = "Delete a flight (Admin only)")
    public ResponseEntity<Void> deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/availability")
    @Operation(summary = "Get seat availability", description = "Get available seats count for a flight")
    public ResponseEntity<Long> getAvailableSeats(@PathVariable Long id) {
        return flightService.findById(id)
            .map(flight -> ResponseEntity.ok(flightService.getAvailableSeatsCount(flight)))
            .orElse(ResponseEntity.notFound().build());
    }
    
    private FlightResponse convertToResponse(Flight flight) {
        FlightResponse response = new FlightResponse();
        response.setId(flight.getId());
        response.setFlightNumber(flight.getFlightNumber());
        response.setDepartureCity(flight.getDepartureCity());
        response.setArrivalCity(flight.getArrivalCity());
        response.setDepartureTime(flight.getDepartureTime());
        response.setArrivalTime(flight.getArrivalTime());
        response.setEconomySeats(flight.getEconomySeats());
        response.setBusinessSeats(flight.getBusinessSeats());
        response.setFirstClassSeats(flight.getFirstClassSeats());
        response.setEconomyPrice(flight.getEconomyPrice());
        response.setBusinessPrice(flight.getBusinessPrice());
        response.setFirstClassPrice(flight.getFirstClassPrice());
        response.setStatus(flight.getStatus());
        response.setAvailableSeats(flightService.getAvailableSeatsCount(flight));
        return response;
    }
}