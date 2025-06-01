package com.gbs.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder){
       return builder.routes()
               .route("movies-info-service", predicateSpec ->
                       predicateSpec.path("/v1/movieinfos", "/v1/movieinfos/**")
//                               .filters(gatewayFilterSpec ->
//                                       gatewayFilterSpec.rewritePath("/movieinfos(?<segment>/?.*)",
//                                       "/v1/movieinfos${segment}"))
                           .uri("lb://MOVIES-INFO-SERVICE"))
               .route("movies-review-service", predicateSpec ->
                       predicateSpec.path("/v1/reviews")
//                               .filters(gatewayFilterSpec ->
//                                       gatewayFilterSpec.rewritePath("/reviews(?<segment>/?.*)",
//                                               "/v1/reviews${segment}"))
                               .uri("lb://MOVIES-REVIEW-SERVICE"))
               .route("movies-service", predicateSpec ->
                       predicateSpec.path("/v1/movies/**")
//                               .filters(gatewayFilterSpec ->
//                                       gatewayFilterSpec.rewritePath("/movies(?<segment>/?.*)",
//                                               "/v1/movies${segment}"))
                               .uri("lb://MOVIES-SERVICE"))
               .route("service-registry", predicateSpec ->
                       predicateSpec.path("/eureka/main")
                               .filters(gatewayFilterSpec ->
                                       gatewayFilterSpec.rewritePath("/eureka/main", "/"))
                               .uri("http://localhost:8761"))
               .route("service-registry-static", predicateSpec ->
                      predicateSpec.path("/eureka/**")
                              .uri("http://localhost:8761"))
               .build();
    }
}
