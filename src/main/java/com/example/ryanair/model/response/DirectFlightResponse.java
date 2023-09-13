package com.example.ryanair.model.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DirectFlightResponse extends FlightResponse {
    private static final int NUM_OF_STOPS = 0;
    private static final int NUM_OF_FLIGHTS_IN_DIRECT_FLIGHT = 1;

    public DirectFlightResponse(FlightInfoResponse flight){
        List<FlightInfoResponse> flightInfoResponseList = new ArrayList<>(NUM_OF_FLIGHTS_IN_DIRECT_FLIGHT);
        flightInfoResponseList.add(flight);
        this.legs = flightInfoResponseList;
        this.stops = NUM_OF_STOPS;
    }

}
