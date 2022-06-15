package ua.bala.project_reactor.service;

import ua.bala.project_reactor.exception.ReactorException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ua.bala.project_reactor.util.CommonUtil;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Slf4j
public class FluxAndMonoGeneratorService {

    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("Alex", "Bob", "Alex", "Ihor"));
    }

    public Flux<String> namesFluxMap() {
        return Flux.fromIterable(List.of("Alex", "Bob", "Alex", "Ihor"))
            .map(String::toUpperCase);
    }

    public Flux<String> namesFluxMapFilter(int size) {
        return Flux.fromIterable(List.of("Alex", "Bob", "Alex", "Ihor"))
            .filter(name -> name.length() > size)
            .map(name -> name.length() + "-" + name)
            .doOnNext(name -> System.out.println("Name is: " + name));
    }

    public Flux<String> namesFluxFlatMapFilter(int size) {
        return Flux.fromIterable(List.of("Alex", "Bob", "Alex", "Ihor"))
            .map(String::toUpperCase)
            .filter(name -> name.length() > size)
            .flatMap(this::splitString)
            .log();
    }

    private Flux<String> splitString(String name) {
        return Flux.fromArray(name.split(""));
    }

    private Flux<String> splitStringWithDelay(String name) {
        int millis = new Random().nextInt(1000);
        return Flux.fromArray(name.split(""))
            .delayElements(Duration.ofMillis(millis));
    }

    public Flux<String> namesFluxFlatMapFilterAsync(int size) {
        return Flux.fromIterable(List.of("Alex", "Bob", "Alex", "Ihor"))
            .map(String::toUpperCase)
            .filter(name -> name.length() > size)
            .flatMap(this::splitStringWithDelay)
            .log();
    }

    public Flux<String> namesFluxConcatMap(int size) {
        return Flux.fromIterable(List.of("Alex", "Bob", "Alex", "Ihor"))
            .map(String::toUpperCase)
            .filter(name -> name.length() > size)
            .concatMap(this::splitStringWithDelay)
            .log();
    }

    public Flux<String> namesFluxTransform(int size) {
        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase).filter(s -> s.length() > size);

        return Flux.fromIterable(List.of("Alex", "Bob", "Alex", "Ihor"))
            .transform(filterMap)
            .flatMap(this::splitString);
    }

    public Flux<String> namesFluxTransformDefaultIfEmpty(int size) {
        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase).filter(s -> s.length() > size);
//        Flux.empty();
        return Flux.fromIterable(List.of("Alex", "Bob", "Alex", "Ihor"))
            .transform(filterMap)
            .flatMap(this::splitString)
            .defaultIfEmpty("default");
    }

    public Flux<String> namesFluxTransformSwitchIfEmpty(int size) {
        Function<Flux<String>, Flux<String>> filterMap = name ->
            name.map(String::toUpperCase)
                .filter(s -> s.length() > size)
                .flatMap(this::splitString);

        Flux<String> defaultFlux = Flux.just("default")
            .transform(filterMap);

//        Flux.empty();
        return Flux.fromIterable(List.of("Alex", "Bob", "Alex", "Ihor"))
            .transform(filterMap)
            .flatMap(this::splitString)
            .switchIfEmpty(defaultFlux);
    }

    public Flux<String> namesFluxImmutability() {
        Flux<String> namesFlux = Flux.fromIterable(List.of("Alex", "Bob", "Alex", "Ihor"));
        namesFlux.map(String::toUpperCase);
        return namesFlux;
    }

    public Mono<String> nameMono() {
        return Mono.just("Ihor").log();
    }

    public Mono<String> nameMonoMapFilter(int stringLength) {
        return Mono.just("Alex")
            .map(String::toUpperCase)
            .filter(name -> name.length() > stringLength)
            .map(name -> name.length() + "-" + name);
    }

    public Mono<List<String>> nameMonoFlatMapFilter(int stringLength) {
        return Mono.just("Alex")
            .map(String::toUpperCase)
            .filter(name -> name.length() > stringLength)
            .flatMap(this::splitStringMono);
    }

    private Mono<List<String>> splitStringMono(String name) {
        return Mono.just(List.of(name.split("")));
    }

    public Flux<String> nameMonoFlatMapManyFilter(int stringLength) {
        return Mono.just("Alex")
            .map(String::toUpperCase)
            .filter(name -> name.length() > stringLength)
            .flatMapMany(this::splitString);
    }

    public Mono<String> namesMono_map_filter(int stringLength) {
        return Mono.just("Alex")
            .map(String::toUpperCase)
            .filter(name -> name.length() > stringLength)
            .map(name -> name.length() + "-" + name)
            .defaultIfEmpty("default");
    }

    public Mono<String> namesMono_map_filter_switchIfEmpty(int stringLength) {
        Mono<String> defaultMono = Mono.just("default");
        return Mono.just("Alex")
            .map(String::toUpperCase)
            .filter(name -> name.length() > stringLength)
            .map(name -> name.length() + "-" + name)
            .switchIfEmpty(defaultMono);
    }

    public Flux<String> exploreConcat() {
        Flux<String> adcFlux = Flux.just("A", "B", "C");
        Flux<String> defFlux = Flux.just("D", "E", "F");
        return Flux.concat(adcFlux, defFlux);
    }

    public Flux<String> exploreConcatWith() {
        Flux<String> adcFlux = Flux.just("A", "B", "C");
        Flux<String> defFlux = Flux.just("D", "E", "F");
        return adcFlux.concatWith(defFlux);
    }

    public Flux<String> exploreMerge() {
        Flux<String> adcFlux = Flux.just("A", "B", "C")
            .delayElements(Duration.ofMillis(100));
        Flux<String> defFlux = Flux.just("D", "E", "F")
            .delayElements(Duration.ofMillis(125));
        return Flux.merge(adcFlux, defFlux);
    }

    public Flux<String> exploreMergeWith() {
        Flux<String> adcFlux = Flux.just("A", "B", "C")
            .delayElements(Duration.ofMillis(100));
        Flux<String> defFlux = Flux.just("D", "E", "F")
            .delayElements(Duration.ofMillis(125));
        return adcFlux.mergeWith(defFlux);
    }

    public Flux<String> exploreMergeSequential() {
        Flux<String> adcFlux = Flux.just("A", "B", "C")
            .delayElements(Duration.ofMillis(100));
        Flux<String> defFlux = Flux.just("D", "E", "F")
            .delayElements(Duration.ofMillis(125));
        return Flux.mergeSequential(adcFlux, defFlux);
    }

    public Flux<String> exploreZip() {
        Flux<String> adcFlux = Flux.just("A", "B", "C");
        Flux<String> defFlux = Flux.just("D", "E", "F");
        return Flux.zip(adcFlux, defFlux, (f, s) -> f + s);
    }

    public Flux<String> exploreZipMorePublishers() {
        Flux<String> adcFlux = Flux.just("A", "B", "C");
        Flux<String> defFlux = Flux.just("D", "E", "F");
        Flux<String> _123Flux = Flux.just("1", "2", "3");
        Flux<String> _456Flux = Flux.just("4", "5", "6");
        return Flux.zip(adcFlux, defFlux, _123Flux, _456Flux)
            .map(t4 -> t4.getT1() + t4.getT2() + t4.getT3() + t4.getT4());
    }

    public Flux<String> fluxException() {
        return Flux.just("A", "B", "C")
            .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
            .concatWith(Flux.just("D"));
    }

    public Flux<String> exploreOnErrorReturn() {
        return Flux.just("A", "B", "C")
            .concatWith(Flux.error(new IllegalStateException("Exception Occurred")))
            .onErrorReturn("D");
    }

    public Flux<String> exploreOnErrorResume(Exception e) {
        Flux<String> stringFlux = Flux.just("D", "E", "F");
        return Flux.just("A", "B", "C")
            .concatWith(Flux.error(e))
            .onErrorResume(ex -> {
                log.error("Exception is: ", ex);
                if (ex instanceof IllegalStateException)
                    return stringFlux;
                else
                    return Flux.error(ex);
            });
    }

    public Flux<String> exploreOnErrorContinue() {
        return Flux.just("A", "B", "C")
            .map(name -> {
                if (name.equals("B"))
                    throw new IllegalStateException("Exception Occurred");
                return name;
            })
            .concatWith(Flux.just("D"))
            .onErrorContinue((ex, name) -> {
                log.error("Exception is: " + ex);
                log.info("Name is: " + name);
            });
    }

    public Flux<String> exploreOnErrorMap() {
        return Flux.just("A", "B", "C")
            .map(name -> {
                if (name.equals("B"))
                    throw new IllegalStateException("Exception Occurred");
                return name;
            })
            .concatWith(Flux.just("D"))
            .onErrorMap(ex -> {
                log.error("Exception is: " + ex);
                return new ReactorException(ex, ex.getMessage());
            });
    }

    public Flux<String> exploreDoOnError() {
        return Flux.just("A", "B", "C")
            .concatWith(Flux.error(new IllegalStateException("Exception Occurred")))
            .doOnError(ex -> log.error("Exception is: ", ex));
    }

    public Mono<Object> exploreMonoDoOnError() {
        return Mono.just("B")
            .map(value -> {
                throw new RuntimeException("Exception Occurred");
            }).onErrorMap(ex -> {
                log.error("Exception is: ", ex);
                return new ReactorException(ex, ex.getMessage());
            });
    }

    public Mono<String> exploreMonoOnErrorContinue(String input) {
        return Mono.just(input)
            .map(i -> {
                if (i.equals("abc"))
                    throw new RuntimeException("Exception Occurred");
                return i;
            })
            .onErrorContinue((ex, name) -> {
                log.error("Exception is: " + ex);
                log.info("Name is: " + name);
            });
    }

    public static void main(String[] args) {

        FluxAndMonoGeneratorService service = new FluxAndMonoGeneratorService();

        service.namesFluxFlatMapFilter(1)
            .subscribe();
        System.out.println("-----------------------");
        service.namesFluxFlatMapFilterAsync(1)
            .subscribe();
    }

    public Flux<Integer> exploreGenerate() {
        return Flux.generate(
            () -> 1,
            (state, sink) -> {
                sink.next(state * 2);
                if (state == 10)
                    sink.complete();
                return state + 1;
            });
    }

    public static List<String> names() {
        CommonUtil.delay(1000);
        return List.of("alex", "bob", "joel");
    }

    public Flux<String> exploreCreate() {
        return Flux.create(
            sink -> {
                names()
                    .forEach(sink::next);
                sink.complete();
            });
    }

    public Flux<String> exploreCreateAsync() {
        return Flux.create(
            sink ->
                CompletableFuture
                    .supplyAsync(FluxAndMonoGeneratorService::names)
                    .thenAccept(names -> names.forEach((name) -> {
                        sink.next(name);
                        sink.next(name);
                    }))
                    .thenRun(() -> sendEvents(sink))
        );
    }

    public void sendEvents(FluxSink<String> sink) {
        CompletableFuture
            .supplyAsync(FluxAndMonoGeneratorService::names)
            .thenAccept(names -> names.forEach(sink::next))
            .thenRun(sink::complete);
    }

    public Mono<String> exploreCreateMono() {
        return Mono.create(sink -> {
            sink.success("alex");
        });
    }

    public Flux<String> exploreHandle() {
        return Flux.fromIterable(names())
            .handle((name, sink) -> {
                if (name.length() > 3)
                    sink.next(name.toUpperCase());
            });
    }


}
