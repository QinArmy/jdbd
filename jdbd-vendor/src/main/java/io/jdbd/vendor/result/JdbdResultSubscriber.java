package io.jdbd.vendor.result;

import io.jdbd.result.ResultItem;
import io.jdbd.result.ResultStates;
import io.jdbd.result.ResultStatusConsumerException;
import io.jdbd.vendor.ResultType;
import io.jdbd.vendor.SubscribeException;
import io.jdbd.vendor.util.JdbdCollections;
import org.reactivestreams.Subscription;
import reactor.core.publisher.FluxSink;

import java.util.List;
import java.util.function.Consumer;

@Deprecated
abstract class JdbdResultSubscriber implements ResultSubscriber {

    List<Throwable> errorList;

    Subscription subscription;

    JdbdResultSubscriber() {
    }


    abstract ResultType getSubscribeType();

    /**
     * @return true : stateConsumer throw error
     */
    final boolean fluxSinkComplete(FluxSink<?> sink, Consumer<ResultStates> stateConsumer, ResultStates state) {
        Throwable consumerError = null;
        try {
            stateConsumer.accept(state);
        } catch (Throwable e) {
            consumerError = e;
        }
        if (consumerError == null) {
            sink.complete();
        } else {
            final ResultStatusConsumerException re;
            re = ResultStatusConsumerException.create(stateConsumer, consumerError);
            addError(re);
            sink.error(re);
        }
        return consumerError != null;
    }

    final void addSubscribeError(ResultType resultType) {
        List<Throwable> errorList = this.errorList;
        if (errorList == null || errorList.isEmpty()) {
            addError(new SubscribeException(getSubscribeType(), resultType));
            return;
        }
        boolean add = true;
        switch (resultType) {
            case UPDATE:
            case BATCH_UPDATE:
            case QUERY: {
                for (Throwable e : errorList) {
                    if (e.getClass() == SubscribeException.class) {
                        add = false;
                        break;
                    }
                }
            }
            break;
            case MULTI_RESULT: {
                final int size = errorList.size();
                for (int i = 0; i < size; i++) {
                    Throwable e = errorList.get(i);
                    if (e.getClass() != SubscribeException.class) {
                        continue;
                    }
                    SubscribeException se = (SubscribeException) e;
                    if (se.getActualType() != ResultType.MULTI_RESULT) {
                        errorList.set(i, new SubscribeException(getSubscribeType(), ResultType.MULTI_RESULT));
                    }
                    add = false;
                    break;
                }
            }
            break;
            default:
                throw new IllegalArgumentException("resultType error.");
        }

        if (add) {
            addError(new SubscribeException(getSubscribeType(), resultType));
        }
    }


    final void addError(Throwable error) {
        List<Throwable> errorList = this.errorList;
        if (errorList == null) {
            this.errorList = errorList = JdbdCollections.arrayList();
            final Subscription s = this.subscription;
            if (s != null) {
                s.cancel();
            }
        }
        errorList.add(error);
    }

    final boolean hasError() {
        List<Throwable> errorList = this.errorList;
        return !(errorList == null || errorList.isEmpty());
    }


    static IllegalArgumentException createUnknownTypeError(ResultItem result) {
        return new IllegalArgumentException(String.format("Unknown type[%s]", result.getClass().getName()));
    }

    static IllegalArgumentException createDuplicationResultState(ResultStates state) {
        return new IllegalArgumentException(String.format("ResultState [%s] duplication", state.getClass().getName()));
    }

}
