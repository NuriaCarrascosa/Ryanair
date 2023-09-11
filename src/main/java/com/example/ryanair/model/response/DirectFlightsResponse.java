package com.example.ryanair.model.response;

import lombok.Data;

import java.util.List;

@Data
public class DirectFlightsResponse extends FlightsResponse {
    private static final int NUM_OF_STOPS = 0;

    public DirectFlightsResponse(List<FlightInfoResponse> legs) {
        this.stops = NUM_OF_STOPS;
        this.legs = legs;
    }
}
