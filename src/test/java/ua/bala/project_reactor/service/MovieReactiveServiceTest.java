package ua.bala.project_reactor.service;

import ua.bala.project_reactor.domain.Movie;
import ua.bala.project_reactor.service.MovieInfoService;
import ua.bala.project_reactor.service.MovieReactiveService;
import ua.bala.project_reactor.service.RevenueService;
import ua.bala.project_reactor.service.ReviewService;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class MovieReactiveServiceTest {

    private MovieInfoService movieInfoService = new MovieInfoService();
    private ReviewService reviewService = new ReviewService();
    private RevenueService revenueService = new RevenueService();
    private MovieReactiveService movieReactiveService = new MovieReactiveService(movieInfoService, reviewService, revenueService);

    @Test
    void getAllMovies() {

        Flux<Movie> allMovies = movieReactiveService.getAllMovies();

        StepVerifier.create(allMovies)
            .assertNext(movie -> {
                assertEquals("Batman Begins", movie.getMovie().getName());
                assertEquals(2, movie.getReviewList().size());
            })
            .assertNext(movie -> {
                assertEquals("The Dark Knight", movie.getMovie().getName());
                assertEquals(2, movie.getReviewList().size());
            })
            .assertNext(movie -> {
                assertEquals("Dark Knight Rises", movie.getMovie().getName());
                assertEquals(2, movie.getReviewList().size());
            })
            .verifyComplete();

    }

    @Test
    void getMovieById() {

        Mono<Movie> movieById = movieReactiveService.getMovieById(100L);

        StepVerifier.create(movieById)
            .assertNext(movie -> {
                assertEquals("Batman Begins", movie.getMovie().getName());
                assertEquals(2, movie.getReviewList().size());
            })
            .verifyComplete();
    }

    @Test
    void getMovieByIdWithRevenue() {
        Mono<Movie> movieById = movieReactiveService.getMovieByIdWithRevenue(100L);

        StepVerifier.create(movieById)
            .assertNext(movie -> {
                assertEquals("Batman Begins", movie.getMovie().getName());
                assertEquals(2, movie.getReviewList().size());
                assertNotNull(movie.getRevenue());
            })
            .verifyComplete();
    }

}