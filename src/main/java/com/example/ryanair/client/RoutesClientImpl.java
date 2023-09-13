package com.example.ryanair.client;

import com.example.ryanair.model.Route;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
@Log4j2
public class RoutesClientImpl implements RoutesClient {

    public static final String ROUTES_CLIENT_URI = "https://services-api.ryanair.com/views/locate/3/routes";
    private static final String OPERATOR = "RYANAIR";
    private final RestTemplate restTemplate;

    public RoutesClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Route> getAllRoutes() throws RestClientException {
        return Arrays.stream(restTemplate.getForObject(ROUTES_CLIENT_URI, Route[].class)).toList().stream()
                .filter(route -> route.getOperator().equals(OPERATOR) && route.getConnectingAirport()==null).toList();
    }

}
