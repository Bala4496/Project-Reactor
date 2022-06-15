package ua.bala.project_reactor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class BackpressureTest {

    @Test
    void testBackPressure() throws InterruptedException {
        Flux<Integer> numberRange = Flux.range(1, 100).log();

        CountDownLatch latch = new CountDownLatch(1);
        numberRange
            .subscribe(new BaseSubscriber<>() {
                @Override
                protected void hookOnSubscribe(Subscription subscription) {
                    request(2L);
                }

                @Override
                protected void hookOnNext(Integer value) {
//                    super.hookOnNext(value);
                    log.info("hookOnNext : {}", value);
                    if (value % 2 == 0 || value < 50)
                        request(2);
                    else
                        cancel();
                }

                @Override
                protected void hookOnComplete() {
                    super.hookOnComplete();
                }

                @Override
                protected void hookOnError(Throwable throwable) {
                    super.hookOnError(throwable);
                }

                @Override
                protected void hookOnCancel() {
//                    super.hookOnCancel();
                    log.info("Inside OnCancel");
                    latch.countDown();
                }
            });
        assertTrue(latch.await(5L, TimeUnit.SECONDS));
    }

    @Test
    void testBackPressureDrop() throws InterruptedException {
        Flux<Integer> numberRange = Flux.range(1, 100).log();

        CountDownLatch latch = new CountDownLatch(1);
        numberRange
            .onBackpressureDrop(item -> log.info("Dropped items are: {}", item))
            .subscribe(new BaseSubscriber<>() {
                @Override
                protected void hookOnSubscribe(Subscription subscription) {
                    request(2L);
                }

                @Override
                protected void hookOnNext(Integer value) {
//                    super.hookOnNext(value);
                    log.info("hookOnNext : {}", value);
//                    if (value % 2 == 0 || value < 50)
//                        request(2);
//                    else
//                        cancel();
                    if (value == 2)
                        hookOnCancel();
                }

                @Override
                protected void hookOnComplete() {
                    super.hookOnComplete();
                }

                @Override
                protected void hookOnError(Throwable throwable) {
                    super.hookOnError(throwable);
                }

                @Override
                protected void hookOnCancel() {
//                    super.hookOnCancel();
                    log.info("Inside OnCancel");
                    latch.countDown();
                }
            });
        assertTrue(latch.await(5L, TimeUnit.SECONDS));
    }

    @Test
    void testBackPressureBuffer() throws InterruptedException {
        Flux<Integer> numberRange = Flux.range(1, 100).log();

        CountDownLatch latch = new CountDownLatch(1);

        numberRange
            .onBackpressureBuffer(10, item -> log.info("Last Buffered item is: {}", item))
            .subscribe(new BaseSubscriber<>() {
                @Override
                protected void hookOnSubscribe(Subscription subscription) {
                    request(1);
                }

                @Override
                protected void hookOnNext(Integer value) {
                    log.info("hookOnNext : {}", value);
                    if (value < 50)
                        request(1);
                    else
                        latch.countDown();
                }

                @Override
                protected void hookOnComplete() {
                    super.hookOnComplete();
                }

                @Override
                protected void hookOnError(Throwable throwable) {
                    super.hookOnError(throwable);
                }

                @Override
                protected void hookOnCancel() {
//                    super.hookOnCancel();
                    log.info("Inside OnCancel");
                    latch.countDown();
                }
            });
        assertTrue(latch.await(5L, TimeUnit.SECONDS));
    }

    @Test
    void testBackPressureError() throws InterruptedException {
        Flux<Integer> numberRange = Flux.range(1, 100).log();

        CountDownLatch latch = new CountDownLatch(1);

        numberRange
            .onBackpressureError()
            .subscribe(new BaseSubscriber<>() {
                @Override
                protected void hookOnSubscribe(Subscription subscription) {
                    request(1);
                }

                @Override
                protected void hookOnNext(Integer value) {
                    log.info("hookOnNext : {}", value);
                    if (value < 50)
                        request(1);
                    else
                        latch.countDown();
                }

                @Override
                protected void hookOnComplete() {
                    super.hookOnComplete();
                }

                @Override
                protected void hookOnError(Throwable throwable) {
                    super.hookOnError(throwable);
                }

                @Override
                protected void hookOnCancel() {
//                    super.hookOnCancel();
                    log.info("Inside OnCancel");
                    latch.countDown();
                }
            });
        assertTrue(latch.await(5L, TimeUnit.SECONDS));
    }
}
