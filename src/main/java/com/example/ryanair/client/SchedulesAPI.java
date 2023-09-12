package com.example.ryanair.client;

import com.example.ryanair.client.model.ScheduleResponse;
import com.example.ryanair.model.Flight;
import com.example.ryanair.model.FlightRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.lang.annotation.Documented;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
public class SchedulesAPI implements InterfazSchedulesAPI {

    public static final String BASE_SCHEDULES_API_URI = "https://services-api.ryanair.com/timtbl/3/schedules/";
    private final RestTemplate restTemplate;

    public SchedulesAPI(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Flight> getAllSchedules(FlightRequest flightsRequest) throws RestClientException {

        List<Flight> flightList = getResponse(flightsRequest).toFlightList(flightsRequest.getDepartureDateTimeRequested().getYear());

        return flightList.stream().map(flight -> {
            flight.setArrival(flightsRequest.getArrival());
            flight.setDeparture(flightsRequest.getDeparture());
            return flight;
        }).toList();
    }

    private ScheduleResponse getResponse(FlightRequest interconnectedFlightsRequest) throws RestClientException {
        String uri = BASE_SCHEDULES_API_URI
                + interconnectedFlightsRequest.getDeparture() + "/"
                + interconnectedFlightsRequest.getArrival()
                + "/years/" + interconnectedFlightsRequest.getDepartureDateTimeRequested().getYear()
                + "/months/" + interconnectedFlightsRequest.getDepartureDateTimeRequested().getMonthValue();

        return restTemplate.getForObject(uri, ScheduleResponse.class);
    }

    /**
     * Gets all the schedules from the schedules API considering all the possible days/months/years
     * between the departure and arrival DateTime.
     * @param flightsRequest
     * @return List<Flight>
     * @throws RestClientException: If getResponse() is not able to do the mapping.
     */
    public List<Flight> getAllSchedulesFromDepartureToArrival(FlightRequest flightsRequest) throws RestClientException {

        List<Flight> flightList = new ArrayList<>();
        int year = flightsRequest.getDepartureDateTimeRequested().getYear();
        int month = flightsRequest.getDepartureDateTimeRequested().getMonthValue();

        while (year < flightsRequest.getArrivalDateTimeRequested().getYear() ||
                (month <= flightsRequest.getArrivalDateTimeRequested().getMonthValue() && year == flightsRequest.getArrivalDateTimeRequested().getYear())) {

            List<Flight> temporalFlightList = getAllSchedules(flightsRequest);

            flightList.addAll(temporalFlightList);

            if(month == Month.DECEMBER.getValue()){
                month = Month.JANUARY.getValue();
                year++;
                LocalDateTime dateTime = flightsRequest.getDepartureDateTimeRequested().plusMonths(1).plusYears(1);
                flightsRequest.setDepartureDateTimeRequested(dateTime);
            }
            else{
                month++;
                LocalDateTime dateTime = flightsRequest.getDepartureDateTimeRequested().plusMonths(1);
                flightsRequest.setDepartureDateTimeRequested(dateTime);
            }

        }

        return flightList;
    }

}
