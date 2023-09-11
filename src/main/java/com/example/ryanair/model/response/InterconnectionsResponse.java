package com.example.ryanair.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class InterconnectionsResponse {
    private List<FlightsResponse> flightsResponseList;
}
