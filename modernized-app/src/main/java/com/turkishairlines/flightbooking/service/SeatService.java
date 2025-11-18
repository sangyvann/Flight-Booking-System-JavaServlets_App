package com.turkishairlines.flightbooking.service;

import com.turkishairlines.flightbooking.entity.Booking;
import com.turkishairlines.flightbooking.entity.Flight;
import com.turkishairlines.flightbooking.entity.Seat;
import com.turkishairlines.flightbooking.entity.SeatClass;
import com.turkishairlines.flightbooking.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SeatService {
    
    private final SeatRepository seatRepository;
    
    @Autowired
    public SeatService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }
    
    public void reserveSeats(Flight flight, SeatClass seatClass, int numberOfSeats, Booking booking) {
        List<Seat> availableSeats = seatRepository.findAvailableSeatsByFlightAndClass(flight, seatClass);
        
        if (availableSeats.size() < numberOfSeats) {
            throw new IllegalStateException("Not enough available seats of the requested class");
        }
        
        for (int i = 0; i < numberOfSeats; i++) {
            Seat seat = availableSeats.get(i);
            seat.setAvailable(false);
            seat.setBooking(booking);
            seatRepository.save(seat);
        }
    }
    
    public void releaseSeats(Booking booking) {
        List<Seat> bookedSeats = seatRepository.findSeatsByBooking(booking);
        for (Seat seat : bookedSeats) {
            seat.setAvailable(true);
            seat.setBooking(null);
            seatRepository.save(seat);
        }
    }
    
    public List<Seat> getAvailableSeats(Flight flight, SeatClass seatClass) {
        return seatRepository.findAvailableSeatsByFlightAndClass(flight, seatClass);
    }
    
    public List<Seat> getSeatsByFlight(Flight flight) {
        return seatRepository.findSeatsByFlight(flight);
    }
    
    public void initializeSeatsForFlight(Flight flight) {
        // Create economy seats
        for (int i = 1; i <= flight.getEconomySeats(); i++) {
            Seat seat = new Seat();
            seat.setSeatNumber("E" + String.format("%02d", i));
            seat.setSeatClass(SeatClass.ECONOMY);
            seat.setFlight(flight);
            seat.setAvailable(true);
            seatRepository.save(seat);
        }
        
        // Create business seats
        for (int i = 1; i <= flight.getBusinessSeats(); i++) {
            Seat seat = new Seat();
            seat.setSeatNumber("B" + String.format("%02d", i));
            seat.setSeatClass(SeatClass.BUSINESS);
            seat.setFlight(flight);
            seat.setAvailable(true);
            seatRepository.save(seat);
        }
        
        // Create first class seats
        for (int i = 1; i <= flight.getFirstClassSeats(); i++) {
            Seat seat = new Seat();
            seat.setSeatNumber("F" + String.format("%02d", i));
            seat.setSeatClass(SeatClass.FIRST_CLASS);
            seat.setFlight(flight);
            seat.setAvailable(true);
            seatRepository.save(seat);
        }
    }
}