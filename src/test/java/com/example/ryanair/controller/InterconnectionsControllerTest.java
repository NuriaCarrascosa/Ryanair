package com.example.ryanair.controller;

import com.example.ryanair.model.request.FlightRequest;
import com.example.ryanair.model.response.DirectFlightResponse;
import com.example.ryanair.model.response.FlightInfoResponse;
import com.example.ryanair.model.response.FlightResponse;
import com.example.ryanair.model.response.InterconnectedFlightResponse;
import com.example.ryanair.service.InterconnectionsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.example.ryanair.service.InterconnectionsService.MAX_NUM_OF_STOPS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


class InterconnectionsControllerTest {

    private static InterconnectionsController testee;
    private static FlightRequest flightRequest;
    private static List<FlightResponse> flightResponseList;
    private static final String STOP_AIRPORT = "STN";
    private static final String DEPARTURE = "DUB";
    private static final String ARRIVAL = "WRO";
    private static final int YEAR = 2023;
    private static final int MONTH = 12;
    private static final int DAY = 11;
    private static final int STOP_DEPARTURE_HOUR = 18;
    private static final int STOP_DEPARTURE_MINUTES = 10;
    private static final int DEPARTURE_HOUR = 14;
    private static final int DEPARTURE_MINUTES = 40;
    private static final int STOP_ARRIVAL_HOUR = 15;
    private static final int STOP_ARRIVAL_MINUTES = 30;
    private static final int ARRIVAL_HOUR = 22;
    private static final int ARRIVAL_MINUTES = 30;
    private static final LocalDateTime STOP_DEPARTURE_DATE_TIME = LocalDateTime.of(YEAR, MONTH, DAY, STOP_DEPARTURE_HOUR, STOP_DEPARTURE_MINUTES);
    private static final LocalDateTime STOP_ARRIVAL_DATE_TIME = LocalDateTime.of(YEAR, MONTH, DAY, STOP_ARRIVAL_HOUR, STOP_ARRIVAL_MINUTES);
    private static final LocalDateTime DEPARTURE_DATE_TIME = LocalDateTime.of(YEAR, MONTH, DAY, DEPARTURE_HOUR, DEPARTURE_MINUTES);
    private static final LocalDateTime ARRIVAL_DATE_TIME = LocalDateTime.of(YEAR, MONTH, DAY, ARRIVAL_HOUR, ARRIVAL_MINUTES);
    private static final String DEPARTURE_DATE_TIME_INPUT = YEAR + "-" + MONTH + "-" + DAY + "T" + DEPARTURE_HOUR + ":" + DEPARTURE_MINUTES;
    private static final String ARRIVAL_DATE_TIME_INPUT = YEAR + "-" + MONTH + "-" + DAY + "T" + ARRIVAL_HOUR + ":" + ARRIVAL_MINUTES;

    @Mock
    private static InterconnectionsService interconnectionsServiceMock;

    @BeforeEach
    void initialize(){
        MockitoAnnotations.openMocks(this);
        testee = new InterconnectionsController(interconnectionsServiceMock);

        flightRequest = new FlightRequest(DEPARTURE, ARRIVAL, DEPARTURE_DATE_TIME, ARRIVAL_DATE_TIME);

        FlightInfoResponse directFlightInfo = new FlightInfoResponse(DEPARTURE, ARRIVAL, DEPARTURE_DATE_TIME, ARRIVAL_DATE_TIME);
        FlightResponse directFlightResponse = new DirectFlightResponse(directFlightInfo);

        FlightInfoResponse interconnectedFirstFlightInfo = new FlightInfoResponse(DEPARTURE, STOP_AIRPORT, DEPARTURE_DATE_TIME, STOP_ARRIVAL_DATE_TIME);
        FlightInfoResponse interconnectedSecondFlightInfo = new FlightInfoResponse(STOP_AIRPORT, ARRIVAL, STOP_DEPARTURE_DATE_TIME, ARRIVAL_DATE_TIME);
        List<FlightInfoResponse> interconnectedFlightLegs = new ArrayList<>();
        interconnectedFlightLegs.add(interconnectedFirstFlightInfo);
        interconnectedFlightLegs.add(interconnectedSecondFlightInfo);
        FlightResponse interconnectedFlightResponse = new InterconnectedFlightResponse(MAX_NUM_OF_STOPS, interconnectedFlightLegs);

        flightResponseList = new ArrayList<>();
        flightResponseList.add(directFlightResponse);
        flightResponseList.add(interconnectedFlightResponse);
    }

    @Test
    void getInterconnections_withAllParameters_returnFlightResponseList() {
        //Setup
        when(interconnectionsServiceMock.getInterconnections(flightRequest)).thenReturn(flightResponseList);

        //Test
        List<FlightResponse> flightResponseListResult = testee.getInterconnections(DEPARTURE, ARRIVAL, DEPARTURE_DATE_TIME_INPUT, ARRIVAL_DATE_TIME_INPUT);

        //Verification
        assertEquals(flightResponseList, flightResponseListResult);
        verify(interconnectionsServiceMock, times(1)).getInterconnections(flightRequest);
    }

    @ParameterizedTest
    @MethodSource("setExceptions")
    void getInterconnections_withAllParameters_throwsException(Exception exception) {
        //Setup
        when(interconnectionsServiceMock.getInterconnections(flightRequest)).thenThrow(exception);

        //Test and verification
        ResponseStatusException thrown = assertThrows(ResponseStatusException.class, () -> testee.getInterconnections(DEPARTURE, ARRIVAL, DEPARTURE_DATE_TIME_INPUT, ARRIVAL_DATE_TIME_INPUT));
        assertEquals(thrown.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        verify(interconnectionsServiceMock, times(1)).getInterconnections(flightRequest);
    }

    @Test
    void getInterconnections_withInvalidIATACodeParameters_throwsInvalidInputException(){
        //Setup
        String invalidArrival = "Madrid";

        //Test and verification
        ResponseStatusException thrown = assertThrows(ResponseStatusException.class, () -> testee.getInterconnections(DEPARTURE, invalidArrival, DEPARTURE_DATE_TIME_INPUT, ARRIVAL_DATE_TIME_INPUT));
        assertEquals(thrown.getStatusCode(), HttpStatus.BAD_REQUEST);
        verify(interconnectionsServiceMock, times(0)).getInterconnections(flightRequest);
    }

    @Test
    void getInterconnections_withInvalidDateTimeParameters_throwsInvalidInputException(){
        //Setup
        String invalid_departure_date_time = "0" + "-" + MONTH + "-" + DAY + "T" + DEPARTURE_HOUR + ":" + DEPARTURE_MINUTES;

        //Test and verification
        ResponseStatusException thrown = assertThrows(ResponseStatusException.class, () -> testee.getInterconnections(DEPARTURE, ARRIVAL, invalid_departure_date_time, ARRIVAL_DATE_TIME_INPUT));
        assertEquals(thrown.getStatusCode(), HttpStatus.BAD_REQUEST);
        verify(interconnectionsServiceMock, times(0)).getInterconnections(flightRequest);
    }

    private static Stream<Arguments> setExceptions(){
        return Stream.of(
                Arguments.of(
                        new RestClientException("RestClientException msg")
                ),
                Arguments.of(
                        new NullPointerException("NullPointerException msg")
                )
        );

    }


}

