package com.example.ryanair.api;

import com.example.ryanair.model.Route;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
public class RoutesAPI {

    public static final String routesAPIUri = "https://services-api.ryanair.com/views/locate/3/routes";
    private final RestTemplate restTemplate;

    public RoutesAPI(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Route> getRoutes() throws JSONException {
        List<Route> routeList = new ArrayList<>();

        JSONArray jsonResponse = getJsonResponse();

        for (int i = 0; i < jsonResponse.length(); i++) {
            JSONObject jsonRoute = (JSONObject) jsonResponse.get(i);
            routeList.add(parseRoute(jsonRoute));
        }

        log.info("Hay " + jsonResponse.length() + " rutas definidas.");

        return routeList;
    }

    private Route parseRoute(JSONObject jsonRoute) throws JSONException {
        String connectingAirport = jsonRoute.isNull("connectingAirport")
                ? null : (String) jsonRoute.get("connectingAirport");

        return new Route(
                (String) jsonRoute.get("airportFrom"),
                (String) jsonRoute.get("airportTo"),
                connectingAirport,
                (Boolean) jsonRoute.get("newRoute"),
                (Boolean) jsonRoute.get("seasonalRoute"),
                (String) jsonRoute.get("operator"),
                (String) jsonRoute.get("group"));
    }

    private JSONArray getJsonResponse() throws JSONException {
        String stringResponse = restTemplate.getForObject(routesAPIUri, String.class);
        return new JSONArray(stringResponse);
    }

}
