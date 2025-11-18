package com.turkishairlines.flightbooking.service;

import com.turkishairlines.flightbooking.entity.Flight;
import com.turkishairlines.flightbooking.entity.SeatClass;
import com.turkishairlines.flightbooking.repository.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private FlightService flightService;

    private Flight testFlight;

    @BeforeEach
    void setUp() {
        testFlight = new Flight();
        testFlight.setId(1L);
        testFlight.setFlightNumber("TK001");
        testFlight.setDepartureCity("Istanbul");
        testFlight.setArrivalCity("London");
        testFlight.setDepartureTime(LocalDateTime.now().plusDays(1));
        testFlight.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(4));
        testFlight.setEconomySeats(150);
        testFlight.setBusinessSeats(30);
        testFlight.setFirstClassSeats(10);
        testFlight.setEconomyPrice(BigDecimal.valueOf(299.99));
        testFlight.setBusinessPrice(BigDecimal.valueOf(799.99));
        testFlight.setFirstClassPrice(BigDecimal.valueOf(1299.99));
        testFlight.setStatus(Flight.FlightStatus.ACTIVE);
    }

    @Test
    void searchFlights_ShouldReturnFlights_WhenValidCriteria() {
        // Arrange
        LocalDate departureDate = LocalDate.now().plusDays(1);
        List<Flight> expectedFlights = Arrays.asList(testFlight);
        
        when(flightRepository.searchFlights(
            eq("Istanbul"), 
            eq("London"), 
            any(LocalDateTime.class), 
            any(LocalDateTime.class)
        )).thenReturn(expectedFlights);

        // Act
        List<Flight> result = flightService.searchFlights("Istanbul", "London", departureDate);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testFlight.getFlightNumber(), result.get(0).getFlightNumber());
        
        verify(flightRepository).searchFlights(
            eq("Istanbul"), 
            eq("London"), 
            any(LocalDateTime.class), 
            any(LocalDateTime.class)
        );
    }

    @Test
    void findByFlightNumber_ShouldReturnFlight_WhenExists() {
        // Arrange
        when(flightRepository.findByFlightNumber("TK001")).thenReturn(Optional.of(testFlight));

        // Act
        Optional<Flight> result = flightService.findByFlightNumber("TK001");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("TK001", result.get().getFlightNumber());
        verify(flightRepository).findByFlightNumber("TK001");
    }

    @Test
    void findByFlightNumber_ShouldReturnEmpty_WhenNotExists() {
        // Arrange
        when(flightRepository.findByFlightNumber("TK999")).thenReturn(Optional.empty());

        // Act
        Optional<Flight> result = flightService.findByFlightNumber("TK999");

        // Assert
        assertFalse(result.isPresent());
        verify(flightRepository).findByFlightNumber("TK999");
    }

    @Test
    void getAllActiveFlights_ShouldReturnActiveFlights() {
        // Arrange
        List<Flight> expectedFlights = Arrays.asList(testFlight);
        when(flightRepository.findByStatus(Flight.FlightStatus.ACTIVE)).thenReturn(expectedFlights);

        // Act
        List<Flight> result = flightService.getAllActiveFlights();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(flightRepository).findByStatus(Flight.FlightStatus.ACTIVE);
    }

    @Test
    void createFlight_ShouldSaveFlight() {
        // Arrange
        when(flightRepository.save(testFlight)).thenReturn(testFlight);

        // Act
        Flight result = flightService.createFlight(testFlight);

        // Assert
        assertNotNull(result);
        assertEquals(testFlight.getFlightNumber(), result.getFlightNumber());
        verify(flightRepository).save(testFlight);
    }

    @Test
    void hasAvailableSeats_ShouldReturnTrue_WhenSeatsAvailable() {
        // Arrange
        when(flightRepository.countBookedSeats(testFlight)).thenReturn(50L);

        // Act
        boolean result = flightService.hasAvailableSeats(testFlight, SeatClass.ECONOMY, 10);

        // Assert
        assertTrue(result);
        verify(flightRepository).countBookedSeats(testFlight);
    }

    @Test
    void hasAvailableSeats_ShouldReturnFalse_WhenNotEnoughSeats() {
        // Arrange
        when(flightRepository.countBookedSeats(testFlight)).thenReturn(185L); // Almost full

        // Act
        boolean result = flightService.hasAvailableSeats(testFlight, SeatClass.ECONOMY, 10);

        // Assert
        assertFalse(result);
        verify(flightRepository).countBookedSeats(testFlight);
    }

    @Test
    void getAvailableSeatsCount_ShouldReturnCount() {
        // Arrange
        when(flightRepository.countAvailableSeats(testFlight)).thenReturn(140L);

        // Act
        Long result = flightService.getAvailableSeatsCount(testFlight);

        // Assert
        assertEquals(140L, result);
        verify(flightRepository).countAvailableSeats(testFlight);
    }

    @Test
    void deleteFlight_ShouldCallRepository() {
        // Act
        flightService.deleteFlight(1L);

        // Assert
        verify(flightRepository).deleteById(1L);
    }
}