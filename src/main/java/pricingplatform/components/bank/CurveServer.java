package pricingplatform.components.bank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pricingplatform.actors.MarketDataService;
import pricingplatform.components.common.Payload;
import pricingplatform.components.common.PubSubBus;
import pricingplatform.components.models.Curve;
import pricingplatform.components.models.CurvePoint;
import pricingplatform.components.models.Tenor;
import pricingplatform.services.Endpoints;
import rx.Subscription;
import rx.concurrency.Schedulers;
import rx.util.functions.Action1;

public class CurveServer {
    private final Logger logger = LoggerFactory.getLogger(CurveServer.class);

    private final PubSubBus msgBus;
    private final Subscription marketData;

    public CurveServer(final MarketDataService[] marketDataProviders, final PubSubBus msgBus) {
        this.msgBus = msgBus;

        marketData = msgBus.subscribe(Endpoints.MarketData).observeOn(Schedulers.threadPoolForComputation()).subscribe(new Action1<Payload>() {
            public void call(final Payload data) {
                logger.debug(String.format("Received MarketData %s", data));
                final Curve curve = new Curve(new CurvePoint[] {new CurvePoint(1.0, Tenor.Tenors.D1),
                                                                new CurvePoint(2.0, Tenor.Tenors.D2),
                                                                new CurvePoint(1.0, Tenor.Tenors.D3),
                                                                new CurvePoint(3.0, Tenor.Tenors.D4)});
                logger.debug(String.format("Publishing curve"));
                msgBus.publish(Endpoints.Curve, new Payload(Payload.PayloadType.Curve, curve.toString()));
            }
        });
    }
}
