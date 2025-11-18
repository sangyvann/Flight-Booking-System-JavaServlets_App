package com.turkishairlines.flightbooking.dto;

import com.turkishairlines.flightbooking.entity.Flight;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FlightResponse {
    
    private Long id;
    private String flightNumber;
    private String departureCity;
    private String arrivalCity;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private Integer economySeats;
    private Integer businessSeats;
    private Integer firstClassSeats;
    private BigDecimal economyPrice;
    private BigDecimal businessPrice;
    private BigDecimal firstClassPrice;
    private Flight.FlightStatus status;
    private Long availableSeats;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }
    
    public String getDepartureCity() { return departureCity; }
    public void setDepartureCity(String departureCity) { this.departureCity = departureCity; }
    
    public String getArrivalCity() { return arrivalCity; }
    public void setArrivalCity(String arrivalCity) { this.arrivalCity = arrivalCity; }
    
    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }
    
    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }
    
    public Integer getEconomySeats() { return economySeats; }
    public void setEconomySeats(Integer economySeats) { this.economySeats = economySeats; }
    
    public Integer getBusinessSeats() { return businessSeats; }
    public void setBusinessSeats(Integer businessSeats) { this.businessSeats = businessSeats; }
    
    public Integer getFirstClassSeats() { return firstClassSeats; }
    public void setFirstClassSeats(Integer firstClassSeats) { this.firstClassSeats = firstClassSeats; }
    
    public BigDecimal getEconomyPrice() { return economyPrice; }
    public void setEconomyPrice(BigDecimal economyPrice) { this.economyPrice = economyPrice; }
    
    public BigDecimal getBusinessPrice() { return businessPrice; }
    public void setBusinessPrice(BigDecimal businessPrice) { this.businessPrice = businessPrice; }
    
    public BigDecimal getFirstClassPrice() { return firstClassPrice; }
    public void setFirstClassPrice(BigDecimal firstClassPrice) { this.firstClassPrice = firstClassPrice; }
    
    public Flight.FlightStatus getStatus() { return status; }
    public void setStatus(Flight.FlightStatus status) { this.status = status; }
    
    public Long getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(Long availableSeats) { this.availableSeats = availableSeats; }
}