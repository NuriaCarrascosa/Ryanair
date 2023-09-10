package com.example.ryanair.api;

import com.example.ryanair.model.Flight;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
public class SchedulesAPI {

    public static final DateTimeFormatter hourMinuteFormat = DateTimeFormatter.ofPattern("HH:mm");
    public static final String baseSchedulesAPIUri = "https://services-api.ryanair.com/timtbl/3/schedules/";
    private final RestTemplate restTemplate;

    public SchedulesAPI(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Flight> getSchedules(String departure, String arrival, String year, String month) throws JSONException {
        List<Flight> flightList = new ArrayList<>();

        JSONObject jsonResponse = getJsonResponse(departure, arrival, year, month);
        JSONArray jsonDaysResponse = jsonResponse.getJSONArray("days");

        for(int i = 0; i < jsonDaysResponse.length(); i++){
            JSONObject jsonDayFlightsResponse = jsonDaysResponse.getJSONObject(i);

            int day = (int) jsonDayFlightsResponse.get("day");
            JSONArray jsonFlightsResponse = jsonDayFlightsResponse.getJSONArray("flights");

            for(int j = 0; j < jsonFlightsResponse.length(); j++){
                JSONObject jsonFlight = jsonFlightsResponse.getJSONObject(j);
                flightList.add(parseFlight(year, month, day, jsonFlight));

                log.info("Para el día " + day + "-" + month + "-" + year
                        + " hay " + jsonFlightsResponse.length() + " vuelos.");
            }
        }

        if(flightList.isEmpty()){
            log.info("No hay vuelos con las condiciones indicadas: de "
                    + departure + " a " + arrival + " " + month + "-" + year + ".");
        }

        return flightList;
    }

    private Flight parseFlight(String year, String month, int day, JSONObject jsonFlight) throws JSONException {
        return new Flight(
                Integer.parseInt((String) jsonFlight.get("number")),
                LocalTime.parse((String) jsonFlight.get("departureTime"), hourMinuteFormat),
                LocalTime.parse((String) jsonFlight.get("arrivalTime"), hourMinuteFormat),
                Integer.parseInt(year),
                Integer.parseInt(month),
                day);
    }

    private JSONObject getJsonResponse(String departure, String arrival, String year, String month) throws JSONException {
        String uri = baseSchedulesAPIUri + departure + "/" + arrival + "/years/" + year + "/months/" + month;
        String stringResponse = restTemplate.getForObject(uri, String.class);
        return new JSONObject(stringResponse);
    }

}
