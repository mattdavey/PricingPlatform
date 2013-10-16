package pricingplatform.actors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pricingplatform.components.common.Payload;
import pricingplatform.components.common.PubSubBus;
import pricingplatform.services.Endpoints;
import pricingplatform.services.ServiceDiscovery;

import java.util.HashMap;
import java.util.Random;

public class MarketDataService implements ServiceDiscovery {
    private final Logger logger = LoggerFactory.getLogger(MarketDataService.class);
    private final Random randomGenerator = new Random(System.nanoTime());

    private final String name;
    private final String endpoint;
    private PubSubBus msgBus;
    private final HashMap<String,Endpoints.EndpointTypes> endpoints = new HashMap<String, Endpoints.EndpointTypes>();

    public MarketDataService(final String name) {
        this.name = name;
        this.endpoint = String.format("%s_%s", name, Endpoints.MarketData);
        endpoints.put(endpoint, Endpoints.EndpointTypes.MarketData);
    }

    public void connect(final PubSubBus msgBus) {
        this.msgBus = msgBus;
    }

    public HashMap<String, Endpoints.EndpointTypes> getEndpoints() {
        return endpoints;
    }

    public void generate(final String data) {
        msgBus.publish(endpoint, new Payload(Payload.PayloadType.MarketData, data));
    }
}
