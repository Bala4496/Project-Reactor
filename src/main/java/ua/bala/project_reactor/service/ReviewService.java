package ua.bala.project_reactor.service;

import ua.bala.project_reactor.domain.Review;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

import java.util.List;

public class ReviewService {

    private WebClient webClient;

    public ReviewService() {}
    public ReviewService(WebClient webClient) {
        this.webClient = webClient;
    }

    public  List<Review> retrieveReviews(long movieId){

        List<Review> reviewsList = List.of(new Review(movieId, "Awesome Movie", 8.9),
                new Review(movieId, "Excellent Movie", 9.0));
        return reviewsList;
    }

    public Flux<Review> retrieveReviewsFlux_RestClient(long movieId){

        String uriString = UriComponentsBuilder.fromUriString("/v1/review")
            .queryParam("movieInfoId", movieId)
            .buildAndExpand()
            .toUriString();

        return webClient.get().uri(uriString)
            .retrieve()
            .bodyToFlux(Review.class);
    }

    public Flux<Review> retrieveReviewsFlux(long MovieId){

        List<Review> reviewsList = List.of(new Review(MovieId, "Awesome Movie", 8.9),
            new Review(MovieId, "Excellent Movie", 9.0));
        return Flux.fromIterable(reviewsList);
    }
}
