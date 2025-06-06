package com.reactivespring.controller;

import com.reactivespring.client.MoviesInfoRestClient;
import com.reactivespring.client.ReviewsRestClient;
import com.reactivespring.domain.Movie;
import com.reactivespring.domain.MovieInfo;
import com.reactivespring.domain.Review;
import com.reactivespring.service.MoviesService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/movies")
public class MoviesController {

    private MoviesService moviesService;

    public MoviesController(MoviesService moviesService){
        this.moviesService = moviesService;
    }

    @GetMapping("/{id}")
    public Mono<Movie> getMovieById(@PathVariable("id") String movieId){

        return moviesService.retrieveMovieInfo(movieId);
    }

    @GetMapping(value = "/stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<MovieInfo> retrieveMoviesInfo(){

        return moviesService.retrieveMovieInfoStream();
    }
}
