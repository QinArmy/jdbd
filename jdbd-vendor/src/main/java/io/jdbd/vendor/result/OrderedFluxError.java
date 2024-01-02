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

import io.jdbd.result.OrderedFlux;
import io.jdbd.result.ResultItem;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.Operators;

final class OrderedFluxError implements OrderedFlux {

    private final Throwable error;

    OrderedFluxError(Throwable error) {
        this.error = error;
    }

    @Override
    public void subscribe(Subscriber<? super ResultItem> s) {
        Operators.error(s, this.error);
    }

}
