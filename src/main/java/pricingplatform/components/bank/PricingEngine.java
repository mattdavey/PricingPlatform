package pricingplatform.components.bank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pricingplatform.components.common.Payload;
import pricingplatform.components.common.PubSubBus;
import pricingplatform.components.models.PricingModel;
import rx.Subscription;
import rx.concurrency.Schedulers;
import rx.util.functions.Action1;

public class PricingEngine {
    private final Logger logger = LoggerFactory.getLogger(PricingEngine.class);

    private final PubSubBus msgBus;
    private final PricingModel pricingModel;
    private final Subscription marketData;

    public PricingEngine(final PubSubBus msgBus, final PricingModel pricingModel) {
        this.msgBus = msgBus;
        this.pricingModel = pricingModel;

        marketData = msgBus.subscribe(PubSubBus.MarketData).observeOn(Schedulers.threadPoolForComputation()).subscribe(new Action1<Payload>() {
            public void call(final Payload data) {
            logger.debug(String.format("Received MarketData %s", data));
            final String price = pricingModel.generatePrice(data.getData());
            logger.debug(String.format("Publishing price %s", price));
            msgBus.publish(PubSubBus.Price, new Payload(Payload.PayloadType.Price, price));
            }
        });
    }
}
