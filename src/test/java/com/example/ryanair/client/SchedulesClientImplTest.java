package com.example.ryanair.client;

import com.example.ryanair.client.model.FlightPerDay;
import com.example.ryanair.client.model.FlightSchedule;
import com.example.ryanair.client.model.ScheduleResponse;
import com.example.ryanair.model.Flight;
import com.example.ryanair.model.request.FlightRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.ryanair.client.SchedulesClientImpl.BASE_SCHEDULES_CLIENT_URI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class SchedulesClientImplTest {

    private static SchedulesClientImpl testee;
    private static FlightRequest flightRequest;
    private static ScheduleResponse scheduleResponse;
    private static final String FLIGHT_NUMBER = "1926";
    private static final String DEPARTURE = "DUB";
    private static final String ARRIVAL = "WRO";
    private static final int YEAR = 2023;
    private static final int MONTH = 6;
    private static final int DAY = 1;
    private static final int DEPARTURE_HOUR = 18;
    private static final int DEPARTURE_MINUTES = 0;
    private static final int ARRIVAL_HOUR = 21;
    private static final int ARRIVAL_MINUTES = 35;
    private static final String URI = BASE_SCHEDULES_CLIENT_URI + DEPARTURE + "/" + ARRIVAL + "/years/" + YEAR + "/months/" + MONTH;
    private static final LocalDateTime DEPARTURE_DATE_TIME = LocalDateTime.of(YEAR, MONTH, DAY, DEPARTURE_HOUR, DEPARTURE_MINUTES);
    private static final LocalDateTime ARRIVAL_DATE_TIME = LocalDateTime.of(YEAR, MONTH, DAY, ARRIVAL_HOUR, ARRIVAL_MINUTES);

    @Mock
    private static RestTemplate restTemplateMock;

    @BeforeEach
    void initialize(){
        MockitoAnnotations.openMocks(this);
        testee = new SchedulesClientImpl(restTemplateMock);

        flightRequest = new FlightRequest(DEPARTURE, ARRIVAL, DEPARTURE_DATE_TIME, ARRIVAL_DATE_TIME);

        FlightSchedule flightSchedule = new FlightSchedule(FLIGHT_NUMBER, LocalTime.of(DEPARTURE_HOUR, DEPARTURE_MINUTES), LocalTime.of(ARRIVAL_HOUR, ARRIVAL_MINUTES));
        FlightPerDay flightPerDay = new FlightPerDay(DAY, List.of(flightSchedule));
        List<FlightPerDay> flightPerDayList = new ArrayList<>();
        flightPerDayList.add(flightPerDay);
        scheduleResponse = new ScheduleResponse(MONTH, flightPerDayList);
    }


    @Test
    void getAllSchedules_withAllParameters_returnsFlightList() {
        //Setup
        when(restTemplateMock.getForObject(URI, ScheduleResponse.class)).thenReturn(scheduleResponse);

        List<Flight> expectedFlightList = new ArrayList<>();
        expectedFlightList.add(new Flight(FLIGHT_NUMBER,
                LocalDateTime.of(YEAR, MONTH, DAY, DEPARTURE_HOUR, DEPARTURE_MINUTES),
                LocalDateTime.of(YEAR, MONTH, DAY, ARRIVAL_HOUR, ARRIVAL_MINUTES),
                DEPARTURE, ARRIVAL));

        //Test
        List<Flight> resultFlightList = testee.getAllSchedules(flightRequest);

        //Verify
        assertEquals(expectedFlightList, resultFlightList);
        verify(restTemplateMock, times(1)).getForObject(URI, ScheduleResponse.class);
    }


    @Test
    void getAllSchedules_withAllParameters_throwException() {
        //Setup
        when(restTemplateMock.getForObject(URI, ScheduleResponse.class)).thenThrow(RestClientException.class);

        //Test
        assertThrows(RestClientException.class, () -> testee.getAllSchedules(flightRequest));

        //Verify
        verify(restTemplateMock, times(1)).getForObject(URI, ScheduleResponse.class);
    }

}
