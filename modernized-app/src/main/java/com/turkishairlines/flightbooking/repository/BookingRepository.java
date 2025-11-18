package com.turkishairlines.flightbooking.repository;

import com.turkishairlines.flightbooking.entity.Booking;
import com.turkishairlines.flightbooking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    Optional<Booking> findByBookingReference(String bookingReference);
    
    List<Booking> findByCustomer(User customer);
    
    List<Booking> findByCustomerOrderByBookingDateDesc(User customer);
    
    @Query("SELECT b FROM Booking b WHERE b.status = :status")
    List<Booking> findByStatus(@Param("status") Booking.BookingStatus status);
    
    @Query("SELECT b FROM Booking b WHERE b.bookingDate >= :startDate AND b.bookingDate <= :endDate")
    List<Booking> findBookingsByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.customer = :customer AND b.status = 'CONFIRMED'")
    Long countConfirmedBookingsByCustomer(@Param("customer") User customer);
    
    @Query("SELECT b FROM Booking b WHERE b.flight.id = :flightId")
    List<Booking> findBookingsByFlightId(@Param("flightId") Long flightId);
}