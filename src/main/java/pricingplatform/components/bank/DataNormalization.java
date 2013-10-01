package pricingplatform.components.bank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pricingplatform.components.common.Payload;
import pricingplatform.components.common.PubSubBus;
import pricingplatform.services.Endpoints;
import rx.Subscription;
import rx.concurrency.Schedulers;
import rx.util.functions.Action1;

public class DataNormalization {
    private final Logger logger = LoggerFactory.getLogger(DataNormalization.class);

    private final String name;
    private final PubSubBus msgBus;
    private final Subscription input;
    private final Subscription output;

    public DataNormalization(final String name, final PubSubBus msgBus) {
        this.name = name;
        this.msgBus = msgBus;

        final String topicDownIn = String.format("%s_%s", name, Endpoints.Normalizer_DownStream_In);
        logger.debug("Subscribing to "+topicDownIn);
        input = msgBus.subscribe(topicDownIn).observeOn(Schedulers.threadPoolForComputation()).subscribe(new Action1<Payload>() {
            public void call(final Payload data) {
                logger.debug(String.format("Received from %s_DownStream_In processing sending to %s_UpStream_Out %s", name, name, data));
                msgBus.publish(String.format("%s_%s", name, Endpoints.Normalizer_UpStream_Out), data);
            }
        });

        final String topicUpIn = String.format("%s_%s", name, Endpoints.Normalizer_UpStream_In);
        logger.debug("Subscribing to "+topicUpIn);
        output = msgBus.subscribe(topicUpIn).observeOn(Schedulers.threadPoolForComputation()).subscribe(new Action1<Payload>() {
            public void call(final Payload data) {
                logger.debug(String.format("Received from %s_UpStream_In processing sending to %s_DownStream_Out %s", name, name, data));
                msgBus.publish(String.format("%s_%s", name, Endpoints.Normalizer_DownStream_Out), data);
            }
        });
    }
}
