package com.example.ryanair.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FlightRequest {
    String departure;
    String arrival;
    LocalDateTime departureDateTimeRequested;
    LocalDateTime arrivalDateTimeRequested;
}
