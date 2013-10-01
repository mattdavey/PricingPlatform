package pricingplatform.components.bank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pricingplatform.components.common.Payload;
import pricingplatform.components.common.PubSubBus;
import pricingplatform.services.Endpoints;
import rx.Subscription;
import rx.concurrency.Schedulers;
import rx.util.functions.Action1;

public class MatchingEngine {
    private final Logger logger = LoggerFactory.getLogger(MatchingEngine.class);
    private final Subscription marketData;
    private final String name;

    public MatchingEngine(final String name, final PubSubBus msgBus) {
        this.name = name;
        marketData = msgBus.subscribe(String.format("%s_%s", name, Endpoints.Algo_Price)).observeOn(Schedulers.threadPoolForComputation()).subscribe(new Action1<Payload>() {
            public void call(final Payload data) {
            logger.debug(String.format("Received Algo Price %s", data));
            msgBus.publish(String.format("%s_%s", name, Endpoints.Price), data);
            }
        });
    }
}
