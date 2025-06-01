package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class MoviesInfoControllerIntgTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

    @Autowired
    WebTestClient webTestClient;

    static  String MOVIES_INFO_URI = "/v1/movieinfos";

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

    }

    @Test
    void addMovieInfo() {
        //given - precondition or setup
        var movieInfo = new MovieInfo(null, "Batman Begins2",
                "2005", List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        //when - action or behaviour that we are going to test
        webTestClient.post()
                .uri(MOVIES_INFO_URI)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(savedMovieInfo);
                    assertNotNull(savedMovieInfo.getMovieInfoId());
                });

        //then - verify output
    }

    //Junit Test for
    @Test
    public void getAllMovieInfos(){
        //given - precondition or setup

        //when  - action or behaviour that we are going to test
        webTestClient.get()
                .uri(MOVIES_INFO_URI)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);

          //then - verify output

    }

    @Test
    public void getAllMovieInfos_stream(){
        //given - precondition or setup
        var movieInfo = new MovieInfo(null, "Batman Begins2",
                "2005", List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        //when - action or behaviour that we are going to test
        webTestClient.post()
                .uri(MOVIES_INFO_URI)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(savedMovieInfo);
                    assertNotNull(savedMovieInfo.getMovieInfoId());
                });

        //when  - action or behaviour that we are going to test
        var moviesStreamFlux = webTestClient.get()
                .uri(MOVIES_INFO_URI + "/stream")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(MovieInfo.class)
                .getResponseBody();

        //then
        StepVerifier.create(moviesStreamFlux)
                .assertNext(movieInfo1 -> {
                    assert movieInfo1.getMovieInfoId() != null;
                })
                .thenCancel()
                .verify();

    }

    @Test
    public void getAllMovieInfosByYear(){
        //given - precondition or setup
        var uri = UriComponentsBuilder.fromUriString(MOVIES_INFO_URI)
                        .queryParam("year", "2005")
                                .build().toUri();

        //when  - action or behaviour that we are going to test
        webTestClient.get()
                .uri(uri)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(1);

        //then - verify output

    }

    @Test
    public void getMovieInfoById(){
        //given - precondition or setup
        String id = "abc";

        //when  - action or behaviour that we are going to test
        webTestClient.get()
                .uri(MOVIES_INFO_URI + "/{id}", id)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Dark Knight Rises");
                /*
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var movieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(movieInfo);
                });*/

        //then - verify output

    }

    @Test
    public void getMovieInfoById_notFound(){
        //given - precondition or setup
        String id = "abcd";

        //when & then
        webTestClient.get()
                .uri(MOVIES_INFO_URI + "/{id}", id)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void updateMovieInfo() {
        //given - precondition or setup
        var id = "abc";
        var movieInfo = new MovieInfo(null, "Batman Begins3",
                "2005", List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        //when - action or behaviour that we are going to test
        webTestClient.put()
                .uri(MOVIES_INFO_URI + "/{id}", id)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var updatedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(updatedMovieInfo);
                    assertNotNull(updatedMovieInfo.getMovieInfoId());
                    assertEquals("Batman Begins3", updatedMovieInfo.getName());
                });

        //then - verify output
    }

    @Test
    void updateMovieInfo_notFound() {
        //given - precondition or setup
        var id = "def";
        var movieInfo = new MovieInfo(null, "Batman Begins3",
                "2005", List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        //when - action or behaviour that we are going to test
        webTestClient.put()
                .uri(MOVIES_INFO_URI + "/{id}", id)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isNotFound();

        //then - verify output
    }

    @Test
    public void deleteMovieInfoById(){
        //given - precondition or setup
        String id = "abc";

        //when  - action or behaviour that we are going to test; then
        webTestClient.delete()
                .uri(MOVIES_INFO_URI + "/{id}", id)
                .exchange()
                .expectStatus()
                .isNoContent();
    }
}