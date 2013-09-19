package pricingplatform.actors;

import pricingplatform.actors.roles.MarketMaker;
import pricingplatform.components.FIXEngine;

public class Bank implements MarketMaker {
    private final String name;
    private final MarketDataService[] marketDataServices;

    public Bank(final String name, final MarketDataService[] marketDataServices) {
        this.name = name;
        this.marketDataServices = marketDataServices;
    }

    public void connect(final FIXEngine engine) {

    }

    public void send(final String message) {

    }
}
