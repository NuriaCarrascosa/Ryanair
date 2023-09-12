package com.example.ryanair.model.response;

import lombok.Data;

import java.util.List;

@Data
public class InterconnectedFlightResponse extends FlightResponse {

    public InterconnectedFlightResponse(int stops, List<FlightInfoResponse> legs) {
        this.stops = stops;
        this.legs = legs;
    }

}
