package pricingplatform.components.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Subscription;
import rx.concurrency.Schedulers;
import rx.util.functions.Action1;

public class FIXEngine {
    private final Logger logger = LoggerFactory.getLogger(FIXEngine.class);
    private final String owner;
    private final PubSubBus msgBus;
    private final Subscription listener;
    private FIXEngine linkedEngine;

    public FIXEngine(final String owner, final PubSubBus msgBus) {
        this.owner = owner;
        this.msgBus = msgBus;

        listener = msgBus.subscribe(PubSubBus.Normalizer_DownStream_Out).observeOn(Schedulers.threadPoolForComputation()).subscribe(new Action1<Payload>() {
            public void call(final Payload data) {
            logger.debug(String.format("Received %s in engine (%s) sending to engine (%s)", data, owner, linkedEngine.owner));
            linkedEngine.send(data);
            }
        });
    }

    public void send(final Payload message) {
        logger.debug(String.format("Processing %s (%s)", message, owner));
        msgBus.publish(PubSubBus.Normalizer_DownStream_In, message);
    }

    public void connect(final FIXEngine engine) {
        logger.debug(String.format("%s connected to %s", owner, engine.getName()));
        this.linkedEngine = engine;
        engine.linkedEngine = this;
    }

    public String getName() {
        return owner;
    }

    public FIXEngine getLinkedEngine() {
        return linkedEngine;
    }
}
