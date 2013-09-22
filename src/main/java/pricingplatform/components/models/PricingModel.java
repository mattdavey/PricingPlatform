package pricingplatform.components.models;

public class PricingModel {
    public String generatePrice(final String data) {
        final Double marketData = Double.parseDouble(data);
        return Double.toString(marketData + 0.5);
    }
}
