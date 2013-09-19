package pricingplatform.actors;

import pricingplatform.actors.roles.ECN;
import pricingplatform.components.FIXEngine;

public class MultiBankPlatform implements ECN {
    private final String name;
    private final FIXEngine[] fixEngines;

    public MultiBankPlatform(final String name, final FIXEngine[] fixEngines) {
        this.name = name;
        this.fixEngines = fixEngines;
    }
}
