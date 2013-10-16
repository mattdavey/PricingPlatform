import pricingplatform.actors.Customer;
import pricingplatform.actors.MarketDataService;
import pricingplatform.actors.MultiBankPlatform;
import pricingplatform.actors.bank.BankCentricPricingEngine;
import pricingplatform.actors.bank.BankWithoutPriceGeneration;
import pricingplatform.components.common.Payload;
import quickfix.FieldNotFound;
import quickfix.InvalidMessage;
import quickfix.Message;
import quickfix.field.*;
import quickfix.fix44.Logon;
import quickfix.fix44.NewOrderSingle;
import rx.util.functions.Action1;

import java.sql.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;

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
        final CountDownLatch latch = new CountDownLatch(4);

        final Customer customer = new Customer("HedgeFund");
        customer.getReceivedPayloads().subscribe(new Action1<Payload>() {
            public void call(Payload payload) {
                latch.countDown();
            }
        });

        final MarketDataService bloomberg = new MarketDataService("Bloomberg");
        final MarketDataService reuters = new MarketDataService("Reuters");

        final MultiBankPlatform mbp = new MultiBankPlatform("FXall");

        final BankWithoutPriceGeneration dresdner = new BankCentricPricingEngine("Dresdner", new MarketDataService[] {bloomberg});
        dresdner.connectToECN(mbp);

        final BankWithoutPriceGeneration merrill = new BankCentricPricingEngine("MerrillLynch", new MarketDataService[] {bloomberg, reuters});
        merrill.connectToECN(mbp);

        customer.connectToECN(mbp);
        customer.rfq();

        try {
            latch.await(5, TimeUnit.SECONDS);
            assertEquals("Incorrect payloads received by customer", 0, latch.getCount());
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}