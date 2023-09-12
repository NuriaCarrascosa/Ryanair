package com.example.ryanair.client;

import com.example.ryanair.model.Flight;
import com.example.ryanair.model.request.FlightRequest;
import com.example.ryanair.utils.TestUtils;
import lombok.SneakyThrows;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.ryanair.client.SchedulesAPI.BASE_SCHEDULES_API_URI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class SchedulesAPITest {

    private static SchedulesAPI schedulesAPITestee;
    private static FlightRequest interconnectedFlightsRequest;
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
    private static final String URI = BASE_SCHEDULES_API_URI + DEPARTURE + "/" + ARRIVAL + "/years/" + YEAR + "/months/" + MONTH;
    private static final LocalDateTime DEPARTURE_DATE_TIME = LocalDateTime.of(YEAR, MONTH, DAY, DEPARTURE_HOUR, DEPARTURE_MINUTES);
    private static final LocalDateTime ARRIVAL_DATE_TIME = LocalDateTime.of(YEAR, MONTH, DAY, ARRIVAL_HOUR, ARRIVAL_MINUTES);

    @Mock
    private static RestTemplate restTemplateMock;

    @BeforeEach
    void initialize(){
        MockitoAnnotations.openMocks(this);
        schedulesAPITestee = new SchedulesAPI(restTemplateMock);

        interconnectedFlightsRequest = new FlightRequest(DEPARTURE, ARRIVAL, DEPARTURE_DATE_TIME, ARRIVAL_DATE_TIME);
    }

    @SneakyThrows
    @Test
    void getAllSchedules_withAllParameters_returnsFlightList() {
        //Setup
        JSONObject jsonSchedule = (JSONObject) TestUtils.parseJSONFile("schedule-json.txt");
        when(restTemplateMock.getForObject(URI, String.class)).thenReturn(jsonSchedule.toJSONString());

        List<Flight> expectedFlightList = new ArrayList<>();
        expectedFlightList.add(new Flight(FLIGHT_NUMBER,
                LocalDateTime.of(YEAR, MONTH, DAY, DEPARTURE_HOUR, DEPARTURE_MINUTES),
                LocalDateTime.of(YEAR, MONTH, DAY, ARRIVAL_HOUR, ARRIVAL_MINUTES),
                DEPARTURE, ARRIVAL));
        expectedFlightList.add(new Flight(FLIGHT_NUMBER,
                LocalDateTime.of(YEAR, MONTH, 3, 17, 25),
                LocalDateTime.of(YEAR, MONTH, 3, 21, 0),
                DEPARTURE, ARRIVAL));

        //Test
        List<Flight> resultFlightList = schedulesAPITestee.getAllSchedules(interconnectedFlightsRequest);

        //Verify
        assertEquals(expectedFlightList, resultFlightList);
        verify(restTemplateMock, times(1)).getForObject(URI, String.class);
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {
            "invalidFlightNumber-schedule-json.txt",
            "invalidDepartureTime-schedule-json.txt",
            "invalidArrivalTime-schedule-json.txt",
            "invalidDay-schedule-json.txt",
            "missingField-schedule-json.txt"
    })
    void getAllSchedules_withAllParameters_invalidJSONObjects_throwException(String fileName) {
        //Setup
        JSONObject jsonSchedule = (JSONObject) TestUtils.parseJSONFile(fileName);
        when(restTemplateMock.getForObject(URI, String.class)).thenReturn(jsonSchedule.toJSONString());

        //Test
        assertThrows(Exception.class, () -> schedulesAPITestee.getAllSchedules(interconnectedFlightsRequest));

        //Verify
        verify(restTemplateMock, times(1)).getForObject(URI, String.class);
    }

}
