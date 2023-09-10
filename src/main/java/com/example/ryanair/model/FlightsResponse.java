package com.example.ryanair.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public abstract class FlightsResponse {
    protected int stops;
    protected List<FlightInfoResponse> legs;
}
