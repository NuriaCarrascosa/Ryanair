package com.example.ryanair.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class InterconnectedFlight {

    private List<Flight> interconnectedFlight;

    public Flight getFlight(int i) {
        return interconnectedFlight.get(i);
    }
}
