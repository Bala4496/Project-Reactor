package ua.bala.project_reactor.service;

import ua.bala.project_reactor.domain.Review;
import ua.bala.project_reactor.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReviewServiceTest {

    WebClient webClient = WebClient.builder()
        .baseUrl("http://localhost:8080/movies")
        .build();

    ReviewService reviewService = new ReviewService(webClient);

    @Test
    public void retrieveReviewsFlux_RestClient() {

        Flux<Review> reviewFlux = reviewService.retrieveReviewsFlux_RestClient(1);

        StepVerifier.create(reviewFlux)
            .assertNext(review ->
                assertEquals("Nolan is the real superhero", review.getComment())
            )
            .verifyComplete();
    }
}
