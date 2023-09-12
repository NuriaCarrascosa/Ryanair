package com.example.ryanair.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FlightsRequest {
    String departure;
    String arrival;
    LocalDateTime departureDateTimeRequested;
    LocalDateTime arrivalDateTimeRequested;
}
