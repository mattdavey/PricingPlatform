package pricingplatform.components;

import rx.subjects.PublishSubject;

public class DataNormalization {
    private final PublishSubject<String> incoming = PublishSubject.create();

    public void send(final String message) {
        incoming.onNext(message);
    }
}
