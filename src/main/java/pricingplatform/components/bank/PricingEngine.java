package pricingplatform.components.bank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pricingplatform.actors.MarketDataService;
import pricingplatform.components.common.Payload;
import pricingplatform.components.common.PubSubBus;
import pricingplatform.components.models.PricingModel;
import pricingplatform.services.Endpoints;
import rx.Subscription;
import rx.concurrency.Schedulers;
import rx.util.functions.Action1;

import java.util.HashMap;

public class PricingEngine {
    private final Logger logger = LoggerFactory.getLogger(PricingEngine.class);

    private final String name;
    private final PubSubBus msgBus;
    private final PricingModel pricingModel;
    private final Subscription[] marketData;
    private final String publishEndpoint;

    public PricingEngine(final String name, final MarketDataService[] marketDataProviders, final PubSubBus msgBus, final PricingModel pricingModel) {
        this.name = name;
        this.msgBus = msgBus;
        this.pricingModel = pricingModel;
        marketData = new Subscription[marketDataProviders.length];
        publishEndpoint = String.format("%s_%s", name, Endpoints.Price);

        int i=0;
        for (final MarketDataService service : marketDataProviders) {
            final HashMap<String, Endpoints.EndpointTypes> serviceEndpoints = service.getEndpoints();

            for (final String endpoint : serviceEndpoints.keySet()) {
                marketData[i++] = msgBus.subscribe(endpoint).observeOn(Schedulers.threadPoolForComputation()).subscribe(new Action1<Payload>() {
                    public void call(final Payload data) {
                    logger.debug(String.format("Received %s from %s", data, endpoint));
                    final String price = pricingModel.generatePrice(data.getData());
                    logger.debug(String.format("Publishing price %s on %s", price, publishEndpoint));
                    msgBus.publish(publishEndpoint, new Payload(Payload.PayloadType.Price, price));
                    }
                });
            }
        }
    }
}
