package ua.bala.project_reactor.service;

import ua.bala.project_reactor.domain.Movie;
import ua.bala.project_reactor.domain.MovieInfo;
import ua.bala.project_reactor.domain.Revenue;
import ua.bala.project_reactor.domain.Review;
import ua.bala.project_reactor.exception.MovieException;
import ua.bala.project_reactor.exception.NetworkException;
import ua.bala.project_reactor.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;
import java.util.List;

@Slf4j
public class MovieReactiveService {

    private MovieInfoService movieInfoService;
    private ReviewService reviewService;
    private RevenueService revenueService;

    public MovieReactiveService(MovieInfoService movieInfoService, ReviewService reviewService) {
        this.movieInfoService = movieInfoService;
        this.reviewService = reviewService;
    }

    public MovieReactiveService(MovieInfoService movieInfoService, ReviewService reviewService, RevenueService revenueService) {
        this.movieInfoService = movieInfoService;
        this.reviewService = reviewService;
        this.revenueService = revenueService;
    }

    public Flux<Movie> getAllMovies() {
        Flux<MovieInfo> movieInfoFlux = movieInfoService.retrieveMoviesFlux();
        return movieInfoFlux
            .flatMap(movieInfo -> {
                Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                    .collectList();
                return reviewsMono
                    .map(reviews -> new Movie(movieInfo, reviews));
            })
            .onErrorMap(ex -> {
                log.error("Exception is: ", ex);
                throw new MovieException(ex.getMessage());
            });
    }

    public Flux<Movie> getAllMoviesRestClient() {
        Flux<MovieInfo> movieInfoFlux = movieInfoService.retrieveAllMoviesInfoRestClient();
        return movieInfoFlux
            .flatMap(movieInfo -> {
                Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux_RestClient(movieInfo.getMovieInfoId())
                    .collectList();
                return reviewsMono
                    .map(reviews -> new Movie(movieInfo, reviews));
            })
            .onErrorMap(ex -> {
                log.error("Exception is: ", ex);
                throw new MovieException(ex.getMessage());
            });
    }

    public Flux<Movie> getAllMoviesRetry() {
        Flux<MovieInfo> movieInfoFlux = movieInfoService.retrieveMoviesFlux();
        return movieInfoFlux
            .flatMap(movieInfo -> {
                Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                    .collectList();
                return reviewsMono
                    .map(reviews -> new Movie(movieInfo, reviews));
            })
            .onErrorMap(ex -> {
                log.error("Exception is: ", ex);
                throw new MovieException(ex.getMessage());
            })
            .retry(3);
    }

    public Flux<Movie> getAllMoviesRetryWhen() {
        RetryBackoffSpec retryWhen = getRetryBackoffSpec();
        Flux<MovieInfo> movieInfoFlux = movieInfoService.retrieveMoviesFlux();
        return movieInfoFlux
            .flatMap(movieInfo -> {
                Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                    .collectList();
                return reviewsMono
                    .map(reviews -> new Movie(movieInfo, reviews));
            })
            .onErrorMap(ex -> {
                log.error("Exception is: ", ex);
                if (ex instanceof NetworkException)
                    throw new MovieException(ex.getMessage());
                else
                    throw new ServiceException(ex.getMessage());
            })
            .retryWhen(retryWhen);
    }

    public Flux<Movie> getAllMoviesRepeat() {
        Flux<MovieInfo> movieInfoFlux = movieInfoService.retrieveMoviesFlux();
        return movieInfoFlux
            .flatMap(movieInfo -> {
                Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                    .collectList();
                return reviewsMono
                    .map(reviews -> new Movie(movieInfo, reviews));
            })
            .onErrorMap(ex -> {
                log.error("Exception is: ", ex);
                if (ex instanceof NetworkException)
                    throw new MovieException(ex.getMessage());
                else
                    throw new ServiceException(ex.getMessage());
            })
            .retryWhen(getRetryBackoffSpec())
            .repeat();
    }

    public Flux<Movie> getAllMoviesRepeatN(int count) {
        Flux<MovieInfo> movieInfoFlux = movieInfoService.retrieveMoviesFlux();
        return movieInfoFlux
            .flatMap(movieInfo -> {
                Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                    .collectList();
                return reviewsMono
                    .map(reviews -> new Movie(movieInfo, reviews));
            })
            .onErrorMap(ex -> {
                log.error("Exception is: ", ex);
                if (ex instanceof NetworkException)
                    throw new MovieException(ex.getMessage());
                else
                    throw new ServiceException(ex.getMessage());
            })
            .retryWhen(getRetryBackoffSpec())
            .repeat(count);
    }

    private RetryBackoffSpec getRetryBackoffSpec() {
        return Retry
            .fixedDelay(3, Duration.ofMillis(500))
            .filter(ex -> ex instanceof MovieException)
            .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) -> Exceptions.propagate(retrySignal.failure())));
    }

    public Mono<Movie> getMovieById(long id) {
        Mono<MovieInfo> movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(id);
        return movieInfoMono.flatMap(movieInfo -> {
            Mono<List<Review>> reviewsFlux = reviewService.retrieveReviewsFlux(id).collectList();
            return movieInfoMono.zipWith(reviewsFlux, Movie::new);
        });
    }

    public Mono<Movie> getMovieByIdRestClient(long id) {
        Mono<MovieInfo> movieInfoMono = movieInfoService.retrieveMoviesByIdRestClient(id);
        Mono<List<Review>> reviewsFlux = reviewService.retrieveReviewsFlux_RestClient(id).collectList();
        return movieInfoMono.zipWith(reviewsFlux, Movie::new).log();
    }

    public Mono<Movie> getMovieByIdWithRevenue(long id) {
        Mono<MovieInfo> movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(id);
        Mono<List<Review>> reviewFlux = reviewService.retrieveReviewsFlux(id)
            .collectList();

        Mono<Revenue> revenueMono = Mono.fromCallable(() -> revenueService.getRevenue(id))
            .subscribeOn(Schedulers.boundedElastic());

        return movieInfoMono.
            zipWith(reviewFlux, Movie::new)
            .zipWith(revenueMono, (movie, revenue) -> {
                movie.setRevenue(revenue);
                return movie;
            });

    }

}
