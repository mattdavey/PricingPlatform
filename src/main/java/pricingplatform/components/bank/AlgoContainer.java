package pricingplatform.components.bank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pricingplatform.actors.algo.Strategy;

public class AlgoContainer {
    private final Logger logger = LoggerFactory.getLogger(AlgoContainer.class);
    private final Strategy[] strategies;

    public AlgoContainer(Strategy[] strategies) {
        this.strategies = strategies;

        for (final Strategy strategy : strategies)
            strategy.start();
    }
}
