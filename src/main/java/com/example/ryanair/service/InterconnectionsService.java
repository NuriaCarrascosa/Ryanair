package com.example.ryanair.service;

import com.example.ryanair.api.RoutesAPI;
import com.example.ryanair.api.SchedulesAPI;
import com.example.ryanair.model.Flight;
import com.example.ryanair.model.InterconnectedFlight;
import com.example.ryanair.model.Route;
import org.json.JSONException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class InterconnectionsService {

    private final SchedulesAPI schedulesAPI;
    private final RoutesAPI routesAPI;
    private static final long MINIMUM_HOURS_OF_STOP = 2;
    public static final DateTimeFormatter ISO_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd'T'HH:mm", Locale.ENGLISH);

    public InterconnectionsService(SchedulesAPI schedulesAPI, RoutesAPI routesAPI) {
        this.schedulesAPI = schedulesAPI;
        this.routesAPI = routesAPI;
    }

    public List<Flight> getInterconnections(String departure, String departureISODateTime,
                                            String arrival, String arrivalISODateTime) throws JSONException {
        LocalDateTime departureDateTime = LocalDateTime.parse(departureISODateTime, ISO_DATE_TIME_FORMAT);
        LocalDateTime arrivalDateTime = LocalDateTime.parse(arrivalISODateTime, ISO_DATE_TIME_FORMAT);

        List<Flight> directFlightList = getDirectFlights(departure, arrival, departureDateTime, arrivalDateTime);
        List<InterconnectedFlight> interconnectedFlights = getInterconnectedFlights(departure, arrival, departureDateTime, arrivalDateTime);

        return directFlightList;
    }

    public List<Flight> getDirectFlights(String departure, String arrival,
                                         LocalDateTime departureDateTimeRequested,
                                         LocalDateTime arrivalDateTimeRequested) throws JSONException {
        List<Flight> flightScheduleList = schedulesAPI.getAllSchedules(departure, arrival,
                departureDateTimeRequested.getYear(), departureDateTimeRequested.getMonthValue());

        return flightScheduleList.stream().filter(flight ->
                !flight.getDepartureDateTime().isBefore(departureDateTimeRequested) &&
                        !flight.getArrivalDateTime().isAfter(arrivalDateTimeRequested)).toList();
    }


    public List<InterconnectedFlight> getInterconnectedFlights(String departure, String arrival,
                                                               LocalDateTime departureDateTimeRequested,
                                                               LocalDateTime arrivalDateTimeRequested) throws JSONException {

        List<InterconnectedFlight> interconnectedFlightList = new ArrayList<>();
        List<Route> routeList = routesAPI.getAllRoutes();

        // connectedAirports is a list of all the airports' IATA codes of the possible interconnected flights.
        List<String> connectedAirports = routeList.stream().filter(route -> route.getAirportFrom().equals(departure) && routeList.stream()
                .anyMatch(route1 -> route1.getAirportFrom().equals(route.getAirportTo()) && route1.getAirportTo().equals(arrival)))
                .map(Route::getAirportTo).toList();

        for (String stopAirport : connectedAirports) {
            List<Flight> possibleDepartureAirportStop;
            List<Flight> possibleArrivalAirportStop = schedulesAPI.getSchedulesByDay(departure, stopAirport,
                    departureDateTimeRequested.getYear(), departureDateTimeRequested.getMonthValue(), departureDateTimeRequested.getDayOfMonth());

            if(!possibleArrivalAirportStop.isEmpty()){
                possibleDepartureAirportStop = schedulesAPI.getSchedulesByDay(stopAirport, arrival,
                        departureDateTimeRequested.getYear(), departureDateTimeRequested.getMonthValue(), departureDateTimeRequested.getDayOfMonth());

                for (Flight firstFlight : possibleArrivalAirportStop) {
                    for (Flight secondFlight : possibleDepartureAirportStop){
                        if(!firstFlight.getDepartureDateTime().isBefore(departureDateTimeRequested)
                                && !secondFlight.getArrivalDateTime().isAfter(arrivalDateTimeRequested)){
                            if(!secondFlight.getDepartureDateTime().isBefore(firstFlight.getArrivalDateTime().plusHours(MINIMUM_HOURS_OF_STOP))){
                                interconnectedFlightList.add(new InterconnectedFlight(
                                        firstFlight.getFlightNumber(),
                                        firstFlight.getDepartureDateTime(),
                                        secondFlight.getArrivalDateTime(),
                                        firstFlight.getDeparture(),
                                        secondFlight.getArrival(),
                                        secondFlight.getFlightNumber(),
                                        firstFlight.getArrival(),
                                        firstFlight.getArrivalDateTime(),
                                        secondFlight.getDepartureDateTime()));
                            }
                        }
                    }
                }

            }

        }

        return interconnectedFlightList;
    }

}
