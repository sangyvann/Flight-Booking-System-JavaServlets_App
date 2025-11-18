package com.turkishairlines.flightbooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turkishairlines.flightbooking.dto.FlightSearchRequest;
import com.turkishairlines.flightbooking.entity.Flight;
import com.turkishairlines.flightbooking.service.FlightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FlightController.class)
class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlightService flightService;

    @Autowired
    private ObjectMapper objectMapper;

    private Flight testFlight;
    private FlightSearchRequest searchRequest;

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

        searchRequest = new FlightSearchRequest();
        searchRequest.setDepartureCity("Istanbul");
        searchRequest.setArrivalCity("London");
        searchRequest.setDepartureDate(LocalDate.now().plusDays(1));
        searchRequest.setPassengers(1);
    }

    @Test
    void searchFlights_ShouldReturnFlights_WhenValidRequest() throws Exception {
        // Arrange
        List<Flight> flights = Arrays.asList(testFlight);
        when(flightService.searchFlights(
            eq("Istanbul"), 
            eq("London"), 
            any(LocalDate.class)
        )).thenReturn(flights);
        when(flightService.getAvailableSeatsCount(testFlight)).thenReturn(140L);

        // Act & Assert
        mockMvc.perform(post("/api/flights/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(searchRequest))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpected(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].flightNumber").value("TK001"))
                .andExpect(jsonPath("$[0].departureCity").value("Istanbul"))
                .andExpect(jsonPath("$[0].arrivalCity").value("London"))
                .andExpect(jsonPath("$[0].availableSeats").value(140));
    }

    @Test
    void searchFlights_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        // Arrange
        FlightSearchRequest invalidRequest = new FlightSearchRequest();
        // Missing required fields

        // Act & Assert
        mockMvc.perform(post("/api/flights/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllFlights_ShouldReturnFlights() throws Exception {
        // Arrange
        List<Flight> flights = Arrays.asList(testFlight);
        when(flightService.getAllActiveFlights()).thenReturn(flights);
        when(flightService.getAvailableSeatsCount(testFlight)).thenReturn(140L);

        // Act & Assert
        mockMvc.perform(get("/api/flights"))
                .andExpect(status().isOk())
                .andExpected(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].flightNumber").value("TK001"));
    }

    @Test
    void getFlightByNumber_ShouldReturnFlight_WhenExists() throws Exception {
        // Arrange
        when(flightService.findByFlightNumber("TK001")).thenReturn(Optional.of(testFlight));
        when(flightService.getAvailableSeatsCount(testFlight)).thenReturn(140L);

        // Act & Assert
        mockMvc.perform(get("/api/flights/TK001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flightNumber").value("TK001"))
                .andExpect(jsonPath("$.departureCity").value("Istanbul"));
    }

    @Test
    void getFlightByNumber_ShouldReturnNotFound_WhenNotExists() throws Exception {
        // Arrange
        when(flightService.findByFlightNumber("TK999")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/flights/TK999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getDepartureCities_ShouldReturnCities() throws Exception {
        // Arrange
        List<String> cities = Arrays.asList("Istanbul", "Ankara", "Izmir");
        when(flightService.getAllDepartureCities()).thenReturn(cities);

        // Act & Assert
        mockMvc.perform(get("/api/flights/cities/departure"))
                .andExpect(status().isOk())
                .andExpected(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value("Istanbul"))
                .andExpect(jsonPath("$[1]").value("Ankara"))
                .andExpect(jsonPath("$[2]").value("Izmir"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createFlight_ShouldCreateFlight_WhenAdmin() throws Exception {
        // Arrange
        when(flightService.createFlight(any(Flight.class))).thenReturn(testFlight);
        when(flightService.getAvailableSeatsCount(testFlight)).thenReturn(190L);

        // Act & Assert
        mockMvc.perform(post("/api/flights")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testFlight))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flightNumber").value("TK001"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void createFlight_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/flights")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testFlight))
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAvailableSeats_ShouldReturnCount() throws Exception {
        // Arrange
        when(flightService.findById(1L)).thenReturn(Optional.of(testFlight));
        when(flightService.getAvailableSeatsCount(testFlight)).thenReturn(140L);

        // Act & Assert
        mockMvc.perform(get("/api/flights/1/availability"))
                .andExpect(status().isOk())
                .andExpect(content().string("140"));
    }

    @Test
    void getAvailableSeats_ShouldReturnNotFound_WhenFlightNotExists() throws Exception {
        // Arrange
        when(flightService.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/flights/999/availability"))
                .andExpect(status().isNotFound());
    }
}