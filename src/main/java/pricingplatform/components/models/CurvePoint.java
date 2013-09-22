package pricingplatform.components.models;

public class CurvePoint {
    private final double data;
    private final Tenor.Tenors tenor;

    public CurvePoint(double data, Tenor.Tenors tenor) {
        this.data = data;
        this.tenor = tenor;
    }
}
