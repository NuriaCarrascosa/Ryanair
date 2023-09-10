package com.example.ryanair.api;

import com.example.ryanair.model.Flight;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
public class SchedulesAPI {

    public static final DateTimeFormatter HOUR_MINUTE_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    public static final String BASE_SCHEDULES_API_URI = "https://services-api.ryanair.com/timtbl/3/schedules/";
    private final RestTemplate restTemplate;

    public SchedulesAPI(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Flight> getAllSchedules(String departure, String arrival, int year, int month) throws JSONException {
        List<Flight> flightList = new ArrayList<>();

        JSONObject jsonResponse = getJsonResponse(departure, arrival, year, month);
        JSONArray jsonDaysResponse = jsonResponse.getJSONArray("days");

        for(int i = 0; i < jsonDaysResponse.length(); i++){
            JSONObject jsonDayFlightsResponse = jsonDaysResponse.getJSONObject(i);

            int day = (int) jsonDayFlightsResponse.get("day");
            JSONArray jsonFlightsResponse = jsonDayFlightsResponse.getJSONArray("flights");

            for(int j = 0; j < jsonFlightsResponse.length(); j++){
                JSONObject jsonFlight = jsonFlightsResponse.getJSONObject(j);
                flightList.add(parseFlight(year, month, day, jsonFlight, departure, arrival));
            }
        }

        return flightList;
    }

    public List<Flight> getSchedulesByDay(String departure, String arrival, int year, int month, int day) throws JSONException {
        List<Flight> flightList = getAllSchedules(departure, arrival, year, month);
        return flightList.stream().filter(flight -> flight.getArrivalDateTime().getDayOfMonth()==day).toList();
    }

    private Flight parseFlight(int year, int month, int day, JSONObject jsonFlight, String departure, String arrival) throws JSONException {
        LocalTime departureTime = LocalTime.parse((String) jsonFlight.get("departureTime"), HOUR_MINUTE_FORMAT);
        LocalTime arrivalTime = LocalTime.parse((String) jsonFlight.get("arrivalTime"), HOUR_MINUTE_FORMAT);

        LocalDateTime departureLocalDateTime = LocalDateTime.of(year, month, day, departureTime.getHour(), departureTime.getMinute());
        LocalDateTime arrivalLocalDateTime = LocalDateTime.of(year, month, day, arrivalTime.getHour(), arrivalTime.getMinute());

        int flightNumber = Integer.parseInt((String) jsonFlight.get("number"));

        return new Flight(flightNumber, departureLocalDateTime, arrivalLocalDateTime, departure, arrival);
    }

    private JSONObject getJsonResponse(String departure, String arrival, int year, int month) throws JSONException {
        String uri = BASE_SCHEDULES_API_URI + departure + "/" + arrival + "/years/" + year + "/months/" + month;
        String stringResponse = restTemplate.getForObject(uri, String.class);
        return new JSONObject(stringResponse);
    }

}
