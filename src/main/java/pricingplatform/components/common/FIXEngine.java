package pricingplatform.components.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pricingplatform.services.Endpoints;
import rx.Subscription;
import rx.concurrency.Schedulers;
import rx.util.functions.Action1;

public class FIXEngine {
    private final Logger logger = LoggerFactory.getLogger(FIXEngine.class);
    private final String owner;
    private final String name;
    private final PubSubBus msgBus;
    private final Subscription listener;
    private FIXEngine linkedEngine;

    public FIXEngine(final String owner, final String name, final PubSubBus msgBus) {
        this.owner = owner;
        this.name = name;
        this.msgBus = msgBus;

        listener = msgBus.subscribe(String.format("%s_%s", name, Endpoints.Normalizer_DownStream_Out)).observeOn(Schedulers.threadPoolForComputation()).subscribe(new Action1<Payload>() {
            public void call(final Payload data) {
            logger.debug(String.format("%s Received %s in engine (%s) sending to engine (%s)", String.format("%s_%s", name, Endpoints.Normalizer_DownStream_Out), data, owner, linkedEngine.owner));
            linkedEngine.send(data);
            }
        });
    }

    public void send(final Payload message) {
        final String topic = String.format("%s_%s", name, Endpoints.Normalizer_DownStream_In);
        logger.debug(String.format("(%s) Processing %s sending to %s", owner, message, topic));
        msgBus.publish(topic, message);
    }

    public void connect(final FIXEngine engine) {
        logger.debug(String.format("%s connected to %s", owner, engine.getOwner()));
        this.linkedEngine = engine;
        engine.linkedEngine = this;
    }

    public String getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public FIXEngine getLinkedEngine() {
        return linkedEngine;
    }
}
