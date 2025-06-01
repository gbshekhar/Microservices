package com.reactivespring.repository;

import com.reactivespring.domain.MovieInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
class MovieInfoRepositoryIntgTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

    @BeforeEach
    void setUp() {
        var movieinfos = List.of(new MovieInfo(null, "Batman Begins",
                        "2005", List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        "2008", List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        "2012", List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        movieInfoRepository.saveAll(movieinfos)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    //Junit Test for
    @Test
    public void findAll(){
        //given - precondition or setup

        //when  - action or behaviour that we are going to test
       var moviesInfoFlux = movieInfoRepository.findAll().log();

        //then - verify output
        StepVerifier.create(moviesInfoFlux)
                .expectNextCount(3)
                .verifyComplete();

    }

    @Test
    void findById() {
        //given - precondtion or setup
        String movieId = "abc";

        //when - action or behaviour that we are going to test
        var movieMono = movieInfoRepository.findById(movieId);

        //then - verify output
        StepVerifier.create(movieMono)
                .assertNext(movieInfo -> {
                    assertEquals("Dark Knight Rises", movieInfo.getName());
                })
                .verifyComplete();
    }

    //Junit Test for
    @Test
    public void saveMovieInfo(){
        //given - precondition or setup
        var movieInfo = new MovieInfo(null, "Batman Begins1",
                "2005", List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        //when  - action or behaviour that we are going to test
        var movieInfoMono = movieInfoRepository.save(movieInfo).log();

        //then - verify output
        StepVerifier.create(movieInfoMono)
                .assertNext(movieInfo1 -> {
                    assertNotNull(movieInfo1.getMovieInfoId());
                    assertEquals("Batman Begins1", movieInfo1.getName());
                })
                .verifyComplete();
    }

    @Test
    public void updateMovieInfo(){
        //given - precondition or setup
        var movieInfo = movieInfoRepository.findById("abc").block();
        movieInfo.setYear("2006");

        //when  - action or behaviour that we are going to test
        var movieInfoMono = movieInfoRepository.save(movieInfo).log();

        //then - verify output
        StepVerifier.create(movieInfoMono)
                .assertNext(movieInfo1 -> {
                    assertEquals("2006", movieInfo1.getYear());
                })
                .verifyComplete();
    }

    @Test
    public void deleteById(){
        //given - precondition or setup

        //when  - action or behaviour that we are going to test
        movieInfoRepository.deleteById("abc").block();
        var moviesInfoFlux = movieInfoRepository.findAll().log();

        //then - verify output
        StepVerifier.create(moviesInfoFlux)
                .expectNextCount(2)
                .verifyComplete();

    }
}