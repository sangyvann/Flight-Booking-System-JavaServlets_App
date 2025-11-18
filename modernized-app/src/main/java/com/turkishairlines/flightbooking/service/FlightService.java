package com.turkishairlines.flightbooking.service;

import com.turkishairlines.flightbooking.entity.Flight;
import com.turkishairlines.flightbooking.entity.SeatClass;
import com.turkishairlines.flightbooking.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FlightService {
    
    private final FlightRepository flightRepository;
    
    @Autowired
    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }
    
    public List<Flight> searchFlights(String departureCity, String arrivalCity, LocalDate departureDate) {
        LocalDateTime startOfDay = departureDate.atStartOfDay();
        LocalDateTime endOfDay = departureDate.atTime(23, 59, 59);
        
        return flightRepository.searchFlights(departureCity, arrivalCity, startOfDay, endOfDay);
    }
    
    public Optional<Flight> findByFlightNumber(String flightNumber) {
        return flightRepository.findByFlightNumber(flightNumber);
    }
    
    public List<Flight> getAllActiveFlights() {
        return flightRepository.findByStatus(Flight.FlightStatus.ACTIVE);
    }
    
    public List<String> getAllDepartureCities() {
        return flightRepository.findAllDepartureCities();
    }
    
    public List<String> getAllArrivalCities() {
        return flightRepository.findAllArrivalCities();
    }
    
    public Flight createFlight(Flight flight) {
        return flightRepository.save(flight);
    }
    
    public Flight updateFlight(Flight flight) {
        return flightRepository.save(flight);
    }
    
    public void deleteFlight(Long flightId) {
        flightRepository.deleteById(flightId);
    }
    
    public Optional<Flight> findById(Long flightId) {
        return flightRepository.findById(flightId);
    }
    
    public boolean hasAvailableSeats(Flight flight, SeatClass seatClass, int requiredSeats) {
        Long bookedSeats = flightRepository.countBookedSeats(flight);
        int totalSeats = flight.getTotalSeats();
        int availableSeats = totalSeats - bookedSeats.intValue();
        
        return availableSeats >= requiredSeats;
    }
    
    public Long getAvailableSeatsCount(Flight flight) {
        return flightRepository.countAvailableSeats(flight);
    }
    
    public Long getBookedSeatsCount(Flight flight) {
        return flightRepository.countBookedSeats(flight);
    }
}