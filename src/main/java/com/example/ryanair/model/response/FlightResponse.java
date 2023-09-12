package com.example.ryanair.model.response;

import lombok.Data;

import java.util.List;

@Data
public abstract class FlightResponse {
    protected int stops;
    protected List<FlightInfoResponse> legs;
}