package com.example.ryanair.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Route {
    private String airportFrom; // IATA code
    private String airportTo; // IATA code
    private String connectingAirport; // IATA code
    private boolean newRoute;
    private boolean seasonalRoute;
    private String operator;
    private String group;
}
