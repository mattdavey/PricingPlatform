package pricingplatform.actors.algo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pricingplatform.components.common.Payload;
import pricingplatform.components.common.PubSubBus;
import pricingplatform.components.models.PricingModel;
import rx.Subscription;
import rx.concurrency.Schedulers;
import rx.util.functions.Action1;

public class BasicStrategy implements Strategy {
    private final Logger logger = LoggerFactory.getLogger(BasicStrategy.class);

    private final PubSubBus msgBus;
    private Subscription marketData;
    private final PricingModel pricingModel = new PricingModel();

    public BasicStrategy(PubSubBus msgBus) {
        this.msgBus = msgBus;
    }

    public void start() {
        marketData = msgBus.subscribe(PubSubBus.MarketData).observeOn(Schedulers.threadPoolForComputation()).subscribe(new Action1<Payload>() {
            public void call(final Payload data) {
            logger.debug(String.format("Received MarketData %s", data));
            final String price = pricingModel.generatePrice(data.getData());
            logger.debug(String.format("Publishing price %s", price));
            msgBus.publish(PubSubBus.Algo_Price, new Payload(Payload.PayloadType.Price, price));
            }
        });
    }
}
