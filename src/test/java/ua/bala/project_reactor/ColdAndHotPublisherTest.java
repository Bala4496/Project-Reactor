package ua.bala.project_reactor;

import org.junit.jupiter.api.Test;
import reactor.core.Disposable;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static ua.bala.project_reactor.util.CommonUtil.delay;


public class ColdAndHotPublisherTest {

    @Test
    public void coldPublisherTest() {

        Flux<Integer> flux = Flux.range(1, 10);

        flux.subscribe(i -> System.out.println("Subscriber 1 : " + i));
        flux.subscribe(i -> System.out.println("Subscriber 2 : " + i));
    }

    @Test
    public void hotPublisherTest() {

        Flux<Integer> flux = Flux.range(1, 10)
            .delayElements(Duration.ofSeconds(1));

        ConnectableFlux<Integer> connectableFlux = flux.publish();
        connectableFlux.connect();

        connectableFlux.subscribe(i -> System.out.println("Subscriber 1 : " + i));
        delay(4000);

        connectableFlux.subscribe(i -> System.out.println("Subscriber 2 : " + i));
        delay(10000);
    }

    @Test
    public void hotPublisherTestAutoConnect() {

        Flux<Integer> flux = Flux.range(1, 10)
            .delayElements(Duration.ofSeconds(1));

        Flux<Integer> connectableFlux = flux.publish().autoConnect(2);

        connectableFlux.subscribe(i -> System.out.println("Subscriber 1 : " + i));
        delay(2000);

        connectableFlux.subscribe(i -> System.out.println("Subscriber 2 : " + i));
        System.out.println("Two subscribers are connected");
        delay(2000);

        connectableFlux.subscribe(i -> System.out.println("Subscriber 3 : " + i));
        delay(10000);
    }

    @Test
    public void hotPublisherTestRefCount() {

        Flux<Integer> flux = Flux.range(1, 10)
            .delayElements(Duration.ofSeconds(1))
            .doOnCancel(() -> {
                System.out.println("Received Cancer Signal");
            });

        Flux<Integer> connectableFlux = flux.publish().refCount(2);

        Disposable disposable = connectableFlux.subscribe(i -> System.out.println("Subscriber 1 : " + i));
        delay(2000);

        Disposable disposable1 = connectableFlux.subscribe(i -> System.out.println("Subscriber 2 : " + i));
        System.out.println("Two subscribers are connected");
        delay(2000);
        disposable.dispose();
        disposable1.dispose();
        connectableFlux.subscribe(i -> System.out.println("Subscriber 3 : " + i));
        delay(2000);
        connectableFlux.subscribe(i -> System.out.println("Subscriber 4 : " + i));
        delay(10000);
    }

}
