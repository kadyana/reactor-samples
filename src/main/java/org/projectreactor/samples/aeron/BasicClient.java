package org.projectreactor.samples.aeron;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.Timers;
import reactor.aeron.processor.AeronProcessor;
import reactor.fn.Consumer;
import reactor.fn.timer.Timer;
import reactor.io.buffer.Buffer;

import java.util.concurrent.TimeUnit;

public class BasicClient {

    public static void main(String[] args) {
        AeronProcessor processor = AeronProcessor.share("client", true, true, "udp://239.1.1.1:12001", 1, 2, 3, 4);

        processor.subscribe(new Subscriber<Buffer>() {

            private Subscription subscription;

            private Timer timer = Timers.create();

            @Override
            public void onSubscribe(Subscription s) {
                subscription = s;
                timer.schedule(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) {
                        System.out.println("Sent request for 1");
                        subscription.request(1);
                    }
                }, 1, TimeUnit.SECONDS);
            }

            @Override
            public void onNext(Buffer buffer) {
                System.out.println("Received - " + buffer.asString());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Error received");
            }

            @Override
            public void onComplete() {
                System.out.println("Complete received");
            }
        });
    }

}
