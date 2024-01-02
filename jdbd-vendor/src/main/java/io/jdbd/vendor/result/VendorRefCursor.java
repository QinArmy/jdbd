/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.jdbd.vendor.result;

import io.jdbd.result.CurrentRow;
import io.jdbd.result.Cursor;
import io.jdbd.result.Direction;
import io.jdbd.result.ResultStates;
import org.reactivestreams.Publisher;

import java.util.function.Function;

public abstract class VendorRefCursor implements Cursor {

    protected final String name;

    protected VendorRefCursor(String name) {
        this.name = name;
    }

    @Override
    public final String name() {
        return this.name;
    }


    @Override
    public final <T> Publisher<T> fetch(Direction direction, Function<CurrentRow, T> function) {
        return this.fetch(direction, function, ResultStates.IGNORE_STATES);
    }

    @Override
    public final <T> Publisher<T> fetch(Direction direction, long count, Function<CurrentRow, T> function) {
        return this.fetch(direction, count, function, ResultStates.IGNORE_STATES);
    }


}
