package pricingplatform.actors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pricingplatform.actors.marketroles.PriceTaker;
import pricingplatform.components.common.FIXEngine;
import pricingplatform.components.common.Payload;
import pricingplatform.components.common.PubSubBus;
import pricingplatform.components.bank.DataNormalization;
import rx.Subscription;
import rx.util.functions.Action1;

public class Customer implements PriceTaker {
    private final Logger logger = LoggerFactory.getLogger(Customer.class);

    private final String name;

    private final PubSubBus msgBus;
    private final DataNormalization dataNormalization;
    private final FIXEngine engine;
    private final Subscription sub;

    public Customer(final String name) {
        this.name = name;

        msgBus = new PubSubBus(name);
        dataNormalization = new DataNormalization(msgBus);
        this.engine = new FIXEngine(name, msgBus);

        sub = msgBus.subscribe(PubSubBus.Normalizer_UpStream_Out).subscribe(new Action1<Payload>() {
            public void call(final Payload payload) {
                if (payload.getPayloadType() == Payload.PayloadType.Quote) {
                    logger.debug(String.format("Quote received %s", payload));
                }
            }
        });
    }

    public void connectToECN(final MultiBankPlatform mbp) {
        engine.connect(mbp.getFIXEngine(MultiBankPlatform.ConnectionType.Customer, name));
    }

    public void rfq() {
        msgBus.publish(PubSubBus.Normalizer_UpStream_In, new Payload(Payload.PayloadType.RFQ, "RFQ"));
    }
}
