package com.example.ryanair.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Flight {
    private String flightNumber;
    private LocalDateTime departureDateTime;
    private LocalDateTime arrivalDateTime;
    private String departure;
    private String arrival;
}
