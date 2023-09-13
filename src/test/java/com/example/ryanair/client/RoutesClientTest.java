package com.example.ryanair.client;

import com.example.ryanair.model.Route;
import com.example.ryanair.utils.TestUtils;
import lombok.SneakyThrows;
import org.json.simple.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static com.example.ryanair.client.RoutesClientImpl.ROUTES_CLIENT_URI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class RoutesClientTest {

    private static RoutesClientImpl testee;
    private static final boolean NEW_ROUTE = false;
    private static final boolean SEASONAL_ROUTE = false;
    private static final String OPERATOR = "RYANAIR";
    private static final String GROUP = "DOMESTIC";

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
        JSONArray jsonRoute = (JSONArray) TestUtils.parseJSONFile("route-json.txt");
        when(restTemplateMock.getForObject(ROUTES_CLIENT_URI, String.class)).thenReturn(jsonRoute.toString());

        List<Route> expectedRouteList = new ArrayList<>();
        expectedRouteList.add(newRoute("LUZ","STN"));
        expectedRouteList.add(newRoute("CHQ","SKG"));
        expectedRouteList.add(newRoute("DUB","WRO"));

        //Test
        List<Route> resultRouteList = testee.getAllRoutes();

        //Verify
        assertEquals(expectedRouteList, resultRouteList);
        verify(restTemplateMock, times(1)).getForObject(ROUTES_CLIENT_URI, String.class);
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {
            "invalidAirportFrom-route-json.txt",
            "invalidAirportTo-route-json.txt",
            "invalidConnectingAirport-route-json.txt",
            "invalidNewRoute-route-json.txt",
            "invalidSeasonalRoute-route-json.txt",
            "invalidOperator-route-json.txt",
            "invalidGroup-route-json.txt",
            "missingField-route-json.txt"
    })
    void getAllRoutes_withAllParameters_invalidJSONObjects_throwException(String fileName){
        //Setup
        JSONArray jsonRoute = (JSONArray) TestUtils.parseJSONFile(fileName);
        when(restTemplateMock.getForObject(ROUTES_CLIENT_URI, String.class)).thenReturn(jsonRoute.toString());

        //Test
        assertThrows(Exception.class, () -> testee.getAllRoutes());

        //Verify
        verify(restTemplateMock, times(1)).getForObject(ROUTES_CLIENT_URI, String.class);
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
