package com.example.ryanair.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.example.ryanair.service.InterconnectionsService.MIN_NUM_OF_STOPS;

@Data
public class DirectFlightsResponse extends FlightsResponse {
    public DirectFlightsResponse(List<FlightInfoResponse> legs) {
        this.stops = MIN_NUM_OF_STOPS;
        this.legs = legs;
    }
}
