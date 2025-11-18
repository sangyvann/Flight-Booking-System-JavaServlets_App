package com.turkishairlines.flightbooking.entity;

public enum SeatClass {
    ECONOMY("Economy"),
    BUSINESS("Business"),
    FIRST_CLASS("First Class");
    
    private final String displayName;
    
    SeatClass(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}