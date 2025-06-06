package com.reactivespring.service;

import com.reactivespring.client.MoviesInfoRestClient;
import com.reactivespring.client.ReviewsRestClient;
import com.reactivespring.domain.Movie;
import com.reactivespring.domain.MovieInfo;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MoviesService {

    private MoviesInfoRestClient moviesInfoRestClient;
    private ReviewsRestClient reviewsRestClient;

    public MoviesService(MoviesInfoRestClient moviesInfoRestClient, ReviewsRestClient reviewsRestClient){
        this.moviesInfoRestClient = moviesInfoRestClient;
        this.reviewsRestClient = reviewsRestClient;
    }

    @CircuitBreaker(name = "moviesService")
    public Mono<Movie> retrieveMovieInfo(String movieId){
        return moviesInfoRestClient.retrieveMovieInfo(movieId)
                .flatMap(movieInfo -> {
                    var reviewsListMono = reviewsRestClient.getReviewsByMovieId(movieId)
                            .collectList();
                    return reviewsListMono.map(reviews ->  new Movie(movieInfo, reviews));
                });
    }

    public Flux<MovieInfo> retrieveMovieInfoStream(){
        return moviesInfoRestClient.retrieveMovieInfoStream();
    }
}
