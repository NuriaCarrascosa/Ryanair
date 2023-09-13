package com.example.ryanair.controller;

import com.example.ryanair.exception.InvalidInputException;
import com.example.ryanair.model.request.FlightRequest;
import com.example.ryanair.model.response.FlightResponse;
import com.example.ryanair.service.InterconnectionsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

@RestController
@Log4j2
public class InterconnectionsController {

    private final InterconnectionsService interconnectionsService;
    private static final int IATA_CODE_LENGHT = 3;
    public static final DateTimeFormatter ISO_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd'T'HH:mm", Locale.ENGLISH);

    public InterconnectionsController(InterconnectionsService interconnectionsService) {
        this.interconnectionsService = interconnectionsService;
    }

    @GetMapping("/ryanair/interconnections")
    @ResponseBody
    public List<FlightResponse> getInterconnections(@RequestParam String departure, String arrival,
                                                    String departureDateTime, String arrivalDateTime) {

        try {
            checkInputParameters(departure, arrival, departureDateTime, arrivalDateTime);
            return this.interconnectionsService.getInterconnections(
                    new FlightRequest(
                            departure,
                            arrival,
                            LocalDateTime.parse(departureDateTime, ISO_DATE_TIME_FORMAT),
                            LocalDateTime.parse(arrivalDateTime, ISO_DATE_TIME_FORMAT)));
        } catch (InvalidInputException invalidInputException) {
            invalidInputException.printStackTrace();
            log.error(invalidInputException.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, invalidInputException.getMessage());

        } catch (RestClientException restClientException) {
            restClientException.printStackTrace();
            log.error(restClientException.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, restClientException.getMessage());

        } catch (DateTimeParseException dateTimeParseException) {
            dateTimeParseException.printStackTrace();
            log.error(dateTimeParseException.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, dateTimeParseException.getMessage());

        } catch (Exception exception) {
            exception.printStackTrace();
            log.error(exception.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }

    }

    /**
     * Checks that the input values are correct, meaning that:
     * - departure and arrival are IATA code types (3 capital letters) and not empty
     * - departureDateTime and arrivalDateTime are ISO format ant not empty
     * @param departure
     * @param arrival
     * @param departureDateTime
     * @param arrivalDateTime
     * @throws InvalidInputException
     */
    private void checkInputParameters(String departure, String arrival,
                                      String departureDateTime, String arrivalDateTime) throws InvalidInputException{

        if(!isIATACodeValid(departure)){
            throw new InvalidInputException("Invalid input of the departure IATA code");
        }
        if(!isIATACodeValid(arrival)){
            throw new InvalidInputException("Invalid input of the arrival IATA code");
        }
        if(!isDateTimeFormatValid(departureDateTime)){
            throw new InvalidInputException("Invalid input of the departure Date Time");
        }
        if(!isDateTimeFormatValid(arrivalDateTime)){
            throw new InvalidInputException("Invalid input of the arrival Date Time");
        }

    }

    private boolean isIATACodeValid(String iataCode){
        return (!iataCode.isEmpty() && iataCode.length() == IATA_CODE_LENGHT && iataCode.equals(iataCode.toUpperCase()));
    }

    /**
     * Check if the String dateTime satisfy the requirements of the program such as not being empty and having the
     * pattern of: a valid year "yyyy" (between 1900 and 2099) + "-" + valid month (between 01 and 12) + "-" + valid
     * day (between 01 and 31) + "T" + valid hour (between 00 and 23) + valid minutes (between 00 and 59).
     * @param dateTime
     * @return true if the String dateTime is not empty an if it matches with the ISO_DATE_TIME_FORMAT and
     * false if it does not fulfill none of these requirements
     */
    private boolean isDateTimeFormatValid(String dateTime){
        return !dateTime.isEmpty();
    }

}
