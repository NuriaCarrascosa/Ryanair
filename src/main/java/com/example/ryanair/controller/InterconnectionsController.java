package com.example.ryanair.controller;

import com.example.ryanair.exception.InvalidInputException;
import com.example.ryanair.model.Flight;
import com.example.ryanair.model.Route;
import com.example.ryanair.service.InterconnectionsService;
import lombok.extern.log4j.Log4j2;
import org.json.JSONException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
public class InterconnectionsController {

    private final InterconnectionsService interconnectionsService;

    public InterconnectionsController(InterconnectionsService interconnectionsService) {
        this.interconnectionsService = interconnectionsService;
    }

    @GetMapping("/ryanair/interconnections")
    @ResponseBody
    public List<Flight> getInterconnections(@RequestParam String departure, String arrival,
                                            String departureDateTime, String arrivalDateTime){

        try {
            checkInputParameters(departure, arrival, departureDateTime, arrivalDateTime);
            return this.interconnectionsService.getInterconnections(departure, departureDateTime,
                    arrival, arrivalDateTime);
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error(exception.getMessage());
        }

        return null;
    }

    private void checkInputParameters(String departure, String arrival,
                                      String departureDateTime, String arrivalDateTime) throws InvalidInputException{

        //TODO: Check input parameters:
        //      - departure and arrival are IATA code types
        //      - departureDateTime and arrivalDateTime are ISO format
    }

}
