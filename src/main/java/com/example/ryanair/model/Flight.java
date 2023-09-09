package com.example.ryanair.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

@Data
@AllArgsConstructor
public class Flight {
    private String carrierCode;
    private int flightNumber;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private int year;
    private int month;
    private int day;
}
