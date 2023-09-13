package com.example.ryanair.client;

import com.example.ryanair.client.model.ScheduleResponse;
import com.example.ryanair.model.Flight;
import com.example.ryanair.model.request.FlightRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
public class SchedulesClientImpl implements SchedulesClient {

    public static final String BASE_SCHEDULES_CLIENT_URI = "https://services-api.ryanair.com/timtbl/3/schedules/";
    private final RestTemplate restTemplate;

    public SchedulesClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Gets all the schedules from the schedules API considering all the possible days/months/years
     * between the departure and arrival DateTime.
     * @param flightsRequest
     * @return List<Flight>
     * @throws RestClientException: If getResponse() is not able to do the mapping.
     */
    public List<Flight> getAllSchedules(FlightRequest flightsRequest) throws RestClientException {

        List<Flight> flightList = new ArrayList<>();

        LocalDateTime startDate = flightsRequest.getDepartureDateTimeRequested();
        LocalDateTime endDate = flightsRequest.getArrivalDateTimeRequested();

        for (LocalDateTime date = startDate; date.isBefore(endDate); date = date.plusMonths(1)) {

            List<Flight> temporalFlightList = getResponse(flightsRequest).toFlightList(
                    flightsRequest.getDepartureDateTimeRequested().getYear(),
                    flightsRequest.getArrival(),
                    flightsRequest.getDeparture());

            flightList.addAll(temporalFlightList);

            if(date.getMonthValue() == Month.DECEMBER.getValue()){
                date = date.plusYears(1);
            }

            flightsRequest.setDepartureDateTimeRequested(date);

        }

        return flightList;
    }

    private ScheduleResponse getResponse(FlightRequest interconnectedFlightsRequest) throws RestClientException {
        String uri = BASE_SCHEDULES_CLIENT_URI
                + interconnectedFlightsRequest.getDeparture() + "/"
                + interconnectedFlightsRequest.getArrival()
                + "/years/" + interconnectedFlightsRequest.getDepartureDateTimeRequested().getYear()
                + "/months/" + interconnectedFlightsRequest.getDepartureDateTimeRequested().getMonthValue();

        return restTemplate.getForObject(uri, ScheduleResponse.class);
    }

}
