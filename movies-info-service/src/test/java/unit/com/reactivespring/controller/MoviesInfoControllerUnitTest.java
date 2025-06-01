package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MoviesInfoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;

@WebFluxTest(controllers = MoviesInfoController.class)
@AutoConfigureWebTestClient
class MoviesInfoControllerUnitTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private MoviesInfoService moviesInfoServiceMock;

    static  String MOVIES_INFO_URI = "/v1/movieinfos";

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    //Junit Test for
    @Test
    public void getAllMovieInfos(){
        //given - precondition or setup
        var movieinfos = List.of(new MovieInfo(null, "Batman Begins",
                        "2005", List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        "2008", List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        "2012", List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        when(moviesInfoServiceMock.getAllMovieInfos()).thenReturn(Flux.fromIterable(movieinfos));

        //when  & then
        webTestClient.get()
                .uri(MOVIES_INFO_URI)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);

    }

    //Junit Test for
    @Test
    public void getMovieInfoById(){
        //given - precondition or setup
        var movieInfoMono = Mono.just(new MovieInfo("abc", "Dark Knight Rises",
                "2012", List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));
        var id = "abc";
        when(moviesInfoServiceMock.getMovieInfoById(id)).thenReturn(movieInfoMono);

        //when  & then
        webTestClient.get()
                .uri(MOVIES_INFO_URI + "/{id}", id)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var movieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(movieInfo);
                });
    }

    //Junit Test for
    @Test
    public void addMovieInfo(){
        //given - precondition or setup
        var movieInfo = new MovieInfo(null, "Batman Begins2",
                "2005", List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));
        when(moviesInfoServiceMock.addMovieInfo(isA(MovieInfo.class))).thenReturn(Mono.just(new MovieInfo("mockId", "Batman Begins2",
                "2005", List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"))));

        //when && then
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
                    assertEquals("mockId", savedMovieInfo.getMovieInfoId());
                });

    }

    @Test
    public void addMovieInfo_validation(){
        //given - precondition or setup
        var movieInfo = new MovieInfo(null, "",
                "", List.of(""), LocalDate.parse("2005-06-15"));
        when(moviesInfoServiceMock.addMovieInfo(isA(MovieInfo.class))).thenReturn(Mono.just(new MovieInfo("mockId", "Batman Begins2",
                "2005", List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"))));

        //when && then
        webTestClient.post()
                .uri(MOVIES_INFO_URI)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .consumeWith(stringEntityExchangeResult  -> {
                    var responseBody = stringEntityExchangeResult.getResponseBody();
                    var expectedErrorMessage = "movieInfo.cast must be present,movieInfo.name must be present,movieInfo.year must be present";
                    assertNotNull(responseBody);
                    assertEquals(expectedErrorMessage, responseBody);
                });
    }

    @Test
    void updateMovieInfo() {
        //given - precondition or setup
        var id = "abc";
        var movieInfo = new MovieInfo(null, "Batman Begins3",
                "2005", List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));
        when(moviesInfoServiceMock.updateMovieInfo(isA(String.class), isA(MovieInfo.class))).thenReturn(Mono.just(new MovieInfo("mockId", "Batman Begins3",
                "2005", List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"))));

        //when & then
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
    }
}