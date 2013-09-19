package pricingplatform.components;

import pricingplatform.components.DataNormalization;
import rx.Observable;
import rx.subjects.PublishSubject;

public class FIXEngine {
    private final PublishSubject<String> outgoing = PublishSubject.create();
    private final DataNormalization dataNormalization;

    public FIXEngine(final DataNormalization dataNormalization) {
        this.dataNormalization = dataNormalization;
    }

    public void send(final String message) {
        dataNormalization.send(message);
    }

    public Observable<String> replies() {
        return outgoing;
    }
}
