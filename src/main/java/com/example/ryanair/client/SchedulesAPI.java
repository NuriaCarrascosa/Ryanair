package com.example.ryanair.client;

import com.example.ryanair.client.model.ScheduleResponse;
import com.example.ryanair.model.Flight;
import com.example.ryanair.model.InterconnectedFlightsRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Log4j2
public class SchedulesAPI implements InterfazSchedulesAPI {

    public static final String BASE_SCHEDULES_API_URI = "https://services-api.ryanair.com/timtbl/3/schedules/";
    private final RestTemplate restTemplate;

    public SchedulesAPI(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Flight> getAllSchedules(InterconnectedFlightsRequest interconnectedFlightsRequest) throws RestClientException {

        List<Flight> flightList = getResponse(interconnectedFlightsRequest).toFlightList(interconnectedFlightsRequest.getDepartureDateTimeRequested().getYear());

        return flightList.stream().peek(flight -> {
            flight.setArrival(interconnectedFlightsRequest.getArrival());
            flight.setDeparture(interconnectedFlightsRequest.getDeparture());
        }).toList();
    }

    /*
    public List<Flight> getSchedulesByDay(InterconnectedFlightsRequest interconnectedFlightsRequest, int day) throws JSONException {
        List<Flight> flightList = getAllSchedules(interconnectedFlightsRequest);
        return flightList.stream().filter(flight -> flight.getArrivalDateTime().getDayOfMonth()==day).toList();
    }
     */

    private ScheduleResponse getResponse(InterconnectedFlightsRequest interconnectedFlightsRequest) throws RestClientException {
        String uri = BASE_SCHEDULES_API_URI
                + interconnectedFlightsRequest.getDeparture() + "/"
                + interconnectedFlightsRequest.getArrival()
                + "/years/" + interconnectedFlightsRequest.getDepartureDateTimeRequested().getYear()
                + "/months/" + interconnectedFlightsRequest.getDepartureDateTimeRequested().getMonthValue();

        return restTemplate.getForObject(uri, ScheduleResponse.class);
    }

}
