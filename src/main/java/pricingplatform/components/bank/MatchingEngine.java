package pricingplatform.components.bank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pricingplatform.components.common.Payload;
import pricingplatform.components.common.PubSubBus;
import rx.Subscription;
import rx.concurrency.Schedulers;
import rx.util.functions.Action1;

public class MatchingEngine {
    private final Logger logger = LoggerFactory.getLogger(MatchingEngine.class);
    private final Subscription marketData;

    public MatchingEngine(final PubSubBus msgBus) {
        marketData = msgBus.subscribe(PubSubBus.Algo_Price).observeOn(Schedulers.threadPoolForComputation()).subscribe(new Action1<Payload>() {
            public void call(final Payload data) {
            logger.debug(String.format("Received Algo Price %s", data));
            msgBus.publish(PubSubBus.Price, data);
            }
        });
    }
}
