package pricingplatform.actors.bank;


import pricingplatform.actors.MarketDataService;
import pricingplatform.components.bank.CurveServer;
import pricingplatform.components.bank.PricingEngine;
import pricingplatform.components.models.PricingModel;

public class BankCentricPricingEngine extends BankWithoutPriceGeneration {
    private final PricingEngine pricingEngine;
    private final CurveServer curves;

    public BankCentricPricingEngine(final String name, final MarketDataService[] marketDataProviders) {
        super(name, marketDataProviders);

        curves = new CurveServer(getMsgBus());
        pricingEngine = new PricingEngine(getMsgBus(), new PricingModel());
    }
}
