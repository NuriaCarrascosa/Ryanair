package com.example.ryanair.service;

import com.example.ryanair.client.InterfazRoutesAPI;
import com.example.ryanair.client.InterfazSchedulesAPI;
import com.example.ryanair.model.Flight;
import com.example.ryanair.model.FlightRequest;
import com.example.ryanair.model.InterconnectedFlight;
import com.example.ryanair.model.Route;
import com.example.ryanair.model.response.DirectFlightResponse;
import com.example.ryanair.model.response.FlightInfoResponse;
import com.example.ryanair.model.response.FlightResponse;
import com.example.ryanair.model.response.InterconnectedFlightResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;

import static com.example.ryanair.model.response.FlightInfoResponse.parseFlightToFlightInfoResponse;

@Service
public class InterconnectionsService {

    private final InterfazSchedulesAPI schedulesAPI;
    private final InterfazRoutesAPI routesAPI;
    private static final long MINIMUM_HOURS_OF_STOP = 2;
    public final static int MAX_NUM_OF_STOPS = 1;
    public final static int MAX_NUM_OF_INTERCONNECTED_FLIGHTS = MAX_NUM_OF_STOPS + 1;

    public InterconnectionsService(InterfazSchedulesAPI schedulesAPI, InterfazRoutesAPI routesAPI) {
        this.schedulesAPI = schedulesAPI;
        this.routesAPI = routesAPI;
    }


    public List<FlightResponse> getInterconnections(FlightRequest flightsRequest) throws RestClientException {

        List<Flight> directFlightList = getDirectFlights(flightsRequest);
        List<InterconnectedFlight> interconnectedFlights = getInterconnectedFlights(flightsRequest);

        List<FlightResponse> flightsResponseList = toDirectFlightResponseList(directFlightList);
        List<FlightResponse> interconnectedFlightsResponseList = toInterconnectedFlightResponseList(interconnectedFlights);

        flightsResponseList.addAll(interconnectedFlightsResponseList);

        return flightsResponseList;
    }


    private List<Flight> getDirectFlights(FlightRequest flightsRequest) throws RestClientException {
        List<Flight> flightScheduleList = schedulesAPI.getAllSchedules(flightsRequest);

        return flightScheduleList.stream().filter(flight ->
                !flight.getDepartureDateTime().isBefore(flightsRequest.getDepartureDateTimeRequested()) &&
                        !flight.getArrivalDateTime().isAfter(flightsRequest.getArrivalDateTimeRequested())).toList();
    }


    private List<InterconnectedFlight> getInterconnectedFlights(FlightRequest flightsRequest) throws RestClientException {

        List<String> connectedAirports = getConnectedAirports(flightsRequest);
        List<InterconnectedFlight> interconnectedFlightList = new ArrayList<>();

        for (String stopAirport : connectedAirports) {

            FlightRequest firstFlightsRequest = new FlightRequest(
                    flightsRequest.getDeparture(),
                    stopAirport,
                    flightsRequest.getDepartureDateTimeRequested(),
                    flightsRequest.getArrivalDateTimeRequested());

            List<Flight> possibleFirstFlights = schedulesAPI.getAllSchedulesFromDepartureToArrival(firstFlightsRequest);

            if(!possibleFirstFlights.isEmpty()){

                FlightRequest secondFlightsRequest = new FlightRequest(
                        stopAirport,
                        flightsRequest.getArrival(),
                        flightsRequest.getDepartureDateTimeRequested(),
                        flightsRequest.getArrivalDateTimeRequested());

                List<Flight> possibleSecondFlights = schedulesAPI.getAllSchedulesFromDepartureToArrival(secondFlightsRequest);

                interconnectedFlightList.addAll(getInterconnectedFlightsByStop(flightsRequest, possibleFirstFlights, possibleSecondFlights));

            }
        }

        return interconnectedFlightList;
    }

    private List<InterconnectedFlight> getInterconnectedFlightsByStop(FlightRequest flightsRequest,
                                                                      List<Flight> possibleFirstFlights,
                                                                      List<Flight> possibleSecondFlights) {

        List<InterconnectedFlight> interconnectedFlightList = new ArrayList<>();

        for (Flight firstFlight : possibleFirstFlights) {

            if(!firstFlight.getDepartureDateTime().isBefore(flightsRequest.getDepartureDateTimeRequested())){

                for (Flight secondFlight : possibleSecondFlights){

                    if(!secondFlight.getArrivalDateTime().isAfter(flightsRequest.getArrivalDateTimeRequested())){

                        if(!secondFlight.getDepartureDateTime().isBefore(firstFlight.getArrivalDateTime().plusHours(MINIMUM_HOURS_OF_STOP))){

                            List<Flight> flightsInterconnected = new ArrayList<>(MAX_NUM_OF_INTERCONNECTED_FLIGHTS);
                            flightsInterconnected.add(firstFlight);
                            flightsInterconnected.add(secondFlight);

                            interconnectedFlightList.add(new InterconnectedFlight(flightsInterconnected));
                        }
                    }
                }
            }
        }

        return interconnectedFlightList;
    }

    // getConnectedAirports returns a list of all the airports' IATA codes of the possible interconnected flights.

    /**
     * Gets a list of all the airports' IATA codes that can be a stop in possible interconnected flights.
     * @param flightsRequest
     * @return List<String>
     * @throws RestClientException
     */
    private List<String> getConnectedAirports(FlightRequest flightsRequest) throws RestClientException {
        List<Route> routeList = routesAPI.getAllRoutes();

        return routeList.stream().filter(route -> route.getAirportFrom().equals(flightsRequest.getDeparture()) && routeList.stream()
                .anyMatch(route1 -> route1.getAirportFrom().equals(route.getAirportTo()) && route1.getAirportTo().equals(flightsRequest.getArrival())))
                .map(Route::getAirportTo).toList();
    }

    private List<FlightResponse> toDirectFlightResponseList(List<Flight> flightList){
        List<FlightResponse> directFlightsResponseList = new ArrayList<>();

        for (Flight flight : flightList){
            directFlightsResponseList.add(new DirectFlightResponse(parseFlightToFlightInfoResponse(flight)));
        }

        return directFlightsResponseList;
    }

    private List<FlightResponse> toInterconnectedFlightResponseList(List<InterconnectedFlight> interconnectedFlights) {
        List<FlightResponse> flightInfoResponseList = new ArrayList<>();

        for (InterconnectedFlight interconnectedFlight : interconnectedFlights) {
            List<FlightInfoResponse> interconnectedFlightInfoResponse = new ArrayList<>(MAX_NUM_OF_INTERCONNECTED_FLIGHTS);

            for (int i = 0; i < MAX_NUM_OF_INTERCONNECTED_FLIGHTS; i++){
                interconnectedFlightInfoResponse.add(parseFlightToFlightInfoResponse(interconnectedFlight.getFlight(i)));
            }

            flightInfoResponseList.add(new InterconnectedFlightResponse(MAX_NUM_OF_STOPS, interconnectedFlightInfoResponse));
        }

        return flightInfoResponseList;
    }

}
