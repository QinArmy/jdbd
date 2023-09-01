package io.jdbd.vendor.result;

import io.jdbd.result.CurrentRow;
import io.jdbd.result.CursorDirection;
import io.jdbd.result.RefCursor;
import io.jdbd.result.ResultStates;
import org.reactivestreams.Publisher;

import java.util.function.Function;

public abstract class VendorRefCursor implements RefCursor {

    protected final String name;

    protected VendorRefCursor(String name) {
        this.name = name;
    }

    @Override
    public final String name() {
        return this.name;
    }


    @Override
    public final <T> Publisher<T> fetch(CursorDirection direction, Function<CurrentRow, T> function) {
        return this.fetch(direction, function, ResultStates.IGNORE_STATES);
    }

    @Override
    public final <T> Publisher<T> fetch(CursorDirection direction, long count, Function<CurrentRow, T> function) {
        return this.fetch(direction, count, function, ResultStates.IGNORE_STATES);
    }


}
