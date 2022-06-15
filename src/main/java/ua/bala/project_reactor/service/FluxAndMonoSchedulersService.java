package ua.bala.project_reactor.service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Schedulers;
import ua.bala.project_reactor.util.CommonUtil;

import java.util.List;

@Slf4j
public class FluxAndMonoSchedulersService {

    static List<String> namesList = List.of("Alex", "Bob", "Helen", "Ihor");
    static List<String> namesList1 = List.of("Adam", "Jill", "Sarah", "Rob");

    public Flux<String> explorePublishOn() {

        Flux<String> nameFlux = flux1(namesList)
            .publishOn(Schedulers.boundedElastic())
            .log();

        Flux<String> nameFlux1 = flux1(namesList1)
            .map(name -> {
                log.info("Name is: " + name);
                return name;
            })
            .log();

        return nameFlux.mergeWith(nameFlux1);
    }

    public Flux<String> exploreSubscribeOn() {

        Flux<String> nameFlux = flux1(namesList)
            .subscribeOn(Schedulers.boundedElastic())
            .log();

        Flux<String> nameFlux1 = flux1(namesList1)
            .subscribeOn(Schedulers.boundedElastic())
            .map(name -> {
                log.info("Name is: " + name);
                return name;
            })
            .log();

        return nameFlux.mergeWith(nameFlux1);
    }

    public ParallelFlux<String> exploreParallel() {

        int processors = Runtime.getRuntime().availableProcessors();
        log.info("No of cores : {}", processors);
        return Flux.fromIterable(namesList)
            //.publishOn(Schedulers.parallel())
            .parallel()
            .runOn(Schedulers.parallel())
            .map(this::upperCase)
            .log();
    }

    public Flux<String> exploreParallelViaFlatMap() {

        return Flux.fromIterable(namesList)
            .flatMapSequential(name ->
                Mono.just(name)
                    .map(this::upperCase)
                    .subscribeOn(Schedulers.parallel())
            )
            .log();
    }

    public Flux<String> exploreParallelViaFlatMapSequential() {

        return Flux.fromIterable(namesList)
            .flatMap(name ->
                Mono.just(name)
                    .map(this::upperCase)
                    .subscribeOn(Schedulers.parallel())
            )
            .log();
    }

    public Flux<String> exploreParallelViaFlatMap_1() {

        Flux<String> nameFlux = Flux.fromIterable(namesList)
            .flatMap(name ->
                Mono.just(name)
                    .map(this::upperCase)
                    .subscribeOn(Schedulers.parallel())
            )
            .log();

        Flux<String> nameFlux1 = Flux.fromIterable(namesList1)
            .flatMap(name ->
                Mono.just(name)
                    .map(this::upperCase)
                    .subscribeOn(Schedulers.parallel())
            )
            .map(name -> {
                log.info("Name is: " + name);
                return name;
            })
            .log();

        return nameFlux.mergeWith(nameFlux1);
    }

    private String upperCase(String name) {
        CommonUtil.delay(1000);
        return name.toUpperCase();
    }

    private Flux<String> flux1(List<String> namesList) {
        return Flux.fromIterable(namesList)
            .map(this::upperCase);
    }
}
