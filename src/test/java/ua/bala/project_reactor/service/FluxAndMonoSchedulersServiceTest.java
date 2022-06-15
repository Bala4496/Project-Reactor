package ua.bala.project_reactor.service;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ParallelFlux;
import reactor.test.StepVerifier;
import ua.bala.project_reactor.service.FluxAndMonoSchedulersService;

class FluxAndMonoSchedulersServiceTest {


    private FluxAndMonoSchedulersService schedulersService = new FluxAndMonoSchedulersService();
    @Test
    void explorePublishOn() {

        Flux<String> flux = schedulersService.explorePublishOn();

        StepVerifier.create(flux)
            .expectNextCount(8)
            .verifyComplete();

    }

    @Test
    void exploreSubscribeOn() {

        Flux<String> flux = schedulersService.exploreSubscribeOn();

        StepVerifier.create(flux)
            .expectNextCount(8)
            .verifyComplete();
    }

    @Test
    void exploreParallel() {

        ParallelFlux<String> flux = schedulersService.exploreParallel();

        StepVerifier.create(flux)
            .expectNextCount(4)
            .verifyComplete();
    }

    @Test
    void exploreParallelViaFlatMap() {

        Flux<String> flux = schedulersService.exploreParallelViaFlatMap();

        StepVerifier.create(flux)
            .expectNextCount(4)
            .verifyComplete();
    }

    @Test
    void exploreParallelViaFlatMap_1() {

        Flux<String> flux = schedulersService.exploreParallelViaFlatMap_1();

        StepVerifier.create(flux)
            .expectNextCount(8)
            .verifyComplete();
    }

    @Test
    void exploreParallelViaFlatMapSequential() {

        Flux<String> flux = schedulersService.exploreParallelViaFlatMapSequential();

        StepVerifier.create(flux)
            .expectNextCount(4)
            .verifyComplete();
    }
}