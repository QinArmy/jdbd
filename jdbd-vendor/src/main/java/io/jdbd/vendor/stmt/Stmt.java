package io.jdbd.vendor.stmt;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import reactor.util.annotation.Nullable;

import java.util.function.Function;

public interface Stmt {

    int getTimeout();

    @Nullable
    default Function<Object, Publisher<byte[]>> getImportPublisher() {
        return null;
    }

    @Nullable
    default Function<Object, Subscriber<byte[]>> getExportSubscriber() {
        return null;
    }
}
