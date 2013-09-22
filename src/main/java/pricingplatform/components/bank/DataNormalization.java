package pricingplatform.components.bank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pricingplatform.components.common.Payload;
import pricingplatform.components.common.PubSubBus;
import rx.Subscription;
import rx.concurrency.Schedulers;
import rx.util.functions.Action1;

public class DataNormalization {
    private final Logger logger = LoggerFactory.getLogger(DataNormalization.class);

    private final PubSubBus msgBus;
    private final Subscription input;
    private final Subscription output;

    public DataNormalization(final PubSubBus msgBus) {
        this.msgBus = msgBus;

        input = msgBus.subscribe(PubSubBus.Normalizer_DownStream_In).observeOn(Schedulers.threadPoolForComputation()).subscribe(new Action1<Payload>() {
            public void call(final Payload data) {
            logger.debug(String.format("Received from DownStream_In(%s) processing sending to UpStream_Out %s", msgBus.getName(), data));
            msgBus.publish(PubSubBus.Normalizer_UpStream_Out, data);
            }
        });

        output = msgBus.subscribe(PubSubBus.Normalizer_UpStream_In).observeOn(Schedulers.threadPoolForComputation()).subscribe(new Action1<Payload>() {
            public void call(final Payload data) {
                logger.debug(String.format("Received from UpStream_In(%s) processing sending to DownStream_Out %s", msgBus.getName(), data));
                msgBus.publish(PubSubBus.Normalizer_DownStream_Out, data);
            }
        });
    }
}
