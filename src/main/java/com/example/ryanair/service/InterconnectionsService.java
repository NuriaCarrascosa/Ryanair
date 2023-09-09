package com.example.ryanair.service;

import com.example.ryanair.api.RoutesAPI;
import com.example.ryanair.api.SchedulesAPI;
import com.example.ryanair.model.Flight;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class InterconnectionsService {

    private final SchedulesAPI schedulesAPI;
    private final RoutesAPI routesAPI;
    //private final int MaxNumOfStops = 1;
    //private final int MinHourDifference = 2;

    public InterconnectionsService(SchedulesAPI schedulesAPI, RoutesAPI routesAPI) {
        this.schedulesAPI = schedulesAPI;
        this.routesAPI = routesAPI;
    }

    public List<Flight> getInterconnections(String departure, String departureDateTime,
                                            String arrival, String arrivalDateTime) throws JSONException {

        //TODO: Refactor this to use DateTimeFormatter
        String[] splitDepartureDateTime = departureDateTime.split("-");
        String year = splitDepartureDateTime[0];
        String month = splitDepartureDateTime[1];

        //TODO: Filter flights by direct or interconnected, where interconnected flight:
        //      - max 1 stop
        //      - with 2h min difference between arrival and next departure
        //      Filter flights by time:
        //      - flight.departureTime() < departureDateTime
        //      - flight.arrivalTime() > arrivalDateTime

        return schedulesAPI.getSchedule(departure, arrival, year, month);
    }

}
