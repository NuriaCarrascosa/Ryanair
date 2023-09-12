package com.example.ryanair.client;

import com.example.ryanair.model.Flight;
import com.example.ryanair.model.FlightRequest;
import org.springframework.web.client.RestClientException;

import java.util.List;

public interface InterfazSchedulesAPI {

    List<Flight> getAllSchedules(FlightRequest interconnectedFlightsRequest) throws RestClientException;

    List<Flight> getAllSchedulesFromDepartureToArrival(FlightRequest flightsRequest) throws RestClientException;

    }
