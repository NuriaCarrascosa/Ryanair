package com.example.ryanair.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public abstract class FlightsResponse {
    protected int stops;
    protected List<FlightInfoResponse> legs;
}