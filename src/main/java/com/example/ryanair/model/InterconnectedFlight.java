package com.example.ryanair.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class InterconnectedFlight extends Flight {

    private int secondFlightNumber;
    private String stopAirport;
    private LocalDateTime stopArrivalDateTime;
    private LocalDateTime stopDepartureDateTime;

    public InterconnectedFlight(int firstFlightNumber, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime,
                                String departure, String arrival, int secondFlightNumber, String stopAirport,
                                LocalDateTime stopArrivalDateTime, LocalDateTime stopDepartureDateTime) {
        super(firstFlightNumber, departureDateTime, arrivalDateTime, departure, arrival);
        this.secondFlightNumber = secondFlightNumber;
        this.stopAirport = stopAirport;
        this.stopArrivalDateTime = stopArrivalDateTime;
        this.stopDepartureDateTime = stopDepartureDateTime;
    }
}
