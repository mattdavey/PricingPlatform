package pricingplatform.services;

import java.util.HashMap;

public interface ServiceDiscovery {
    public HashMap<String,Endpoints.EndpointTypes> getEndpoints();
}
