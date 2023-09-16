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

    static DataType buildIn(String typeName, boolean caseSensitivity) {
        if (JdbdUtils.hasNoText(typeName)) {
            throw JdbdUtils.requiredText(typeName);
        }
        if (!caseSensitivity) {
            typeName = typeName.toUpperCase(Locale.ROOT);
        }
        return DatabaseBuildInType.INSTANCE_MAP.computeIfAbsent(typeName, DatabaseBuildInType.CONSTRUCTOR);
    }


    static UserDefinedType userDefined(String typeName, boolean caseSensitivity) {
        if (JdbdUtils.hasNoText(typeName)) {
            throw JdbdUtils.requiredText(typeName);
        }
        if (!caseSensitivity) {
            typeName = typeName.toUpperCase(Locale.ROOT);
        }
        return JdbdUserDefinedType.INSTANCE_MAP.computeIfAbsent(typeName, JdbdUserDefinedType.CONSTRUCTOR);
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


    }// JdbdDataType


    private static final class DatabaseBuildInType extends JdbdDataType {

        private static final ConcurrentMap<String, DatabaseBuildInType> INSTANCE_MAP = JdbdUtils.concurrentHashMap();

        private static final Function<String, DatabaseBuildInType> CONSTRUCTOR = DatabaseBuildInType::new;

        private DatabaseBuildInType(String dataTypeName) {
            super(dataTypeName);
        }


    }// DatabaseBuildInType


    private static final class JdbdUserDefinedType extends JdbdDataType implements UserDefinedType {

        private static final ConcurrentMap<String, JdbdUserDefinedType> INSTANCE_MAP = JdbdUtils.concurrentHashMap();

        private static final Function<String, JdbdUserDefinedType> CONSTRUCTOR = JdbdUserDefinedType::new;


        private JdbdUserDefinedType(String dataTypeName) {
            super(dataTypeName);
        }


    }//JdbdUserDefinedType


}
