package pricingplatform.components.bank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pricingplatform.actors.MarketDataService;
import pricingplatform.actors.algo.TradingStrategy;

public class AlgoContainer {
    private final Logger logger = LoggerFactory.getLogger(AlgoContainer.class);
    private final TradingStrategy[] strategies;

    public AlgoContainer(final MarketDataService[] marketDataProviders, final TradingStrategy[] strategies) {
        this.strategies = strategies;

        for (final TradingStrategy strategy : strategies)
            strategy.execute();
    }
}
