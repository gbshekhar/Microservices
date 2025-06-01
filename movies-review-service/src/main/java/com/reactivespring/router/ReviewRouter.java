package com.reactivespring.router;

import com.reactivespring.handler.ReviewHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;


import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ReviewRouter {

    @Bean
    public RouterFunction<ServerResponse> reviewsRoute(ReviewHandler reviewHandler){
        return route()
                .nest(path("/v1/reviews"), builder -> {
                    builder.POST("", (serverRequest -> reviewHandler.addReview(serverRequest)))
                            .GET("", (serverRequest -> reviewHandler.getReviews(serverRequest)))
                            .PUT("/{id}", serverRequest -> reviewHandler.updateReviews(serverRequest))
                            .DELETE("/{id}", serverRequest -> reviewHandler.deleteReview(serverRequest))
                            .GET("/stream", serverRequest -> reviewHandler.getReviewsStream(serverRequest));
                })
                .GET("/v1/helloworld", (serverRequest -> ServerResponse.ok().bodyValue("hello world")))
//                .POST("/v1/reviews", (serverRequest -> reviewHandler.addReview(serverRequest)))
//                .GET("/v1/reviews", (serverRequest -> reviewHandler.getAllReviews(serverRequest)))
                .build();
    }
}
