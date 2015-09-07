package org.projectreactor.samples.aeron;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.aeron.processor.AeronProcessor;
import reactor.io.buffer.Buffer;

public class BasicServer {

    public static void main(String[] args) {
        AeronProcessor processor = AeronProcessor.share("server", true, true, "udp://239.1.1.1:12001", 1, 2, 3, 4);

        Publisher publisher = new Publisher() {

            class MySubscription implements Subscription {

                private final Subscriber subscriber;

                MySubscription(Subscriber subscriber) {
                    this.subscriber = subscriber;
                }

                @Override
                public void request(long n) {
                    System.out.println("Requested: " + n);

                    for (int i = 0; i < n; i++) {
                        subscriber.onNext(Buffer.wrap("Tick"));
                    }
                }

                @Override
                public void cancel() {
                    System.out.println("Cancelled");
                }

            }

            @Override
            public void subscribe(Subscriber subscriber) {
                subscriber.onSubscribe(new MySubscription(subscriber));
            }
        };

        publisher.subscribe(processor);
    }

}
