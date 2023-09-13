package com.example.ryanair.client;

import com.example.ryanair.model.Route;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.example.ryanair.client.RoutesClientImpl.ROUTES_CLIENT_URI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class RoutesClientImplTest {

    private static RoutesClientImpl testee;
    private static Route[] routeList;
    private static final boolean NEW_ROUTE = false;
    private static final boolean SEASONAL_ROUTE = false;
    private static final String OPERATOR = "RYANAIR";
    private static final String GROUP = "DOMESTIC";
    private static final String DEPARTURE = "DUB";
    private static final String ARRIVAL = "WRO";

    @Mock
    private static RestTemplate restTemplateMock;

    @BeforeEach
    void initialize(){
        MockitoAnnotations.openMocks(this);
        testee = new RoutesClientImpl(restTemplateMock);
    }

    @SneakyThrows
    @Test
    void getAllRoutes_withAllParameters_returnsRouteList(){
        //Setup
        routeList = new Route[]{newRoute(DEPARTURE, ARRIVAL)};
        when(restTemplateMock.getForObject(ROUTES_CLIENT_URI, Route[].class)).thenReturn(routeList);

        List<Route> expectedRouteList = List.of(newRoute(DEPARTURE, ARRIVAL));

        //Test
        List<Route> resultRouteList = testee.getAllRoutes();

        //Verify
        assertEquals(expectedRouteList, resultRouteList);
        verify(restTemplateMock, times(1)).getForObject(ROUTES_CLIENT_URI, Route[].class);
    }

    @Test
    void getAllRoutes_invalidOperatorOrConnectingAirport_notIncluded(){
        //Setup
        routeList = new Route[]{
                newRoute(DEPARTURE, ARRIVAL),
                Route.builder().operator("IBERIA").connectingAirport(null).build(),
                Route.builder().operator(OPERATOR).connectingAirport("MAD").build()};
        when(restTemplateMock.getForObject(ROUTES_CLIENT_URI, Route[].class)).thenReturn(routeList);

        List<Route> expectedRouteList = List.of(newRoute(DEPARTURE, ARRIVAL));

        //Test
        List<Route> resultRouteList = testee.getAllRoutes();

        //Verify
        assertEquals(expectedRouteList, resultRouteList);
        verify(restTemplateMock, times(1)).getForObject(ROUTES_CLIENT_URI, Route[].class);
    }

    @Test
    void getAllRoutes_withAllParameters_throwException(){
        //Setup
        when(restTemplateMock.getForObject(ROUTES_CLIENT_URI, Route[].class)).thenThrow(RestClientException.class);

        //Test
        assertThrows(RestClientException.class, () -> testee.getAllRoutes());

        //Verify
        verify(restTemplateMock, times(1)).getForObject(ROUTES_CLIENT_URI, Route[].class);
    }

    private Route newRoute(String airportFrom, String airportTo){
        return new Route(airportFrom,
                airportTo,
                null,
                NEW_ROUTE,
                SEASONAL_ROUTE,
                OPERATOR,
                GROUP);
    }

}
