package pricingplatform.components.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.subjects.PublishSubject;

import java.util.HashMap;

public class PubSubBus {
    private final Logger logger = LoggerFactory.getLogger(PubSubBus.class);

    private final HashMap<String, PublishSubject<Payload>> topics = new HashMap<String, PublishSubject<Payload>>();
    private final Object locker = new Object();
    private final String name;

    public PubSubBus(final String name) {
        this.name = name;
    }

    public Observable<Payload> subscribe(final String topic) {
        final PublishSubject<Payload> msgBus = getTopic(topic);
        return msgBus;
    }

    private PublishSubject<Payload> getTopic(final String topic) {
        synchronized (locker) {
            PublishSubject<Payload> msgBus = topics.get(topic);
            if (null == msgBus)
            {
                msgBus = PublishSubject.create();
                topics.put(topic, msgBus);
            }
            return msgBus;
        }
    }

    public void publish(final String topic, final Payload message) {
        final PublishSubject<Payload> endPoint = getTopic(topic);
        endPoint.onNext(message);
    }

    public String getName() {
        return name;
    }
}
