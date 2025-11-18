package com.turkishairlines.flightbooking.repository;

import com.turkishairlines.flightbooking.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    
    Optional<Flight> findByFlightNumber(String flightNumber);
    
    @Query("SELECT f FROM Flight f WHERE f.departureCity = :departureCity AND f.arrivalCity = :arrivalCity AND f.departureTime >= :startDate AND f.departureTime <= :endDate AND f.status = 'ACTIVE'")
    List<Flight> searchFlights(
        @Param("departureCity") String departureCity,
        @Param("arrivalCity") String arrivalCity,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT f FROM Flight f WHERE f.departureTime >= :startDate AND f.departureTime <= :endDate AND f.status = 'ACTIVE'")
    List<Flight> findFlightsByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT DISTINCT f.departureCity FROM Flight f WHERE f.status = 'ACTIVE' ORDER BY f.departureCity")
    List<String> findAllDepartureCities();
    
    @Query("SELECT DISTINCT f.arrivalCity FROM Flight f WHERE f.status = 'ACTIVE' ORDER BY f.arrivalCity")
    List<String> findAllArrivalCities();
    
    @Query("SELECT f FROM Flight f WHERE f.status = :status")
    List<Flight> findByStatus(@Param("status") Flight.FlightStatus status);
    
    @Query("SELECT COUNT(s) FROM Seat s WHERE s.flight = :flight AND s.available = false")
    Long countBookedSeats(@Param("flight") Flight flight);
    
    @Query("SELECT COUNT(s) FROM Seat s WHERE s.flight = :flight AND s.available = true")
    Long countAvailableSeats(@Param("flight") Flight flight);
}