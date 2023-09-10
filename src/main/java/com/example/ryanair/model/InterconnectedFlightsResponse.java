package com.example.ryanair.model;

import lombok.Data;

import java.util.List;

import static com.example.ryanair.service.InterconnectionsService.MAX_NUM_OF_STOPS;

@Data
public class InterconnectedFlightsResponse extends FlightsResponse {
    public InterconnectedFlightsResponse(List<FlightInfoResponse> legs) {
        this.stops = MAX_NUM_OF_STOPS;
        this.legs = legs;
    }
}
