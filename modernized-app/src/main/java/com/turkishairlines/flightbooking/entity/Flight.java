package com.turkishairlines.flightbooking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "flights")
public class Flight {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 10)
    @Column(name = "flight_number", nullable = false, unique = true)
    private String flightNumber;
    
    @NotBlank
    @Size(max = 100)
    @Column(name = "departure_city", nullable = false)
    private String departureCity;
    
    @NotBlank
    @Size(max = 100)
    @Column(name = "arrival_city", nullable = false)
    private String arrivalCity;
    
    @NotNull
    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;
    
    @NotNull
    @Column(name = "arrival_time", nullable = false)
    private LocalDateTime arrivalTime;
    
    @Positive
    @Column(name = "economy_seats", nullable = false)
    private Integer economySeats;
    
    @Positive
    @Column(name = "business_seats", nullable = false)
    private Integer businessSeats;
    
    @Positive
    @Column(name = "first_class_seats", nullable = false)
    private Integer firstClassSeats;
    
    @Column(name = "economy_price", nullable = false)
    private BigDecimal economyPrice;
    
    @Column(name = "business_price", nullable = false)
    private BigDecimal businessPrice;
    
    @Column(name = "first_class_price", nullable = false)
    private BigDecimal firstClassPrice;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FlightStatus status = FlightStatus.ACTIVE;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Seat> seats;
    
    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Booking> bookings;
    
    public enum FlightStatus {
        ACTIVE, CANCELLED, COMPLETED
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Business methods
    public Integer getTotalSeats() {
        return economySeats + businessSeats + firstClassSeats;
    }
    
    public BigDecimal getPriceForClass(SeatClass seatClass) {
        return switch (seatClass) {
            case ECONOMY -> economyPrice;
            case BUSINESS -> businessPrice;
            case FIRST_CLASS -> firstClassPrice;
        };
    }
    
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
    
    public FlightStatus getStatus() { return status; }
    public void setStatus(FlightStatus status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Set<Seat> getSeats() { return seats; }
    public void setSeats(Set<Seat> seats) { this.seats = seats; }
    
    public Set<Booking> getBookings() { return bookings; }
    public void setBookings(Set<Booking> bookings) { this.bookings = bookings; }
}