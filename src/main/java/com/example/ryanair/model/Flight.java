package com.example.ryanair.model;

import lombok.Data;

import java.time.LocalTime;

@Data
public class Flight {
    private String carrierCode; // this attribute is not specified on the requirements (pdf)
    private int flightNumber;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private int month;
    private int day;
}
