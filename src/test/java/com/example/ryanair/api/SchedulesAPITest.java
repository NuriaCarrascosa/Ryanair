package com.example.ryanair.api;

import com.example.ryanair.model.Flight;
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

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.ryanair.api.SchedulesAPI.baseSchedulesAPIUri;
import static com.example.ryanair.api.SchedulesAPI.hourMinuteFormat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class SchedulesAPITest {

    private static SchedulesAPI schedulesAPITestee;
    private static final int FLIGHT_NUMBER = 1926;
    private static final String DEPARTURE = "DUB";
    private static final String ARRIVAL = "WRO";
    private static final String YEAR = "2023";
    private static final String MONTH = "6";
    private static final String URI = baseSchedulesAPIUri + DEPARTURE + "/" + ARRIVAL + "/years/" + YEAR + "/months/" + MONTH;

    @Mock
    private static RestTemplate restTemplateMock;

    @BeforeEach
    void initialize(){
        MockitoAnnotations.openMocks(this);
        schedulesAPITestee = new SchedulesAPI(restTemplateMock);
    }

    @SneakyThrows
    @Test
    void getSchedule_withAllParameters_returnsFlightList() {
        //Setup
        JSONObject jsonSchedule = (JSONObject) TestUtils.parseJSONFile("schedule-json.txt");
        when(restTemplateMock.getForObject(URI, String.class)).thenReturn(jsonSchedule.toJSONString());

        List<Flight> expectedFlightList = new ArrayList<>();
        expectedFlightList.add(newFlight("18:00", "21:35", 1));
        expectedFlightList.add(newFlight("17:25", "21:00", 3));

        //Test
        List<Flight> resultFlightList = schedulesAPITestee.getSchedules(DEPARTURE, ARRIVAL, YEAR, MONTH);

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
    void getSchedule_withAllParameters_invalidJSONObjects_throwException(String fileName) {
        //Setup
        JSONObject jsonSchedule = (JSONObject) TestUtils.parseJSONFile(fileName);
        when(restTemplateMock.getForObject(URI, String.class)).thenReturn(jsonSchedule.toJSONString());

        //Test
        assertThrows(Exception.class, () -> schedulesAPITestee.getSchedules(DEPARTURE, ARRIVAL, YEAR, MONTH));

        //Verify
        verify(restTemplateMock, times(1)).getForObject(URI, String.class);
    }

    private Flight newFlight(String departureTime, String arrivalTime, int day){
        return new Flight(FLIGHT_NUMBER,
                LocalTime.parse(departureTime, hourMinuteFormat),
                LocalTime.parse(arrivalTime, hourMinuteFormat),
                Integer.parseInt(YEAR),
                Integer.parseInt(MONTH),
                day);
    }

}
