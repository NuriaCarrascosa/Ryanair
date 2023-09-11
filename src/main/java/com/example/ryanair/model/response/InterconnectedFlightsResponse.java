package com.example.ryanair.model.response;

import lombok.Data;

import java.util.List;

@Data
public class InterconnectedFlightsResponse extends FlightsResponse {

    public InterconnectedFlightsResponse(int stops, List<FlightInfoResponse> legs) {
        this.stops = stops;
        this.legs = legs;
    }
}
