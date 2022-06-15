package ua.bala.project_reactor.service;

import ua.bala.project_reactor.domain.MovieInfo;
import ua.bala.project_reactor.service.MovieInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class MovieInfoServiceTest {

    WebClient webClient = WebClient.builder()
        .baseUrl("http://localhost:8080/movie")
        .build();

    MovieInfoService movieInfoService = new MovieInfoService(webClient);

    @Test
    void retrieveMovieInfoRestClient() {

        Flux<MovieInfo> movieInfoFlux = movieInfoService
            .retrieveAllMoviesInfoRestClient();

        StepVerifier.create(movieInfoFlux)
            .expectNextCount(7)
            .verifyComplete();
    }

    @Test
    void retrieveMoviesByIdRestClient() {

        Mono<MovieInfo> movieInfoFlux = movieInfoService
            .retrieveMoviesByIdRestClient(1);

        StepVerifier.create(movieInfoFlux)
            .expectNextCount(7)
            .verifyComplete();
    }
}