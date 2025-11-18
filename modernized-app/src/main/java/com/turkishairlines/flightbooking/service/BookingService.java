package com.turkishairlines.flightbooking.service;

import com.turkishairlines.flightbooking.entity.Booking;
import com.turkishairlines.flightbooking.entity.Flight;
import com.turkishairlines.flightbooking.entity.SeatClass;
import com.turkishairlines.flightbooking.entity.User;
import com.turkishairlines.flightbooking.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookingService {
    
    private final BookingRepository bookingRepository;
    private final FlightService flightService;
    private final SeatService seatService;
    
    @Autowired
    public BookingService(BookingRepository bookingRepository, 
                         FlightService flightService,
                         SeatService seatService) {
        this.bookingRepository = bookingRepository;
        this.flightService = flightService;
        this.seatService = seatService;
    }
    
    public Booking createBooking(User customer, Flight flight, SeatClass seatClass, int numberOfPassengers) {
        // Check if enough seats are available
        if (!flightService.hasAvailableSeats(flight, seatClass, numberOfPassengers)) {
            throw new IllegalStateException("Not enough seats available for this flight");
        }
        
        // Calculate total price
        BigDecimal unitPrice = flight.getPriceForClass(seatClass);
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(numberOfPassengers));
        
        // Create booking
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setFlight(flight);
        booking.setNumberOfPassengers(numberOfPassengers);
        booking.setTotalPrice(totalPrice);
        booking.setStatus(Booking.BookingStatus.PENDING);
        booking.setBookingDate(LocalDateTime.now());
        
        Booking savedBooking = bookingRepository.save(booking);
        
        // Reserve seats
        seatService.reserveSeats(flight, seatClass, numberOfPassengers, savedBooking);
        
        return savedBooking;
    }
    
    public Booking confirmBooking(Long bookingId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new IllegalArgumentException("Booking not found");
        }
        
        Booking booking = bookingOpt.get();
        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new IllegalStateException("Booking cannot be confirmed in its current state");
        }
        
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        return bookingRepository.save(booking);
    }
    
    public Booking cancelBooking(Long bookingId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new IllegalArgumentException("Booking not found");
        }
        
        Booking booking = bookingOpt.get();
        if (booking.getStatus() == Booking.BookingStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a completed booking");
        }
        
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        
        // Release reserved seats
        seatService.releaseSeats(booking);
        
        return bookingRepository.save(booking);
    }
    
    public List<Booking> getBookingsByCustomer(User customer) {
        return bookingRepository.findByCustomerOrderByBookingDateDesc(customer);
    }
    
    public Optional<Booking> findByBookingReference(String bookingReference) {
        return bookingRepository.findByBookingReference(bookingReference);
    }
    
    public Optional<Booking> findById(Long bookingId) {
        return bookingRepository.findById(bookingId);
    }
    
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
    
    public List<Booking> getBookingsByStatus(Booking.BookingStatus status) {
        return bookingRepository.findByStatus(status);
    }
    
    public List<Booking> getBookingsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return bookingRepository.findBookingsByDateRange(startDate, endDate);
    }
    
    public Long getConfirmedBookingsCountByCustomer(User customer) {
        return bookingRepository.countConfirmedBookingsByCustomer(customer);
    }
}