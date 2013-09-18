import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.Logon;

public class TestProgram {

    public static void main(String[] args) throws FieldNotFound, InvalidMessage {
        final quickfix.fix44.Logon logon = new Logon();
        logon.set(new Username("Bob"));

        final quickfix.fix44.OrderCancelRequest message = new quickfix.fix44.OrderCancelRequest(
                new OrigClOrdID("123"),
                new ClOrdID("321"),
                new Side(Side.BUY),
                new TransactTime());

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