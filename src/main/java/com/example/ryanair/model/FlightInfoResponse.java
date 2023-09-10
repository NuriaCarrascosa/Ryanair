package com.example.ryanair.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FlightInfoResponse {
    private String departureAirport;
    private String arrivalAirport;
    private LocalDateTime departureDateTime;
    private LocalDateTime arrivalDateTime;

    public static FlightInfoResponse parseFlightToFlightInfoResponse(Flight flight) {
        return new FlightInfoResponse(
                flight.getDeparture(),
                flight.getArrival(),
                flight.getDepartureDateTime(),
                flight.getArrivalDateTime());
    }
}