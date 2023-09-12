package com.example.ryanair.client;

import com.example.ryanair.model.Flight;
import com.example.ryanair.model.FlightsRequest;
import org.springframework.web.client.RestClientException;

import java.util.List;

public interface InterfazSchedulesAPI {

    List<Flight> getAllSchedules(FlightsRequest interconnectedFlightsRequest) throws RestClientException;

}
