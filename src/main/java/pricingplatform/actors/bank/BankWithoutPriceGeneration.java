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
import rx.Subscription;
import rx.util.functions.Action1;

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

    public BankWithoutPriceGeneration(final String name, final MarketDataService[] marketDataProviders) {
        this.name = name;
        this.marketDataProviders = marketDataProviders;

        msgBus = new PubSubBus(name);
        dataNormalization = new DataNormalization(msgBus);
        engine = new FIXEngine(name, msgBus);

        for (final MarketDataService marketDataProvider : marketDataProviders) {
            marketDataProvider.connect(msgBus);
        }

        subscribe = msgBus.subscribe(PubSubBus.Normalizer_UpStream_Out).subscribe(new Action1<Payload>() {
            public void call(final Payload data) {
                logger.debug(String.format("%s received %s", name, data));
                if (data.getPayloadType() == Payload.PayloadType.RFQ) {
                    msgBus.publish(PubSubBus.Normalizer_UpStream_In, new Payload(Payload.PayloadType.Quote, "QUOTE"));
                }
            }
        });

        subscribe_prices = msgBus.subscribe(PubSubBus.Price).subscribe(new Action1<Payload>() {
            public void call(final Payload data) {
                logger.debug(String.format("%s Sending price (%s) to FIX engine", name, data));
                msgBus.publish(PubSubBus.Normalizer_UpStream_In, data);
            }
        });
    }

    public void connectToECN(final MultiBankPlatform mbp) {
        engine.connect(mbp.getFIXEngine(MultiBankPlatform.ConnectionType.Bank, name));
    }
}
