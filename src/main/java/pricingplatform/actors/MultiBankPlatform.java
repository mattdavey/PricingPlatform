package pricingplatform.actors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pricingplatform.actors.marketroles.ECN;
import pricingplatform.components.bank.DataNormalization;
import pricingplatform.components.common.FIXEngine;
import pricingplatform.components.common.Payload;
import pricingplatform.components.common.PubSubBus;
import pricingplatform.services.Endpoints;
import rx.Subscription;
import rx.concurrency.Schedulers;
import rx.util.functions.Action1;

import java.util.HashMap;
import java.util.UUID;

public class MultiBankPlatform implements ECN {
    private final Logger logger = LoggerFactory.getLogger(MultiBankPlatform.class);

    public enum ConnectionType {Bank, Customer};

    private final String name;
    private final HashMap<String, ConnectionHolder> connections = new HashMap<String, ConnectionHolder>();

    class ConnectionHolder {
        final PubSubBus msgBus;
        final FIXEngine engine;
        final DataNormalization dataNormalization;
        private final String mbpClientName;
        private final Subscription sub;
        private final ConnectionType type;

        ConnectionHolder(final ConnectionType type, final String requester) {
            this.type = type;
            this.mbpClientName = String.format("%s_%s", name, requester);
            msgBus = new PubSubBus(mbpClientName);
            engine = new FIXEngine(mbpClientName, mbpClientName, msgBus);
            dataNormalization = new DataNormalization(mbpClientName, msgBus);

            sub = msgBus.subscribe(String.format("%s_%s", mbpClientName, Endpoints.Normalizer_UpStream_Out)).
                    observeOn(Schedulers.threadPoolForComputation()).
                    subscribe(new Action1<Payload>() {
                        public void call(final Payload data) {
                            switch (data.getPayloadType()) {
                                case Price:
                                    logger.debug(String.format("Received from UpStream_Out(%s) sending to all customers", msgBus.getName(), data));

                                    for (final ConnectionHolder customer : connections.values()) {
                                        if (customer.type == ConnectionType.Customer) {
                                            customer.engine.getLinkedEngine().send(data);
                                        }
                                    }
                                    break;
                                case RFQ:
                                    logger.debug(String.format("Received from UpStream_Out(%s) sending to all banks", msgBus.getName(), data));

                                    for (final ConnectionHolder bank : connections.values()) {
                                        if (bank.type == ConnectionType.Bank) {
                                            bank.engine.getLinkedEngine().send(data);
                                        }
                                    }
                                    break;
                                case Quote:
                                    logger.debug(String.format("Received from UpStream_Out(%s) sending back to customer", msgBus.getName(), data));

                                    // This is wrong, as it should be to the originating customer
                                    for (final ConnectionHolder bank : connections.values()) {
                                        if (bank.type == ConnectionType.Customer) {
                                            bank.engine.getLinkedEngine().send(data);
                                        }
                                    }
                                    break;
                            }
                        }
                    }, new Action1<Throwable>() {
                                  public void call(Throwable throwable) {
                                      logger.error("Normalizer_UpStream_Out subscription", throwable);
                                  }
                              }
                    );
        }
    }

    public MultiBankPlatform(final String name) {
        this.name = name;
    }

    public FIXEngine getFIXEngine(final ConnectionType type, final String requester) {
        ConnectionHolder engineHolder = connections.get(requester);
        if (null == engineHolder) {
            engineHolder = new ConnectionHolder(type, requester);
            connections.put(requester, engineHolder);
        }

        return engineHolder.engine;
    }
}
