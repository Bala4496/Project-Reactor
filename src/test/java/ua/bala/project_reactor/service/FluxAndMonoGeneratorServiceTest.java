package ua.bala.project_reactor.service;

import ua.bala.project_reactor.service.FluxAndMonoGeneratorService;
import ua.bala.project_reactor.exception.ReactorException;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;
import java.util.List;

public class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService service = new FluxAndMonoGeneratorService();

    @Test
    void namesFlux() {
        Flux<String> namesFlux = service.namesFlux();
        StepVerifier.create(namesFlux)
            //.expectNext("Alex", "Bob", "Alex", "Ihor")
            //.expectNextCount(4)
            .expectNext("Alex")
            .expectNextCount(3)
            .verifyComplete();
    }

    @Test
    void namesFluxMap() {
        Flux<String> namesFlux = service.namesFluxMap();
        StepVerifier.create(namesFlux)
            .expectNext("ALEX")
            .expectNextCount(3)
            .verifyComplete();
    }

    @Test
    void namesFluxMapFilter() {
        Flux<String> namesFlux = service.namesFluxMapFilter(3);
        StepVerifier.create(namesFlux)
            .expectNext("4-Alex")
            .expectNextCount(2)
            .verifyComplete();
    }

    @Test
    void namesFluxFlatMapFilter() {
        Flux<String> namesFlux = service.namesFluxFlatMapFilter(3);
        StepVerifier.create(namesFlux)
            .expectNext("A")
            .expectNextCount(11)
            .verifyComplete();
    }

    @Test
    void namesFluxFlatMapFilterAsync() {
        Flux<String> namesFlux = service.namesFluxFlatMapFilterAsync(3);
        StepVerifier.create(namesFlux)
            .expectNextCount(12)
            .verifyComplete();
    }

    @Test
    void namesFluxConcatMap() {
        Flux<String> namesFlux = service.namesFluxConcatMap(3);
        StepVerifier.create(namesFlux)
            .expectNext("A","L","E","X")
            .expectNextCount(8)
            .verifyComplete();
    }

    @Test
    void namesFluxConcatMapVirtualTimer() {
        VirtualTimeScheduler.getOrSet();
        Flux<String> namesFlux = service.namesFluxConcatMap(3);
        StepVerifier.withVirtualTime(() -> namesFlux)
            .thenAwait(Duration.ofSeconds(10))
            .expectNext("A","L","E","X","A","L","E", "X", "I", "H", "O", "R")
//            .expectNextCount(8)
            .verifyComplete();
    }

    @Test
    void namesFluxImmutability() {
        Flux<String> namesFlux = service.namesFluxImmutability();
        StepVerifier.create(namesFlux)
            .expectNext("Alex")
            .expectNextCount(3)
            .verifyComplete();
    }

    @Test
    void namesFluxTransform() {
        Flux<String> namesFlux = service.namesFluxTransform(3);
        StepVerifier.create(namesFlux)
            .expectNext("A")
            .expectNextCount(11)
            .verifyComplete();
    }

    @Test
    void namesFluxTransformDefaultIfEmpty() {
        Flux<String> namesFlux = service.namesFluxTransformDefaultIfEmpty(6);
        StepVerifier.create(namesFlux)
            .expectNext("default")
            .verifyComplete();
    }

    @Test
    void namesFluxTransformSwitchIfEmpty() {
        Flux<String> namesFlux = service.namesFluxTransformSwitchIfEmpty(6);
        StepVerifier.create(namesFlux)
            .expectNext("D", "E", "F", "A", "U", "L", "T")
            .verifyComplete();
    }

    @Test
    void nameMono() {
        Mono<String> namesMono = service.nameMono();
        StepVerifier.create(namesMono)
            .expectNext("Ihor")
            .expectNextCount(0)
            .verifyComplete();
    }

    @Test
    void nameMonoMapFilter() {
        Mono<String> namesFlux = service.nameMonoMapFilter(3);
        StepVerifier.create(namesFlux)
            .expectNext("4-ALEX")
            .expectNextCount(0)
            .verifyComplete();
    }

    @Test
    void nameMonoFlatMapFilter() {
        Mono<List<String>> namesFlux = service.nameMonoFlatMapFilter(3);
        StepVerifier.create(namesFlux)
            .expectNext(List.of("A", "L", "E","X"))
            .verifyComplete();
    }

    @Test
    void nameMonoFlatMapManyFilter() {
        Flux<String> namesFlux = service.nameMonoFlatMapManyFilter(3);
        StepVerifier.create(namesFlux)
            .expectNext("A", "L", "E","X")
            .verifyComplete();
    }

    @Test
    void namesMono_map_filter() {
        Mono<String> namesFlux = service.namesMono_map_filter(4);
        StepVerifier.create(namesFlux)
            .expectNext("default")
            .verifyComplete();
    }

    @Test
    void namesMono_map_filter_switchIfEmpty() {
        Mono<String> namesFlux = service.namesMono_map_filter_switchIfEmpty(4);
        StepVerifier.create(namesFlux)
            .expectNext("default")
            .verifyComplete();
    }

    @Test
    void exploreConcat() {
        Flux<String> value = service.exploreConcat();
        StepVerifier.create(value)
            .expectNext("A", "B", "C", "D", "E", "F")
            .verifyComplete();
    }

    @Test
    void exploreConcatWith() {
        Flux<String> concatWithFlux = service.exploreConcatWith();
        StepVerifier.create(concatWithFlux)
            .expectNext("A", "B", "C", "D", "E", "F")
            .verifyComplete();
    }

    @Test
    void exploreMerge() {
        Flux<String> value = service.exploreMerge();
        StepVerifier.create(value)
            .expectNext("A", "D", "B", "E", "C", "F")
            .verifyComplete();
    }

    @Test
    void exploreMergeWith() {
        Flux<String> value = service.exploreMergeWith();
        StepVerifier.create(value)
            .expectNext("A", "D", "B", "E", "C", "F")
            .verifyComplete();
    }

    @Test
    void exploreMergeSequential() {
        Flux<String> value = service.exploreMergeSequential();
        StepVerifier.create(value)
            .expectNext("A", "B", "C", "D", "E", "F")
            .verifyComplete();
    }

    @Test
    void exploreZip() {
        Flux<String> value = service.exploreZip();
        StepVerifier.create(value)
            .expectNext("AD", "BE", "CF")
            .verifyComplete();
    }

    @Test
    void exploreZipMorePublishers() {
        Flux<String> value = service.exploreZipMorePublishers();
        StepVerifier.create(value)
            .expectNext("AD14", "BE25", "CF36")
            .verifyComplete();
    }

    @Test
    void fluxException() {
        Flux<String> value = service.fluxException();
        StepVerifier.create(value)
            .expectNext("A", "B", "C")
            .expectError(RuntimeException.class)
            .verify();
    }

    @Test
    void exploreOnErrorReturn() {
        Flux<String> value = service.exploreOnErrorReturn();
        StepVerifier.create(value)
            .expectNext("A", "B", "C", "D")
            .verifyComplete();
    }

    @Test
    void exploreOnErrorResume() {
        Exception exception = new IllegalStateException("Not a valid State");
        Flux<String> value = service.exploreOnErrorResume(exception);
        StepVerifier.create(value)
            .expectNext("A", "B", "C", "D", "E", "F")
            .verifyComplete();
    }

    @Test
    void exploreOnErrorContinue() {
        Flux<String> value = service.exploreOnErrorContinue();
        StepVerifier.create(value)
            .expectNext("A", "C", "D")
            .verifyComplete();
    }

    @Test
    void exploreOnErrorMap() {
        Flux<String> value = service.exploreOnErrorMap();
        StepVerifier.create(value)
            .expectNext("A")
            .expectError(ReactorException.class)
            .verify();
    }

    @Test
    void exploreDoOnError() {
        Flux<String> value = service.exploreDoOnError();
        StepVerifier.create(value)
            .expectNext("A", "B", "C")
            .expectError(IllegalStateException.class)
            .verify();
    }

    @Test
    void exploreMonoOnErrorContinueWhenInputIsAbc() {
        Mono<String> value = service.exploreMonoOnErrorContinue("abc");
        StepVerifier.create(value)
            .verifyComplete();
    }

    @Test
    void exploreMonoOnErrorContinueWhenInputIsNotAbc() {
        Mono<String> value = service.exploreMonoOnErrorContinue("reactor");
        StepVerifier.create(value)
            .expectNext("reactor")
            .verifyComplete();
    }

    @Test
    void exploreGenerate() {
        Flux<Integer> exploreGenerate = service.exploreGenerate().log();
        StepVerifier.create(exploreGenerate)
            .expectNextCount(10)
            .verifyComplete();
    }

    @Test
    void exploreCreate() {
        Flux<String> exploreGenerate = service.exploreCreate().log();
        StepVerifier.create(exploreGenerate)
            .expectNextCount(3)
            .verifyComplete();
    }

    @Test
    void exploreCreateAsync() {
        Flux<String> exploreGenerate = service.exploreCreateAsync().log();
        StepVerifier.create(exploreGenerate)
            .expectNextCount(9)
            .verifyComplete();
    }

    @Test
    void exploreHandle() {
        Flux<String> exploreGenerate = service.exploreHandle().log();
        StepVerifier.create(exploreGenerate)
            .expectNextCount(2)
            .verifyComplete();
    }

    @Test
    void exploreCreateMono() {
        Mono<String> exploreGenerate = service.exploreCreateMono().log();
        StepVerifier.create(exploreGenerate)
            .expectNext("alex")
            .verifyComplete();
    }
}
