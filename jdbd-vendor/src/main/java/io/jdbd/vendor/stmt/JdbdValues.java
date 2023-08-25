package io.jdbd.vendor.stmt;

import io.jdbd.lang.Nullable;
import io.jdbd.meta.DataType;

import java.util.Objects;

public abstract class JdbdValues {

    protected JdbdValues() {
        throw new UnsupportedOperationException();
    }


    public static NamedValue namedValue(String name, DataType dataType, @Nullable Object value) {
        return new JdbdNamedValue(name, dataType, value);
    }

    public static ParamValue paramValue(int indexBasedZero, DataType dataType, @Nullable Object value) {
        return new JdbdParamValue(indexBasedZero, dataType, value);
    }


    private static abstract class JdbdValue implements Value {

        final DataType type;

        final Object value;

        private JdbdValue(DataType type, @Nullable Object value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public final Object get() {
            return this.value;
        }

        @Override
        public final Object getNonNull() throws NullPointerException {
            final Object value = this.value;
            if (value == null) {
                throw new NullPointerException("value is null");
            }
            return value;
        }

        @Override
        public final DataType getType() {
            return this.type;
        }


    }//JdbdValue


    private static final class JdbdParamValue extends JdbdValue implements ParamValue {

        private final int index;

        private JdbdParamValue(int index, DataType type, @Nullable Object value) {
            super(type, value);
            this.index = index;
        }

        @Override
        public int getIndex() {
            return this.index;
        }


        @Override
        public int hashCode() {
            return Objects.hash(this.index, this.type, this.value);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof JdbdParamValue) {
                final JdbdParamValue o = (JdbdParamValue) obj;
                match = o.index == this.index
                        && o.type == this.type   // must same instance
                        && Objects.equals(o.value, this.value);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append(JdbdParamValue.class.getName())
                    .append("[ index : ")
                    .append(this.index)
                    .append(" , type : ")
                    .append(this.type);

            if (!(this.value instanceof String)) {
                builder.append(" , value : ")
                        .append(this.value);
            }// don't print string value for information safe.
            return builder.append(" ]")
                    .toString();
        }

    }//JdbdParamValue


    private static final class JdbdNamedValue extends JdbdValue implements NamedValue {

        private final String name;

        private JdbdNamedValue(String name, DataType type, @Nullable Object value) {
            super(type, value);
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }


        @Override
        public int hashCode() {
            return Objects.hash(this.name, this.type, this.value);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof JdbdNamedValue) {
                final JdbdNamedValue o = (JdbdNamedValue) obj;
                match = o.name.equals(this.name)
                        && o.type == this.type   // must same instance
                        && Objects.equals(o.value, this.value);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append(JdbdNamedValue.class.getName())
                    .append("[ name : ")
                    .append(this.name)
                    .append(" , type : ")
                    .append(this.type);

            if (!(this.value instanceof String)) {
                builder.append(" , value : ")
                        .append(this.value);
            }// don't print string value for information safe.
            return builder.append(" ]")
                    .toString();
        }


    }//JdbdNamedValue


}
