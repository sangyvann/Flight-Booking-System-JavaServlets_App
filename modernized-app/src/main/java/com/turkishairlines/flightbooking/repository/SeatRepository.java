package com.turkishairlines.flightbooking.repository;

import com.turkishairlines.flightbooking.entity.Booking;
import com.turkishairlines.flightbooking.entity.Flight;
import com.turkishairlines.flightbooking.entity.Seat;
import com.turkishairlines.flightbooking.entity.SeatClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    
    List<Seat> findSeatsByFlight(Flight flight);
    
    @Query("SELECT s FROM Seat s WHERE s.flight = :flight AND s.seatClass = :seatClass AND s.available = true")
    List<Seat> findAvailableSeatsByFlightAndClass(
        @Param("flight") Flight flight, 
        @Param("seatClass") SeatClass seatClass
    );
    
    @Query("SELECT s FROM Seat s WHERE s.booking = :booking")
    List<Seat> findSeatsByBooking(@Param("booking") Booking booking);
    
    @Query("SELECT COUNT(s) FROM Seat s WHERE s.flight = :flight AND s.seatClass = :seatClass AND s.available = true")
    Long countAvailableSeatsByFlightAndClass(
        @Param("flight") Flight flight, 
        @Param("seatClass") SeatClass seatClass
    );
    
    @Query("SELECT COUNT(s) FROM Seat s WHERE s.flight = :flight AND s.available = false")
    Long countBookedSeatsByFlight(@Param("flight") Flight flight);
}