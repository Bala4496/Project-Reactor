package ua.bala.project_reactor.service;

import ua.bala.project_reactor.service.MovieInfoService;
import ua.bala.project_reactor.service.MovieReactiveService;
import ua.bala.project_reactor.service.ReviewService;
import ua.bala.project_reactor.domain.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class MovieReactiveServiceRestClientTest {

    WebClient webClient = WebClient.builder()
        .baseUrl("http://localhost:8080/movies")
        .build();


    private MovieInfoService movieInfoService = new MovieInfoService(webClient);
    private ReviewService reviewService = new ReviewService(webClient);

    private MovieReactiveService movieReactiveService = new MovieReactiveService(movieInfoService, reviewService);

    @Test
    void getAllMoviesRestClient() {
        Flux<Movie> movieFlux = movieReactiveService.getAllMoviesRestClient();

        StepVerifier.create(movieFlux)
            .expectNextCount(7)
            .verifyComplete();
    }

    @Test
    void getMovieByIdRestClient() {
        Mono<Movie> movieFlux = movieReactiveService.getMovieByIdRestClient(1);

        StepVerifier.create(movieFlux)
            .expectNextCount(1)
            .verifyComplete();
    }
}