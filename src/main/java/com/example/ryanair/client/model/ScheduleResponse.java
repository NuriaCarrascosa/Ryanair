package com.example.ryanair.client.model;

import com.example.ryanair.model.Flight;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleResponse {
    private int month;
    private List<FlightPerDay> days;

    public List<Flight> toFlightList(int year){
        return days.stream().flatMap(flightPerDay -> flightPerDay.toFlightList(year, month).stream()).toList();
    }

}
