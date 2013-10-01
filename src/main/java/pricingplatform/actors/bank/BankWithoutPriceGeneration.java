package pricingplatform.actors.bank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pricingplatform.actors.MarketDataService;
import pricingplatform.actors.MultiBankPlatform;
import pricingplatform.actors.marketroles.MarketMaker;
import pricingplatform.components.bank.DataNormalization;
import pricingplatform.components.common.FIXEngine;
import pricingplatform.components.common.Payload;
import pricingplatform.components.common.PubSubBus;
import pricingplatform.services.Endpoints;
import rx.Subscription;
import rx.util.functions.Action1;

import java.util.HashMap;
import java.util.UUID;

public abstract class BankWithoutPriceGeneration implements MarketMaker {
    private final Logger logger = LoggerFactory.getLogger(BankWithoutPriceGeneration.class);

    private final String name;
    private final PubSubBus msgBus;
    protected PubSubBus getMsgBus() {
        return msgBus;
    }

    private final MarketDataService[] marketDataProviders;

    private final DataNormalization dataNormalization;
    private final FIXEngine engine;

    private final Subscription subscribe;
    private final Subscription subscribe_prices;

    private final String endpoint;
    private final HashMap<String,Endpoints.EndpointTypes> endpoints = new HashMap<String, Endpoints.EndpointTypes>();

    public BankWithoutPriceGeneration(final String name, final MarketDataService[] marketDataProviders) {
        this.name = name;
        this.marketDataProviders = marketDataProviders;
        this.endpoint = String.format("%s_%s", name, Endpoints.MarketData);
        endpoints.put(endpoint, Endpoints.EndpointTypes.MarketData);

        msgBus = new PubSubBus(name);
        dataNormalization = new DataNormalization(name, msgBus);
        engine = new FIXEngine(name, name, msgBus);

        for (final MarketDataService marketDataProvider : marketDataProviders) {
            marketDataProvider.connect(msgBus);
        }

        subscribe = msgBus.subscribe(String.format("%s_%s", name, Endpoints.Normalizer_UpStream_Out)).subscribe(new Action1<Payload>() {
            public void call(final Payload data) {
                logger.debug(String.format("%s received %s", String.format("%s_%s", name, Endpoints.Normalizer_UpStream_Out), data));
                if (data.getPayloadType() == Payload.PayloadType.RFQ) {
                    msgBus.publish(String.format("%s_%s", name, Endpoints.Normalizer_UpStream_In), new Payload(Payload.PayloadType.Quote, "QUOTE"));
                }
            }
        });

        subscribe_prices = msgBus.subscribe(String.format("%s_%s", name, Endpoints.Price)).subscribe(new Action1<Payload>() {
            public void call(final Payload data) {
                logger.debug(String.format("%s Sending price (%s) to %s", String.format("%s_%s", name, Endpoints.Price), data, String.format("%s_%s", name, Endpoints.Normalizer_UpStream_In)));
                msgBus.publish(String.format("%s_%s", name, Endpoints.Normalizer_UpStream_In), data);
            }
        });
    }

    public void connectToECN(final MultiBankPlatform mbp) {
        engine.connect(mbp.getFIXEngine(MultiBankPlatform.ConnectionType.Bank, name));
    }
}
