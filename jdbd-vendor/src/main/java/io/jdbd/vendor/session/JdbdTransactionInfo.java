package io.jdbd.vendor.session;

import io.jdbd.JdbdException;
import io.jdbd.lang.NonNull;
import io.jdbd.session.Isolation;
import io.jdbd.session.Option;
import io.jdbd.session.TransactionInfo;
import io.jdbd.session.TransactionOption;
import io.jdbd.vendor.util.JdbdCollections;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public enum JdbdTransactionInfo implements TransactionInfo {

    READ_UNCOMMITTED_READ(Isolation.READ_UNCOMMITTED, true),
    READ_UNCOMMITTED_WRITE(Isolation.READ_UNCOMMITTED, false),

    READ_COMMITTED_READ(Isolation.READ_COMMITTED, true),
    READ_COMMITTED_WRITE(Isolation.READ_COMMITTED, false),

    REPEATABLE_READ_READ(Isolation.REPEATABLE_READ, true),
    REPEATABLE_READ_WRITE(Isolation.REPEATABLE_READ, false),

    SERIALIZABLE_READ(Isolation.SERIALIZABLE, true),
    SERIALIZABLE_WRITE(Isolation.SERIALIZABLE, false);

    private final Isolation isolation;

    private final boolean readOnly;

    JdbdTransactionInfo(Isolation isolation, boolean readOnly) {
        this.isolation = isolation;
        this.readOnly = readOnly;
    }

    @NonNull
    @Override
    public final Isolation isolation() {
        return this.isolation;
    }

    @Override
    public final boolean isReadOnly() {
        return this.readOnly;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T valueOf(final Option<T> key) {
        final Object value;
        if (key == Option.IN_TRANSACTION) {
            value = Boolean.TRUE;
        } else if (key == Option.ISOLATION) {
            value = this.isolation;
        } else if (key == Option.READ_ONLY) {
            value = this.readOnly;
        } else {
            value = null;
        }
        return (T) value;
    }

    @Override
    public <T> T nonNullOf(Option<T> option) {
        final T value;
        value = valueOf(option);
        Objects.requireNonNull(value);
        return value;
    }

    @Override
    public final boolean inTransaction() {
        //always true
        return true;
    }


    @Override
    public final String toString() {
        return String.format("%s[inTransaction:true,isolation:%s,readOnly:%s,hash:%s].", JdbdTransactionInfo.class.getName(),
                this.isolation.name(), this.readOnly, System.identityHashCode(this));
    }

    public static TransactionInfo txInfo(final Isolation isolation, final boolean readOnly,
                                         final boolean inTransaction) {
        Objects.requireNonNull(isolation);

        TransactionInfo status;
        if (!inTransaction) {
            status = (TransactionInfo) TransactionOption.option(isolation, readOnly);
            if (status instanceof Enum) {
                assert !status.inTransaction();
                return status;
            }
        }
        if (isolation == Isolation.READ_COMMITTED) {
            status = readOnly ? READ_COMMITTED_READ : READ_COMMITTED_WRITE;
        } else if (isolation == Isolation.REPEATABLE_READ) {
            status = readOnly ? REPEATABLE_READ_READ : REPEATABLE_READ_WRITE;
        } else if (isolation == Isolation.SERIALIZABLE) {
            status = readOnly ? SERIALIZABLE_READ : SERIALIZABLE_WRITE;
        } else if (isolation == Isolation.READ_UNCOMMITTED) {
            status = readOnly ? READ_UNCOMMITTED_READ : READ_UNCOMMITTED_WRITE;
        } else {
            throw new JdbdException(String.format("unexpected %s", isolation));
        }
        return status;
    }


    public static TransactionInfo fromMap(final Map<Option<?>, ?> optionMap) {
        final Isolation isolation;
        final boolean readOnly, inTransaction;

        isolation = (Isolation) optionMap.get(Option.ISOLATION);
        readOnly = (Boolean) optionMap.get(Option.READ_ONLY);
        inTransaction = (Boolean) optionMap.get(Option.IN_TRANSACTION);

        final TransactionInfo status;
        if (optionMap.size() == 3) {
            status = txInfo(isolation, readOnly, inTransaction);
        } else {
            Objects.requireNonNull(isolation);
            status = new MultiOptionTransactionInfo(optionMap);
        }

        return status;
    }


    private static final class MultiOptionTransactionInfo implements TransactionInfo {

        private final Map<Option<?>, ?> optionMap;

        private MultiOptionTransactionInfo(Map<Option<?>, ?> optionMap) {
            this.optionMap = Collections.unmodifiableMap(JdbdCollections.hashMap(optionMap));
        }

        @Override
        public boolean isReadOnly() {
            return (Boolean) this.optionMap.get(Option.READ_ONLY);
        }

        @NonNull
        @Override
        public Isolation isolation() {
            return (Isolation) optionMap.get(Option.ISOLATION);
        }

        @Override
        public boolean inTransaction() {
            return (Boolean) this.optionMap.get(Option.IN_TRANSACTION);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T valueOf(Option<T> option) {
            return (T) this.optionMap.get(option);
        }

        @Override
        public <T> T nonNullOf(Option<T> option) {
            final T value;
            value = valueOf(option);
            Objects.requireNonNull(value);
            return value;
        }


        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder(30);
            builder.append(getClass().getName())
                    .append("[ ");

            final Map<Option<?>, ?> map = this.optionMap;

            int index = 0;
            for (Map.Entry<Option<?>, ?> e : map.entrySet()) {
                if (index > 0) {
                    builder.append(" , ");
                }
                builder.append(' ')
                        .append(e.getKey().name())
                        .append(" = ")
                        .append(e.getValue());
                index++;
            }

            return builder.append(" , hash : ")
                    .append(System.identityHashCode(this))
                    .append(" ]")
                    .toString();
        }


    }//MultiOptionTransactionStatus


}
