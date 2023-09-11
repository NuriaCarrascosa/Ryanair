package com.example.ryanair.client.model;

import com.example.ryanair.model.Flight;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightPerDay {
    private int day;
    private List<FlightSchedule> flights;

    public List<Flight> toFlightList(int year, int month) {
        return flights.stream().map(flightSchedule -> flightSchedule.toFlight(year, month, day)).toList();
    }
}
