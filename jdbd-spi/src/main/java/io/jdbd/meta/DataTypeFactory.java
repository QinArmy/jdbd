package io.jdbd.meta;

import io.jdbd.util.JdbdUtils;

import java.util.Locale;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * <p>
 * Package class
 * </p>
 *
 * @since 1.0
 */
abstract class DataTypeFactory {

    private DataTypeFactory() {
        throw new UnsupportedOperationException();
    }

    static DataType buildIn(String typeName, boolean caseSensitivity) {
        if (JdbdUtils.hasNoText(typeName)) {
            throw JdbdUtils.requiredText(typeName);
        }
        if (!caseSensitivity) {
            typeName = typeName.toUpperCase(Locale.ROOT);
        }
        return DatabaseBuildInType.INSTANCE_MAP.computeIfAbsent(typeName, DatabaseBuildInType.CONSTRUCTOR);
    }

    static DataType internalUse(String typeName, boolean caseSensitivity) {
        // currently, same
        return buildIn(typeName, caseSensitivity);
    }

    static DataType userDefined(String typeName, boolean caseSensitivity) {
        if (JdbdUtils.hasNoText(typeName)) {
            throw JdbdUtils.requiredText(typeName);
        }
        if (!caseSensitivity) {
            typeName = typeName.toUpperCase(Locale.ROOT);
        }
        return UserDefinedType.INSTANCE_MAP.computeIfAbsent(typeName, UserDefinedType.CONSTRUCTOR);
    }


    private static abstract class JdbdDataType implements DataType {

        private final String dataTypeName;

        private JdbdDataType(String dataTypeName) {
            this.dataTypeName = dataTypeName;
        }

        @Override
        public final String name() {
            return this.dataTypeName;
        }

        @Override
        public final String typeName() {
            return this.dataTypeName;
        }

        @Override
        public final boolean isArray() {
            return this.dataTypeName.endsWith("[]");
        }

        @Override
        public final boolean isUnknown() {
            return false;
        }


        @Override
        public final boolean isUserDefined() {
            return this instanceof UserDefinedType;
        }


    }// JdbdDataType


    private static final class DatabaseBuildInType extends JdbdDataType {

        private static final ConcurrentMap<String, DatabaseBuildInType> INSTANCE_MAP = JdbdUtils.concurrentHashMap();

        private static final Function<String, DatabaseBuildInType> CONSTRUCTOR = DatabaseBuildInType::new;

        private DatabaseBuildInType(String dataTypeName) {
            super(dataTypeName);
        }


    }// DatabaseBuildInType


    private static final class UserDefinedType extends JdbdDataType {

        private static final ConcurrentMap<String, UserDefinedType> INSTANCE_MAP = JdbdUtils.concurrentHashMap();

        private static final Function<String, UserDefinedType> CONSTRUCTOR = UserDefinedType::new;


        private UserDefinedType(String dataTypeName) {
            super(dataTypeName);
        }

    }//UserDefinedType


}
