package io.jdbd.session;

import io.jdbd.JdbdException;
import io.jdbd.lang.NonNull;
import io.jdbd.lang.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * This enum is a standard implementation of {@link TransactionStatus}
 * that {@link TransactionStatus#inTransaction()} always is false.
 * </p>
 *
 * @since 1.0
 */
final class JdbdTransactionOption implements TransactionStatus {

    private static final JdbdTransactionOption READ_UNCOMMITTED_READ = new JdbdTransactionOption(Isolation.READ_UNCOMMITTED, true);
    private static final JdbdTransactionOption READ_UNCOMMITTED_WRITE = new JdbdTransactionOption(Isolation.READ_UNCOMMITTED, false);

    private static final JdbdTransactionOption READ_COMMITTED_READ = new JdbdTransactionOption(Isolation.READ_COMMITTED, true);
    private static final JdbdTransactionOption READ_COMMITTED_WRITE = new JdbdTransactionOption(Isolation.READ_COMMITTED, false);

    private static final JdbdTransactionOption REPEATABLE_READ_READ = new JdbdTransactionOption(Isolation.REPEATABLE_READ, true);
    private static final JdbdTransactionOption REPEATABLE_READ_WRITE = new JdbdTransactionOption(Isolation.REPEATABLE_READ, false);

    private static final JdbdTransactionOption SERIALIZABLE_READ = new JdbdTransactionOption(Isolation.SERIALIZABLE, true);
    private static final JdbdTransactionOption SERIALIZABLE_WRITE = new JdbdTransactionOption(Isolation.SERIALIZABLE, false);


    static TransactionOption option(final @Nullable Isolation isolation, final boolean readOnly) {
        final TransactionOption option;
        option = fromInner(isolation, readOnly);
        if (option == null) {
            throw new JdbdException(String.format("unexpected %s", isolation));
        }
        return option;
    }

    @Nullable
    private static TransactionOption fromInner(final @Nullable Isolation isolation, final boolean readOnly) {
        final TransactionOption option;
        if (isolation == null) {
            option = readOnly ? DefaultTransactionOption.READ : DefaultTransactionOption.WRITE;
        } else if (isolation == Isolation.READ_COMMITTED) {
            option = readOnly ? READ_COMMITTED_READ : READ_COMMITTED_WRITE;
        } else if (isolation == Isolation.REPEATABLE_READ) {
            option = readOnly ? REPEATABLE_READ_READ : REPEATABLE_READ_WRITE;
        } else if (isolation == Isolation.SERIALIZABLE) {
            option = readOnly ? SERIALIZABLE_READ : SERIALIZABLE_WRITE;
        } else if (isolation == Isolation.READ_UNCOMMITTED) {
            option = readOnly ? READ_UNCOMMITTED_READ : READ_UNCOMMITTED_WRITE;
        } else {
            option = null;
        }
        return option;
    }

    static Builder builder() {
        return new OptionBuilder();
    }


    private final Isolation isolation;

    private final boolean readOnly;

    JdbdTransactionOption(Isolation isolation, boolean readOnly) {
        this.isolation = isolation;
        this.readOnly = readOnly;
    }

    @NonNull
    @Override
    public Isolation isolation() {
        return this.isolation;
    }

    @Override
    public boolean isReadOnly() {
        return this.readOnly;
    }

    @Override
    public boolean inTransaction() {
        // always false
        return false;
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T> T valueOf(final Option<T> key) {
        final Object value;
        if (key == Option.IN_TRANSACTION) {
            value = Boolean.FALSE;
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
    public String toString() {
        return String.format("%s[inTransaction:false,isolation:%s,readOnly:%s].",
                JdbdTransactionOption.class.getName(),
                this.isolation.name(),
                this.readOnly
        );
    }


    private enum DefaultTransactionOption implements TransactionOption {

        READ(true),
        WRITE(false);

        private final boolean readOnly;

        DefaultTransactionOption(boolean readOnly) {
            this.readOnly = readOnly;
        }


        @Override
        public Isolation isolation() {
            // always null
            return null;
        }

        @Override
        public boolean isReadOnly() {
            return this.readOnly;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T valueOf(final Option<T> key) {
            final Object value;
            if (key == Option.IN_TRANSACTION) {
                value = Boolean.FALSE;
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
        public String toString() {
            return String.format("%s[inTransaction:false,isolation:null,readOnly:%s].",
                    JdbdTransactionOption.class.getName(),
                    this.readOnly
            );
        }


    }//DefaultTransactionOption


    private static final class OptionBuilder implements Builder {

        private final Map<Option<?>, Object> optionMap = new HashMap<>();


        @Override
        public <T> Builder option(final Option<T> key, final @Nullable T value) {
            if (value == null) {
                this.optionMap.remove(key);
            } else {
                this.optionMap.put(key, value);
            }
            return this;
        }

        @Override
        public TransactionOption build() {

            final Map<Option<?>, Object> optionMap = this.optionMap;
            final Boolean readOnly;
            readOnly = (Boolean) optionMap.get(Option.READ_ONLY);
            if (readOnly == null) {
                String m = String.format("%s is required.", Option.READ_ONLY);
                throw new IllegalArgumentException(m);
            } else if (optionMap.containsKey(Option.IN_TRANSACTION)) {
                String m = String.format("%s is illegal.", Option.IN_TRANSACTION);
                throw new IllegalArgumentException(m);
            }
            final Isolation isolation;
            isolation = (Isolation) optionMap.get(Option.ISOLATION);

            TransactionOption option;
            if (isolation != null && optionMap.size() == 2) {
                option = JdbdTransactionOption.fromInner(isolation, readOnly);
                if (option == null) {
                    option = new DynamicTransactionOption(this);
                }
            } else {
                option = new DynamicTransactionOption(this);
            }
            return option;
        }


    }//OptionBuilder

    private static final class DynamicTransactionOption implements TransactionOption {

        private final Map<Option<?>, Object> optionMap;

        private DynamicTransactionOption(OptionBuilder builder) {
            this.optionMap = Collections.unmodifiableMap(builder.optionMap);
        }


        @Override
        public Isolation isolation() {
            return (Isolation) this.optionMap.get(Option.ISOLATION);
        }

        @Override
        public boolean isReadOnly() {
            return (Boolean) this.optionMap.get(Option.READ_ONLY);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T valueOf(Option<T> key) {
            return (T) this.optionMap.get(key);
        }

        @Override
        public <T> T nonNullOf(Option<T> option) {
            final T value;
            value = valueOf(option);
            Objects.requireNonNull(value);
            return value;
        }

        @Override
        public int hashCode() {
            return this.optionMap.hashCode();
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof DynamicTransactionOption) {
                match = ((DynamicTransactionOption) obj).optionMap.equals(this.optionMap);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append(DynamicTransactionOption.class.getName())
                    .append("[ ");
            int index = 0;
            for (Map.Entry<Option<?>, Object> e : this.optionMap.entrySet()) {
                if (index > 0) {
                    builder.append(" , ");
                }
                builder.append(e.getKey().name())
                        .append(" : ")
                        .append(e.getValue());
                index++;
            }
            return builder.append(" ]")
                    .toString();
        }


    }//DynamicTransactionOption


}
