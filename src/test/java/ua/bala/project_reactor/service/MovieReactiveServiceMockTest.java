package ua.bala.project_reactor.service;

import ua.bala.project_reactor.service.MovieInfoService;
import ua.bala.project_reactor.service.MovieReactiveService;
import ua.bala.project_reactor.service.ReviewService;
import ua.bala.project_reactor.domain.Movie;
import ua.bala.project_reactor.exception.MovieException;
import ua.bala.project_reactor.exception.ServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;


import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class MovieReactiveServiceMockTest {

    @Mock
    private MovieInfoService movieInfoService;
    @Mock
    private ReviewService reviewService;

    @InjectMocks
    MovieReactiveService movieReactiveService;

    @Test
    void getAllMovies() {
        Mockito.when(movieInfoService.retrieveMoviesFlux())
            .thenCallRealMethod();
        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
            .thenCallRealMethod();

        Flux<Movie> allMovies = movieReactiveService.getAllMovies();

        StepVerifier.create(allMovies)
            .expectNextCount(3)
            .verifyComplete();
    }

    @Test
    void getAllMoviesRetry() {
        String errorMessage = "Exception occurred in ReviewService";
        Mockito.when(movieInfoService.retrieveMoviesFlux())
            .thenCallRealMethod();
        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
            .thenThrow(new RuntimeException(errorMessage));

        Flux<Movie> allMovies = movieReactiveService.getAllMoviesRetry();

        StepVerifier.create(allMovies)
            .expectError(MovieException.class)
            .verify();

        verify(reviewService, times(4))
            .retrieveReviewsFlux(isA(Long.class));
    }

    @Test
    void getAllMoviesRetryWhen() {
        String errorMessage = "Exception occurred in ReviewService";
        Mockito.when(movieInfoService.retrieveMoviesFlux())
            .thenCallRealMethod();
        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
            .thenThrow(new ServiceException(errorMessage));

        Flux<Movie> allMovies = movieReactiveService.getAllMoviesRetryWhen();

        StepVerifier.create(allMovies)
            .expectError(ServiceException.class)
            .verify();

        verify(reviewService, times(1))
            .retrieveReviewsFlux(isA(Long.class));
    }

    @Test
    void getAllMoviesRepeat() {
        Mockito.when(movieInfoService.retrieveMoviesFlux())
            .thenCallRealMethod();
        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
            .thenCallRealMethod();

        Flux<Movie> allMovies = movieReactiveService.getAllMoviesRepeat();

        StepVerifier.create(allMovies)
            .expectNextCount(6)
            .thenCancel()
            .verify();

        verify(reviewService, times(6))
            .retrieveReviewsFlux(isA(Long.class));
    }

    @Test
    void getAllMoviesRepeatN() {
        Mockito.when(movieInfoService.retrieveMoviesFlux())
            .thenCallRealMethod();
        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
            .thenCallRealMethod();

        Flux<Movie> allMovies = movieReactiveService.getAllMoviesRepeatN(3);

        StepVerifier.create(allMovies)
            .expectNextCount(12)
            .thenCancel()
            .verify();

        verify(reviewService, times(12))
            .retrieveReviewsFlux(isA(Long.class));
    }
}