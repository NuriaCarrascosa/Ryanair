#Ryanair
Ryanair is a Spring Boot based RESTful API application which serves information about all the possible direct and
interconnected flights between two dates. Following the next requirements:
- The departure airport time must not be earlier than the specified and the arrival airport time must not be after the specified.
- Interconnected flights can only have 1 stop and the difference between the arrival and the next departure should be 2h or
greater.
###Endpoint

- http://HOST/ryanair/interconnections?departure={departure}&arrival={arrival}&departureDateTime={departureDateTime}&arrivalDateTime={arrivalDateTime}
  - departure - A departure airport IATA code
  - departureDateTime - A departure datetime in the departure airport timezone in ISO format
  - arrival - An arrival airport IATA code
  - arrivalDateTime - An arrival datetime in the arrival airport timezone in ISO format
  - HOST - If running locally = localhost:8080 (The port can be modified in the application.properties file)
  
###Execution

1- Open a terminal and go to the source project directory

2- Execute `./mvnw spring-boot:run` or `mvn spring-boot:run` if you have already installed it

####Usage example

Calling: `http://localhost:8080/ryanair/interconnections?departure=MAD&arrival=DUB&departureDateTime=2023-11-31T16:20&arrivalDateTime=2023-11-31T23:30`

Should return:
```
[
    {
        "stops": 0,
        "legs": [
            {
                "departureAirport": "MAD",
                "arrivalAirport": "DUB",
                "departureDateTime": "2023-11-30T20:30:00",
                "arrivalDateTime": "2023-11-30T22:10:00"
            }
        ]
    },
    {
        "stops": 1,
        "legs": [
            {
                "departureAirport": "MAD",
                "arrivalAirport": "STN",
                "departureDateTime": "2023-11-30T16:25:00",
                "arrivalDateTime": "2023-11-30T17:55:00"
            },
            {
                "departureAirport": "STN",
                "arrivalAirport": "DUB",
                "departureDateTime": "2023-11-30T20:35:00",
                "arrivalDateTime": "2023-11-30T21:55:00"
            }
        ]
    }
]
```

###Tests

1- Open a terminal and go to the source project directory

2- Execute `./mvnw test` or `mvn test` if you have already installed it