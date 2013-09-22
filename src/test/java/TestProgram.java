import pricingplatform.actors.bank.BankCentricPricingEngine;
import pricingplatform.actors.bank.BankWithoutPriceGeneration;
import pricingplatform.actors.Customer;
import pricingplatform.actors.MarketDataService;
import pricingplatform.actors.MultiBankPlatform;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.Logon;
import quickfix.fix44.NewOrderSingle;

import java.io.IOException;
import java.sql.*;

public class TestProgram {

    public static void main(String[] args) {
        new TestProgram().run();
    }

    private void run() {
        try {
//            createDB();
            standUpComponents();
//            fixPlaying();
        } catch (Exception excepton) {
            excepton.printStackTrace();
        }
    }

    private void createDB() throws ClassNotFoundException, SQLException {
        final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
        Class.forName(driver);

        final String url = "jdbc:derby:memory:testDB;create=true";
        final Connection conn = DriverManager.getConnection(url);

        final DatabaseMetaData dbmd = conn.getMetaData();
        final ResultSet schemas = dbmd.getSchemas();
        while (schemas.next()) {
            String tableSchema = schemas.getString(1);
            String tableCatalog = schemas.getString(2);
            System.out.println(tableSchema +"::"+ tableCatalog);
        }

        final ResultSet rs = dbmd.getTables(null, "PricingPlatform", "Counterparty", null);
        if(!rs.next())
        {
            final Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE PricingPlatform.Counterparty (id INT NOT NULL, name VarChar(32))");
            stmt.execute("INSERT INTO PricingPlatform.Counterparty VALUES (1, 'ABC')");
            stmt.close();
        }

        final Statement stmt = conn.createStatement();
        final ResultSet rs1 = stmt.executeQuery("Select * from PricingPlatform.Counterparty");
        while (rs1.next()){
            System.out.println(rs1.getString(2));
        }

        stmt.close();
        conn.close();
    }

    private void fixPlaying() throws FieldNotFound, InvalidMessage {
        final quickfix.fix44.Logon logon = new Logon();
        logon.set(new Username("Bob"));

        final NewOrderSingle message = new NewOrderSingle(new ClOrdID("123"),
                                                        new Side(Side.BUY),
                                                        new TransactTime(),
                                                        new OrdType('L'));

        message.set(new Text("Cancel My Order!"));

        System.out.println(logon.toString());
        System.out.println(message.toString());

        final Message m = new Message(message.toString());
        final ClOrdID x = new ClOrdID();

        System.out.println("Got Message " + m.getField(x).getValue());
//        try {
//            Session.sendToTarget(message, "TW", "TARGET");
//        } catch (SessionNotFound sessionNotFound) {
//            sessionNotFound.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
    }

    private void standUpComponents() {
        final MarketDataService bloomberg = new MarketDataService("Bloomberg");
        final MarketDataService reuters = new MarketDataService("Reuters");

        final MultiBankPlatform mbp = new MultiBankPlatform("FXall");

        final BankWithoutPriceGeneration dresdner = new BankCentricPricingEngine("Dresdner", new MarketDataService[] {bloomberg});
        dresdner.connectToECN(mbp);

        final BankWithoutPriceGeneration merrill = new BankCentricPricingEngine("MerrillLynch", new MarketDataService[] {bloomberg, reuters});
        merrill.connectToECN(mbp);

        final Customer customer = new Customer("HedgeFund");
        customer.connectToECN(mbp);
        customer.rfq();

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}