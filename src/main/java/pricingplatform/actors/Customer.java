package pricingplatform.actors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pricingplatform.actors.marketroles.PriceTaker;
import pricingplatform.components.bank.DataNormalization;
import pricingplatform.components.common.FIXEngine;
import pricingplatform.components.common.Payload;
import pricingplatform.components.common.PubSubBus;
import pricingplatform.services.Endpoints;
import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;
import rx.util.functions.Action1;

public class Customer implements PriceTaker {
    private final Logger logger = LoggerFactory.getLogger(Customer.class);

    private final String name;

    private final PubSubBus msgBus;
    private final DataNormalization dataNormalization;
    private final FIXEngine engine;
    private final Subscription sub;
    private MultiBankPlatform mbp;
    private final PublishSubject<Payload> receivedPayloads = PublishSubject.create();

    public Customer(final String name) {
        this.name = name;

        msgBus = new PubSubBus(name);
        dataNormalization = new DataNormalization(name, msgBus);
        this.engine = new FIXEngine(name, name, msgBus);

        sub = msgBus.subscribe(String.format("%s_%s", name, Endpoints.Normalizer_UpStream_Out)).subscribe(new Action1<Payload>() {
            public void call(final Payload payload) {
                receivedPayloads.onNext(payload);
                if (payload.getPayloadType() == Payload.PayloadType.Quote) {
                    logger.debug(String.format("Quote received %s", payload));
                } else {
                    logger.debug(String.format("Price received %s", payload));
                }
            }
        });
    }

    public Observable<Payload> getReceivedPayloads() {
        return receivedPayloads;
    }

    public void connectToECN(final MultiBankPlatform mbp) {
        this.mbp = mbp;
        engine.connect(mbp.getFIXEngine(MultiBankPlatform.ConnectionType.Customer, name));
    }

    public void rfq() {
        msgBus.publish(String.format("%s_%s", name, Endpoints.Normalizer_UpStream_In), new Payload(Payload.PayloadType.RFQ, "RFQ"));
    }
}
