package com.example.ryanair.service;

import com.example.ryanair.client.RoutesClient;
import com.example.ryanair.client.SchedulesClient;
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

public class InterconnectionsServiceTest {

    private static InterconnectionsService testee;
    private static FlightRequest flightsRequest;
    private static FlightResponse directFlightResponse;
    private static FlightResponse interconnectedFlightResponse;
    private static List<FlightResponse> flightResponseList;
    private static FlightInfoResponse directFlightInfo;
    private static FlightInfoResponse interconnectedFirstFlightInfo;
    private static FlightInfoResponse interconnectedSecondFlightInfo;
    private static List<FlightInfoResponse> interconnectedFlightLegs;
    private static final String STOP_AIRPORT = "STN";
    private static final String DEPARTURE = "DUB";
    private static final String ARRIVAL = "WRO";
    private static final int YEAR = 2023;
    private static final int MONTH = 12;
    private static final int DAY = 1;
    private static final int STOP_DEPARTURE_HOUR = 18;
    private static final int STOP_DEPARTURE_MINUTES = 0;
    private static final int DEPARTURE_HOUR = 14;
    private static final int DEPARTURE_MINUTES = 0;
    private static final int STOP_ARRIVAL_HOUR = 15;
    private static final int STOP_ARRIVAL_MINUTES = 30;
    private static final int ARRIVAL_HOUR = 22;
    private static final int ARRIVAL_MINUTES = 30;
    private static final LocalDateTime STOP_DEPARTURE_DATE_TIME = LocalDateTime.of(YEAR, MONTH, DAY, STOP_DEPARTURE_HOUR, STOP_DEPARTURE_MINUTES);
    private static final LocalDateTime STOP_ARRIVAL_DATE_TIME = LocalDateTime.of(YEAR, MONTH, DAY, STOP_ARRIVAL_HOUR, STOP_ARRIVAL_MINUTES);
    private static final LocalDateTime DEPARTURE_DATE_TIME = LocalDateTime.of(YEAR, MONTH, DAY, DEPARTURE_HOUR, DEPARTURE_MINUTES);
    private static final LocalDateTime ARRIVAL_DATE_TIME = LocalDateTime.of(YEAR, MONTH, DAY, ARRIVAL_HOUR, ARRIVAL_MINUTES);

    @Mock
    SchedulesClient schedulesClientMock;

    @Mock
    RoutesClient routesClientMock;

    @BeforeEach
    void initialize(){
        MockitoAnnotations.openMocks(this);
        testee = new InterconnectionsService(schedulesClientMock, routesClientMock);

        flightsRequest = new FlightRequest(DEPARTURE, ARRIVAL, DEPARTURE_DATE_TIME, ARRIVAL_DATE_TIME);

        directFlightInfo = new FlightInfoResponse(DEPARTURE, ARRIVAL, DEPARTURE_DATE_TIME, ARRIVAL_DATE_TIME);
        directFlightResponse = new DirectFlightResponse(directFlightInfo);

        interconnectedFirstFlightInfo = new FlightInfoResponse(DEPARTURE, STOP_AIRPORT, DEPARTURE_DATE_TIME, STOP_ARRIVAL_DATE_TIME);
        interconnectedSecondFlightInfo = new FlightInfoResponse(STOP_AIRPORT, ARRIVAL, STOP_DEPARTURE_DATE_TIME, ARRIVAL_DATE_TIME);
        interconnectedFlightLegs = new ArrayList<>();
        interconnectedFlightLegs.add(interconnectedFirstFlightInfo);
        interconnectedFlightLegs.add(interconnectedSecondFlightInfo);
        interconnectedFlightResponse = new InterconnectedFlightResponse(MAX_NUM_OF_STOPS, interconnectedFlightLegs);

        flightResponseList = new ArrayList<>();
        flightResponseList.add(directFlightResponse);
        flightResponseList.add(interconnectedFlightResponse);
    }


    @Test
    void getInterconnections_withAllParameters_returnFlightResponseList() throws RestClientException {
        //Setup
        List<FlightResponse> expectedFlightListResponse = new ArrayList<>();

        //Test
        List<FlightResponse> resultFlightListResponse = testee.getInterconnections(flightsRequest);

        //Verify
        assertEquals(expectedFlightListResponse, resultFlightListResponse);


    }

}