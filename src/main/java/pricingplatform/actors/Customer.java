package pricingplatform.actors;

import pricingplatform.actors.roles.PriceTaker;
import pricingplatform.components.FIXEngine;

public class Customer implements PriceTaker {
    private final String name;
    private FIXEngine engine;

    public Customer(final String name) {
        this.name = name;
    }

    public void connect(final FIXEngine engine) {

        this.engine = engine;
    }

    public void send(final String message) {

    }
}
