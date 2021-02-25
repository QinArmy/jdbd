package io.jdbd;


import org.reactivestreams.Publisher;

import java.util.function.Consumer;

public interface MultiResults {

    Consumer<ResultStates> EMPTY_CONSUMER = resultStates -> {
    };

    /**
     * @return A Reactive Streams {@link Publisher} with rx operators that emits 0 to 1 elements
     * ,like {@code reactor.core.publisher.Mono}.
     * @throws NoMoreResultException  emit when {@link MultiResults} end and no buffer.
     * @throws TooManyResultException emit when database return result set count more than expect
     */
    Publisher<ResultStates> nextUpdate();

    /**
     * @return A Reactive Streams {@link Publisher} with rx operators that emits 0 to N elements
     * ,like {@code reactor.core.publisher.Flux}.
     */
    Publisher<ResultRow> nextQuery(Consumer<ResultStates> statesConsumer);

    /**
     * @see #nextQuery(Consumer)
     */
    Publisher<ResultRow> nextQuery();


}
