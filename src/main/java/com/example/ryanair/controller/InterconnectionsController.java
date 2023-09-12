package com.example.ryanair.controller;

import com.example.ryanair.exception.InvalidInputException;
import com.example.ryanair.model.request.FlightRequest;
import com.example.ryanair.model.response.FlightResponse;
import com.example.ryanair.service.InterconnectionsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@RestController
@Log4j2
public class InterconnectionsController {

    private final InterconnectionsService interconnectionsService;
    public static final DateTimeFormatter ISO_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd'T'HH:mm", Locale.ENGLISH);

    public InterconnectionsController(InterconnectionsService interconnectionsService) {
        this.interconnectionsService = interconnectionsService;
    }

    @GetMapping("/ryanair/interconnections")
    @ResponseBody
    public List<FlightResponse> getInterconnections(@RequestParam String departure, String arrival,
                                                    String departureDateTime, String arrivalDateTime){

        // TODO: Ver si se puede llamar al Get con la clase InterconnectedFlightRequest

        try {
            checkInputParameters(departure, arrival, departureDateTime, arrivalDateTime);
            return this.interconnectionsService.getInterconnections(
                    new FlightRequest(
                            departure,
                            arrival,
                            LocalDateTime.parse(departureDateTime, ISO_DATE_TIME_FORMAT),
                            LocalDateTime.parse(arrivalDateTime, ISO_DATE_TIME_FORMAT)));
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
