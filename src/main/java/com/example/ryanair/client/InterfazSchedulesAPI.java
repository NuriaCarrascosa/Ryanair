package com.example.ryanair.client;

import com.example.ryanair.model.Flight;
import com.example.ryanair.model.InterconnectedFlightsRequest;
import org.json.JSONException;

import java.util.List;

public interface InterfazSchedulesAPI {

    List<Flight> getAllSchedules(InterconnectedFlightsRequest interconnectedFlightsRequest) throws JSONException;

}
