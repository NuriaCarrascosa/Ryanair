package com.example.ryanair.service;

import com.example.ryanair.api.RoutesAPI;
import com.example.ryanair.api.SchedulesAPI;
import com.example.ryanair.model.Flight;
import com.example.ryanair.model.Route;
import org.json.JSONException;
import org.springframework.stereotype.Service;

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
        //TODO: Filter flights by direct or interconnected, where interconnected flight:
        //      - max 1 stop
        //      - with 2h min difference between arrival and next departure
        //      Filter flights by time:
        //      - flight.departureTime() < departureDateTime
        //      - flight.arrivalTime() > arrivalDateTime

        //TODO: Refactor this to use DateTimeFormatter
        String[] splitDepartureDateTime = departureDateTime.split("-");
        String year = splitDepartureDateTime[0];
        String month = splitDepartureDateTime[1];

        List<Flight> flightScheduleList = schedulesAPI.getSchedules(departure, arrival, year, month);

        List<Route> routeList = routesAPI.getRoutes();

        System.out.println(routeList);

        return flightScheduleList;
    }

}
