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


import io.jdbd.result.ResultItem;

import java.util.function.LongConsumer;

/**
 * <p>
 * upstream must invoke {@link #complete()} or {@link #error(Throwable)} ,even if {@link #isCancelled()} return true.
 * <br/>
 *
 * @since 1.0
 */
public interface ResultSink {

    /**
     * <p>
     * This method should be invoked just once by driver developer.
     * </p>
     *
     * @param consumer request number consumer
     */
    void onRequest(LongConsumer consumer);

    boolean isCancelled();

    void next(ResultItem result);

    void error(Throwable e);


    void complete();

}
