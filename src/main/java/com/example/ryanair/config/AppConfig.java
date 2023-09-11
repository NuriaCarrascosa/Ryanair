package com.example.ryanair.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    /*
    @Bean
    public InterfazSchedulesAPI schedulesAPI(RestTemplate restTemplate){
        return new SchedulesAPI(restTemplate);
    }

    @Bean
    public InterfazRoutesAPI routesAPI(RestTemplate restTemplate){
        return new RoutesAPI(restTemplate);
    }

     */

}
