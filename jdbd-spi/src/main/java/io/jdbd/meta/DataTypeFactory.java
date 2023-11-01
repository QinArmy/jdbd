package io.jdbd.meta;

import io.jdbd.util.JdbdUtils;

import java.util.Locale;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * <p>
 * Package class
 * <br/>
 *
 * @since 1.0
 */
abstract class DataTypeFactory {

    private DataTypeFactory() {
        throw new UnsupportedOperationException();
    }

    static DataType typeFrom(String typeName, boolean caseSensitivity) {
        if (JdbdUtils.hasNoText(typeName)) {
            throw JdbdUtils.requiredText(typeName);
        }
        if (!caseSensitivity) {
            typeName = typeName.toUpperCase(Locale.ROOT);
        }
        return JdbdDataType.INSTANCE_MAP.computeIfAbsent(typeName, JdbdDataType.CONSTRUCTOR);
    }


    private static final class JdbdDataType implements DataType {

        private static final ConcurrentMap<String, JdbdDataType> INSTANCE_MAP = JdbdUtils.concurrentHashMap();

        private static final Function<String, JdbdDataType> CONSTRUCTOR = JdbdDataType::new;

        private final String dataTypeName;

        private JdbdDataType(String dataTypeName) {
            this.dataTypeName = dataTypeName;
        }


        @Override
        public String name() {
            return this.dataTypeName;
        }

        @Override
        public String typeName() {
            return this.dataTypeName;
        }

        @Override
        public boolean isArray() {
            return this.dataTypeName.endsWith("[]");
        }

        @Override
        public boolean isUnknown() {
            return false;
        }

        @Override
        public String toString() {
            return String.format("%s[typeName:%s,hash:%s]", getClass().getName(), this.dataTypeName,
                    System.identityHashCode(this));
        }


    }// DatabaseBuildInType


}
