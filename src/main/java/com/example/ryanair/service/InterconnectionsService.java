package com.example.ryanair.service;

import com.example.ryanair.client.InterfazRoutesAPI;
import com.example.ryanair.client.InterfazSchedulesAPI;
import com.example.ryanair.model.*;
import com.example.ryanair.model.response.*;
import org.json.JSONException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.example.ryanair.model.response.FlightInfoResponse.parseFlightToFlightInfoResponse;

@Service
public class InterconnectionsService {

    private final InterfazSchedulesAPI schedulesAPI;
    private final InterfazRoutesAPI routesAPI;
    private static final long MINIMUM_HOURS_OF_STOP = 2;


    public InterconnectionsService(InterfazSchedulesAPI schedulesAPI, InterfazRoutesAPI routesAPI) {
        this.schedulesAPI = schedulesAPI;
        this.routesAPI = routesAPI;
    }

    public InterconnectionsResponse getInterconnections(InterconnectedFlightsRequest interconnectedFlightsRequest) throws JSONException {

        List<Flight> directFlightList = getDirectFlights(interconnectedFlightsRequest);

        List<InterconnectedFlight> interconnectedFlights = getInterconnectedFlights(interconnectedFlightsRequest);

        List<FlightInfoResponse> directFlightInfoResponseList = parseFlightListToFlightInfoResponseList(directFlightList);

        FlightsResponse directFlightsResponse = new DirectFlightsResponse(directFlightInfoResponseList);

        List<FlightInfoResponse> interconnectedFlightInfoResponseList = parseInterconnectedFlightListToFlightInfoResponseList(interconnectedFlights);

        FlightsResponse interconnectedFlightsResponse = new InterconnectedFlightsResponse(1, interconnectedFlightInfoResponseList);

        List<FlightsResponse> listResponse = new ArrayList<>();
        listResponse.add(directFlightsResponse);
        listResponse.add(interconnectedFlightsResponse);

        return new InterconnectionsResponse(listResponse);
    }

    private List<Flight> getDirectFlights(InterconnectedFlightsRequest interconnectedFlightsRequest) throws JSONException {
        List<Flight> flightScheduleList = schedulesAPI.getAllSchedules(interconnectedFlightsRequest);

        return flightScheduleList.stream().filter(flight ->
                !flight.getDepartureDateTime().isBefore(interconnectedFlightsRequest.getDepartureDateTimeRequested()) &&
                        !flight.getArrivalDateTime().isAfter(interconnectedFlightsRequest.getArrivalDateTimeRequested())).toList();
    }


    private List<InterconnectedFlight> getInterconnectedFlights(InterconnectedFlightsRequest interconnectedFlightsRequest) throws JSONException {

        List<InterconnectedFlight> interconnectedFlightList = new ArrayList<>();

        List<String> connectedAirports = getConnectedAirports(interconnectedFlightsRequest);

        for (String stopAirport : connectedAirports) {

            InterconnectedFlightsRequest firstFlightsRequest = new InterconnectedFlightsRequest(
                    interconnectedFlightsRequest.getDeparture(),
                    stopAirport,
                    interconnectedFlightsRequest.getDepartureDateTimeRequested(),
                    interconnectedFlightsRequest.getArrivalDateTimeRequested());

            List<Flight> possibleArrivalAirportStop = schedulesAPI.getAllSchedules(firstFlightsRequest);

            List<Flight> possibleDepartureAirportStop;

            if(!possibleArrivalAirportStop.isEmpty()){
                InterconnectedFlightsRequest secondFlightsRequest = new InterconnectedFlightsRequest(
                        stopAirport,
                        interconnectedFlightsRequest.getArrival(),
                        interconnectedFlightsRequest.getDepartureDateTimeRequested(),
                        interconnectedFlightsRequest.getArrivalDateTimeRequested());

                possibleDepartureAirportStop = schedulesAPI.getAllSchedules(secondFlightsRequest);

                interconnectedFlightList = getInterconnectedFlightsByStop(interconnectedFlightsRequest, interconnectedFlightList, possibleArrivalAirportStop, possibleDepartureAirportStop);

            }

        }

        return interconnectedFlightList;
    }

    private List<InterconnectedFlight> getInterconnectedFlightsByStop(InterconnectedFlightsRequest interconnectedFlightsRequest,
                                                                      List<InterconnectedFlight> interconnectedFlightList,
                                                                      List<Flight> possibleArrivalAirportStop,
                                                                      List<Flight> possibleDepartureAirportStop) {
        for (Flight firstFlight : possibleArrivalAirportStop) {

            if(!firstFlight.getDepartureDateTime().isBefore(interconnectedFlightsRequest.getDepartureDateTimeRequested())){

                for (Flight secondFlight : possibleDepartureAirportStop){

                    if(!secondFlight.getArrivalDateTime().isAfter(interconnectedFlightsRequest.getArrivalDateTimeRequested())){

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

        return interconnectedFlightList;
    }

    private List<String> getConnectedAirports(InterconnectedFlightsRequest interconnectedFlightsRequest) throws JSONException {
        List<Route> routeList = routesAPI.getAllRoutes();

        System.out.println(routeList);

        // connectedAirports is a list of all the airports' IATA codes of the possible interconnected flights.
        List<String> connectedAirports = routeList.stream().filter(route -> route.getAirportFrom().equals(interconnectedFlightsRequest.getDeparture()) && routeList.stream()
                .anyMatch(route1 -> route1.getAirportFrom().equals(route.getAirportTo()) && route1.getAirportTo().equals(interconnectedFlightsRequest.getArrival())))
                .map(Route::getAirportTo).toList();
        return connectedAirports;
    }

    private List<FlightInfoResponse> parseFlightListToFlightInfoResponseList(List<Flight> flightList){
        List<FlightInfoResponse> flightInfoResponseList = new ArrayList<>();

        for (Flight flight : flightList){
            flightInfoResponseList.add(parseFlightToFlightInfoResponse(flight));
        }

        return flightInfoResponseList;
    }

    private List<FlightInfoResponse> parseInterconnectedFlightListToFlightInfoResponseList(List<InterconnectedFlight> interconnectedFlights) {
        List<FlightInfoResponse> flightInfoResponseList = new ArrayList<>();

        for (InterconnectedFlight interconnectedFlight : interconnectedFlights) {
            Flight firstFlight = new Flight(
                    interconnectedFlight.getFlightNumber(),
                    interconnectedFlight.getDepartureDateTime(),
                    interconnectedFlight.getStopArrivalDateTime(),
                    interconnectedFlight.getDeparture(),
                    interconnectedFlight.getStopAirport());
            flightInfoResponseList.add(parseFlightToFlightInfoResponse(firstFlight));

            Flight secondFlight = new Flight(
                    interconnectedFlight.getSecondFlightNumber(),
                    interconnectedFlight.getStopDepartureDateTime(),
                    interconnectedFlight.getArrivalDateTime(),
                    interconnectedFlight.getStopAirport(),
                    interconnectedFlight.getArrival());
            flightInfoResponseList.add(parseFlightToFlightInfoResponse(secondFlight));
        }

        return flightInfoResponseList;
    }

}
