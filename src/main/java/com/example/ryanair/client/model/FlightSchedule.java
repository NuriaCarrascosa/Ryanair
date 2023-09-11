package com.example.ryanair.client.model;

import com.example.ryanair.model.Flight;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightSchedule {
    private String number;
    private LocalTime departureTime;
    private LocalTime arrivalTime;

    public Flight toFlight(int year, int month, int day) {
        LocalDate date = LocalDate.of(year, month, day);
       return Flight.builder().flightNumber(number).departureDateTime(LocalDateTime.of(date, departureTime)).arrivalDateTime(LocalDateTime.of(date, arrivalTime)).build();
    }
}
