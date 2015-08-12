package pricingplatform.actors.bank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pricingplatform.actors.MarketDataService;
import pricingplatform.actors.algo.BasicStrategy;
import pricingplatform.actors.algo.TradingStrategy;
import pricingplatform.components.bank.AlgoContainer;
import pricingplatform.components.bank.MatchingEngine;

public class BankCentricAlgoPricing extends BankWithoutPriceGeneration {
    private final Logger logger = LoggerFactory.getLogger(BankCentricAlgoPricing.class);

    private final AlgoContainer algoContainer;
    private final MatchingEngine matchingEngine;

    public BankCentricAlgoPricing(final String name, final MarketDataService[] marketDataProviders) {
        super(name, marketDataProviders);

        algoContainer = new AlgoContainer(marketDataProviders, new TradingStrategy[] {new BasicStrategy(name, getMsgBus())});
        matchingEngine = new MatchingEngine(name, getMsgBus());
    }
}
