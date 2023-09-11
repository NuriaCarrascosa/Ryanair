package com.example.ryanair.client;

import com.example.ryanair.model.Route;
import org.json.JSONException;

import java.util.List;

public interface InterfazRoutesAPI {

    List<Route> getAllRoutes() throws JSONException;

}
