package com.example.ryanair.service;

import com.example.ryanair.client.RoutesClient;
import com.example.ryanair.client.SchedulesClient;
import com.example.ryanair.model.Flight;
import com.example.ryanair.model.Route;
import com.example.ryanair.model.request.FlightRequest;
import com.example.ryanair.model.response.DirectFlightResponse;
import com.example.ryanair.model.response.FlightInfoResponse;
import com.example.ryanair.model.response.FlightResponse;
import com.example.ryanair.model.response.InterconnectedFlightResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.ryanair.service.InterconnectionsService.MAX_NUM_OF_STOPS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class InterconnectionsServiceTest {

    private static InterconnectionsService testee;
    private static FlightRequest flightsRequest;
    private static FlightResponse directFlightResponse;
    private static FlightResponse interconnectedFlightResponse;
    private static final String FLIGHT_NUMBER = "1926";
    private static final String STOP_AIRPORT = "DUB";
    private static final String DEPARTURE = "MAD";
    private static final String ARRIVAL = "WRO";
    private static final int YEAR = 2023;
    private static final int MONTH = 12;
    private static final int DAY = 31;
    private static final int STOP_DEPARTURE_HOUR = 16;
    private static final int STOP_DEPARTURE_MINUTES = 0;
    private static final int DEPARTURE_HOUR = 14;
    private static final int DEPARTURE_MINUTES = 0;
    private static final int STOP_ARRIVAL_HOUR = 15;
    private static final int STOP_ARRIVAL_MINUTES = 30;
    private static final int ARRIVAL_HOUR = 22;
    private static final int ARRIVAL_MINUTES = 30;
    private static final LocalDateTime STOP_DEPARTURE_DATE_TIME = LocalDateTime.of(YEAR, MONTH, DAY, STOP_DEPARTURE_HOUR, STOP_DEPARTURE_MINUTES).plusDays(1);
    private static final LocalDateTime STOP_ARRIVAL_DATE_TIME = LocalDateTime.of(YEAR, MONTH, DAY, STOP_ARRIVAL_HOUR, STOP_ARRIVAL_MINUTES);
    private static final LocalDateTime DEPARTURE_DATE_TIME = LocalDateTime.of(YEAR, MONTH, DAY, DEPARTURE_HOUR, DEPARTURE_MINUTES);
    private static final LocalDateTime INTER_ARRIVAL_DATE_TIME = LocalDateTime.of(YEAR, MONTH, DAY, ARRIVAL_HOUR, ARRIVAL_MINUTES).plusDays(1);
    private static final LocalDateTime DIRECT_ARRIVAL_DATE_TIME = LocalDateTime.of(YEAR, MONTH, DAY, ARRIVAL_HOUR, ARRIVAL_MINUTES);
    private static final LocalDateTime ARRIVAL_DATE_TIME_REQ = INTER_ARRIVAL_DATE_TIME;



    @Mock
    SchedulesClient schedulesClientMock;

    @Mock
    RoutesClient routesClientMock;

    @BeforeEach
    void initialize(){
        MockitoAnnotations.openMocks(this);
        testee = new InterconnectionsService(schedulesClientMock, routesClientMock);

        flightsRequest = new FlightRequest(DEPARTURE, ARRIVAL, DEPARTURE_DATE_TIME, ARRIVAL_DATE_TIME_REQ);

        FlightInfoResponse directFlightInfo = new FlightInfoResponse(DEPARTURE, ARRIVAL, DEPARTURE_DATE_TIME, DIRECT_ARRIVAL_DATE_TIME);
        directFlightResponse = new DirectFlightResponse(directFlightInfo);

        FlightInfoResponse interconnectedFirstFlightInfo = new FlightInfoResponse(DEPARTURE, STOP_AIRPORT, DEPARTURE_DATE_TIME, STOP_ARRIVAL_DATE_TIME);
        FlightInfoResponse interconnectedSecondFlightInfo = new FlightInfoResponse(STOP_AIRPORT, ARRIVAL, STOP_DEPARTURE_DATE_TIME, INTER_ARRIVAL_DATE_TIME);
        List<FlightInfoResponse> interconnectedFlightLegs = new ArrayList<>();
        interconnectedFlightLegs.add(interconnectedFirstFlightInfo);
        interconnectedFlightLegs.add(interconnectedSecondFlightInfo);
        interconnectedFlightResponse = new InterconnectedFlightResponse(MAX_NUM_OF_STOPS, interconnectedFlightLegs);
    }


    @Test
    void getInterconnections_withAllParameters_returnFlightResponseList() throws RestClientException {
        //Setup
        List<FlightResponse> expectedFlightListResponse = new ArrayList<>();
        expectedFlightListResponse.add(directFlightResponse);
        expectedFlightListResponse.add(interconnectedFlightResponse);

        List<Flight> directFlightList = List.of(new Flight(FLIGHT_NUMBER, DEPARTURE_DATE_TIME, DIRECT_ARRIVAL_DATE_TIME, DEPARTURE, ARRIVAL));

        List<Flight> possibleFirstFlights = List.of(new Flight(FLIGHT_NUMBER, DEPARTURE_DATE_TIME, STOP_ARRIVAL_DATE_TIME, DEPARTURE, STOP_AIRPORT));
        List<Flight> possibleSecondFlights = List.of(new Flight(FLIGHT_NUMBER, STOP_DEPARTURE_DATE_TIME, INTER_ARRIVAL_DATE_TIME, STOP_AIRPORT, ARRIVAL));

        when(schedulesClientMock.getAllSchedules(any())).thenReturn(directFlightList).thenReturn(possibleFirstFlights).thenReturn(possibleSecondFlights);

        List<Route> routeList = List.of(
                Route.builder().airportFrom(DEPARTURE).airportTo(ARRIVAL).build(),
                Route.builder().airportFrom(DEPARTURE).airportTo(STOP_AIRPORT).build(),
                Route.builder().airportFrom(STOP_AIRPORT).airportTo(ARRIVAL).build());

        when(routesClientMock.getAllRoutes()).thenReturn(routeList);


        //Test
        List<FlightResponse> resultFlightListResponse = testee.getInterconnections(flightsRequest);

        //Verify
        assertEquals(expectedFlightListResponse, resultFlightListResponse);
        verify(schedulesClientMock, times(3)).getAllSchedules(any());
        verify(routesClientMock, times(1)).getAllRoutes();
    }

    @Test
    void getInterconnections_withTransferLessThanTwoHours_returnOnlyDirectFlightResponseList() throws RestClientException {
        //Setup
        List<FlightResponse> expectedFlightListResponse = new ArrayList<>();
        expectedFlightListResponse.add(directFlightResponse);

        List<Flight> directFlightList = List.of(new Flight(FLIGHT_NUMBER, DEPARTURE_DATE_TIME, DIRECT_ARRIVAL_DATE_TIME, DEPARTURE, ARRIVAL));

        List<Flight> possibleFirstFlights = List.of(new Flight(FLIGHT_NUMBER, DEPARTURE_DATE_TIME, STOP_ARRIVAL_DATE_TIME, DEPARTURE, STOP_AIRPORT));
        List<Flight> possibleSecondFlights = List.of(new Flight(FLIGHT_NUMBER, STOP_DEPARTURE_DATE_TIME.minusDays(1), INTER_ARRIVAL_DATE_TIME, STOP_AIRPORT, ARRIVAL));

        when(schedulesClientMock.getAllSchedules(any())).thenReturn(directFlightList).thenReturn(possibleFirstFlights).thenReturn(possibleSecondFlights);

        List<Route> routeList = List.of(
                Route.builder().airportFrom(DEPARTURE).airportTo(ARRIVAL).build(),
                Route.builder().airportFrom(DEPARTURE).airportTo(STOP_AIRPORT).build(),
                Route.builder().airportFrom(STOP_AIRPORT).airportTo(ARRIVAL).build());

        when(routesClientMock.getAllRoutes()).thenReturn(routeList);


        //Test
        List<FlightResponse> resultFlightListResponse = testee.getInterconnections(flightsRequest);

        //Verify
        assertEquals(expectedFlightListResponse, resultFlightListResponse);
        verify(schedulesClientMock, times(3)).getAllSchedules(any());
        verify(routesClientMock, times(1)).getAllRoutes();
    }

    @Test
    void getInterconnections_withDepartureDateTimeBeforeRequested_returnEmptyFlightResponseList() throws RestClientException {
        //Setup
        List<FlightResponse> expectedFlightListResponse = new ArrayList<>();

        List<Flight> directFlightList = List.of(new Flight(FLIGHT_NUMBER, DEPARTURE_DATE_TIME.minusDays(1), DIRECT_ARRIVAL_DATE_TIME, DEPARTURE, ARRIVAL));

        when(schedulesClientMock.getAllSchedules(any())).thenReturn(directFlightList);

        List<Route> routeList = List.of(Route.builder().airportFrom(DEPARTURE).airportTo(ARRIVAL).build());

        when(routesClientMock.getAllRoutes()).thenReturn(routeList);

        //Test
        List<FlightResponse> resultFlightListResponse = testee.getInterconnections(flightsRequest);

        //Verify
        assertEquals(expectedFlightListResponse, resultFlightListResponse);
        verify(schedulesClientMock, times(1)).getAllSchedules(any());
        verify(routesClientMock, times(1)).getAllRoutes();
    }

}