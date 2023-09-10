package com.example.ryanair.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InterconnectionsResponse {
    private DirectFlightsResponse directFlights;
    private InterconnectedFlightsResponse interconnectedFlights;
}
