package pricingplatform.services;

public class Endpoints {
    public enum EndpointTypes {MarketData, RFQ, RFS, ESP, Algo};

    public static String MarketData = "MarketData";
    public static String CustomerRFQ = "CustomerRFQ";
    public static String Price = "Price";
    public static String Algo_Price = "Algo_Price";
    public static String Curve = "Curve";
    public static String Normalizer_DownStream_In = "Normalizer_DownStream_In";
    public static String Normalizer_DownStream_Out = "Normalizer_DownStream_Out";
    public static String Normalizer_UpStream_In = "Normalizer_UpStream_In";
    public static String Normalizer_UpStream_Out = "Normalizer_UpStream_Out";
}
