import pricingplatform.actors.Bank;
import pricingplatform.actors.Customer;
import pricingplatform.actors.MarketDataService;import pricingplatform.components.DataNormalization;
import pricingplatform.components.FIXEngine;
import pricingplatform.actors.MultiBankPlatform;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.Logon;
import quickfix.fix44.NewOrderSingle;

import java.sql.*;

public class TestProgram {

    public static void main(String[] args) {
        new TestProgram().run();
    }

    private void run() {
        try {
            createDB();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        standUpComponents();


        try {
            fixPlaying();
        } catch (FieldNotFound fieldNotFound) {
            fieldNotFound.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvalidMessage invalidMessage) {
            invalidMessage.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void createDB() throws ClassNotFoundException, SQLException {
        final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
        Class.forName(driver);

        final String url = "jdbc:derby:memory:testDB;create=true";
        Connection conn = DriverManager.getConnection(url);

        final DatabaseMetaData dbmd = conn.getMetaData();
        ResultSet schemas = dbmd.getSchemas();
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

    private void standUpComponents() {

        final MarketDataService bbg = new MarketDataService("BBG");
        final MarketDataService reuters = new MarketDataService("Reuters");

        final Bank bank1 = new Bank("Dresdner", new MarketDataService[] {bbg});
        final Bank bank2 = new Bank("Merrill Lynch", new MarketDataService[] {bbg, reuters} );
        final Customer customer = new Customer("Hedge Fund");

        final DataNormalization dataNormalization = new DataNormalization();
        final FIXEngine fixEngine1 = new FIXEngine(dataNormalization);
        final FIXEngine fixEngine2 = new FIXEngine(dataNormalization);
        final FIXEngine fixEngine3 = new FIXEngine(dataNormalization);
        final MultiBankPlatform mbp = new MultiBankPlatform("FXall", new FIXEngine[]{fixEngine1, fixEngine2, fixEngine3});

        bank1.connect(fixEngine1);
        bank2.connect(fixEngine2);
        customer.connect(fixEngine3);

        bank1.send("FIX Price");
        bank2.send("FIX Price");
        customer.send("RFQ");
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
}