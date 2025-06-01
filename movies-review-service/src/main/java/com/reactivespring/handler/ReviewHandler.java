package com.reactivespring.handler;

import com.reactivespring.domain.Review;
import com.reactivespring.exception.ReviewDataException;
import com.reactivespring.exception.ReviewNotFoundException;
import com.reactivespring.repository.ReviewReactiveRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;


import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ReviewHandler {

    Sinks.Many<Review> reviewsSink = Sinks.many().replay().all();

    @Autowired
    Validator validator;

    ReviewReactiveRepository reviewReactiveRepository;

    public ReviewHandler(ReviewReactiveRepository reviewReactiveRepository) {
        this.reviewReactiveRepository = reviewReactiveRepository;
    }

    public Mono<ServerResponse> addReview(ServerRequest serverRequest) {
         return serverRequest.bodyToMono(Review.class)
                 .doOnNext(this::validate)
                .flatMap(review -> {
                    return  reviewReactiveRepository.save(review);
                })
                 .doOnNext(review -> {
                     reviewsSink.tryEmitNext(review);
                 })
                 .flatMap(savedReview -> {
                     return ServerResponse.status(HttpStatus.CREATED)
                             .bodyValue(savedReview);
                 });
    }

    private void validate(Review review) {
        var constraintViolations = validator.validate(review);
        log.error("Constraint Violations : {}", constraintViolations);
        if(!constraintViolations.isEmpty()){
            var errorMessage = constraintViolations.stream()
                    .map(ConstraintViolation::getMessage)
                    .sorted()
                    .collect(Collectors.joining(","));

            throw new ReviewDataException(errorMessage);
        }
    }

    public Mono<ServerResponse> getReviews(ServerRequest serverRequest) {
        var movieInfoId = serverRequest.queryParam("movieInfoId");
        if(movieInfoId.isPresent()){
            var reviewsFlux = reviewReactiveRepository.findReviewsByMovieInfoId((Long.valueOf(movieInfoId.get())));
            return ServerResponse.ok().body(reviewsFlux, Review.class);
        } else{
            var reviewFlux = reviewReactiveRepository.findAll();
            return ServerResponse.ok().body(reviewFlux, Review.class);
        }

    }

    public Mono<ServerResponse> updateReviews(ServerRequest serverRequest) {
        var reviewId = serverRequest.pathVariable("id");
        var existingReview = reviewReactiveRepository.findById(reviewId)
                .switchIfEmpty(Mono.error(new ReviewNotFoundException("Review Not Found for the given Review id:" + reviewId)));
        return  existingReview
                .flatMap(review ->
                    serverRequest.bodyToMono(Review.class)
                            .map(reqReview -> {
                                review.setComment(reqReview.getComment());
                                review.setRating(reqReview.getRating());
                                return  review;
                            })
                            .flatMap(reviewReactiveRepository::save)
                            .flatMap(savedReview -> ServerResponse.ok().bodyValue(savedReview))
                );
    }

    public Mono<ServerResponse> deleteReview(ServerRequest serverRequest) {
        var reviewId = serverRequest.pathVariable("id");
        var existingReview = reviewReactiveRepository.findById(reviewId);
        return existingReview.flatMap(review -> reviewReactiveRepository.deleteById(reviewId))
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> getReviewsStream(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_NDJSON)
                .body(reviewsSink.asFlux(), Review.class)
                .log();
    }
}
