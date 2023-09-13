package com.example.ryanair.client;

import com.example.ryanair.model.Flight;
import com.example.ryanair.model.request.FlightRequest;
import org.springframework.web.client.RestClientException;

import java.util.List;

public interface SchedulesClient {

    List<Flight> getAllSchedules(FlightRequest flightsRequest) throws RestClientException;

}
