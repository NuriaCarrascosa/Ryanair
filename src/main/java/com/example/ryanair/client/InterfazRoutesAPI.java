package com.example.ryanair.client;

import com.example.ryanair.model.Route;
import org.springframework.web.client.RestClientException;

import java.util.List;

public interface InterfazRoutesAPI {

    List<Route> getAllRoutes() throws RestClientException;

}
