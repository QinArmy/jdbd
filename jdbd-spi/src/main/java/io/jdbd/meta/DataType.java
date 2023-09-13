package io.jdbd.meta;

import io.jdbd.statement.ParametrizedStatement;

/**
 * <p>
 * This interface representing sql data type,this interface is used by following:
 *     <ul>
 *         <li>{@link ParametrizedStatement#bind(int, DataType, Object)}</li>
 *         <li>{@link io.jdbd.result.ResultRowMeta#getDataType(int)}</li>
 *     </ul>
 *     {@link ParametrizedStatement#bind(int, DataType, Object)} use {@link #typeName()} bind parameter, if not {@link JdbdType}.
 * </p>
 * <p>
 *     The Known superinterfaces:
 *     <ul>
 *         <li>{@link SQLType} representing database build-in type</li>
 *         <li>{@link UserDefinedType} representing user-defined type</li>
 *     </ul>
 * </p>
 * <p>
 *     The Known implementations:
 *     <ul>
 *         <li>{@link JdbdType} generic sql type</li>
 *     </ul>
 * </p>
 *
 * @see SQLType
 * @see JdbdType
 * @since 1.0
 */
public interface DataType {

    /**
     * @return alias of data type in java language.
     */
    String name();

    /**
     * @return data type name in database. If support ,upper case precedence. If array end with [] .
     */
    String typeName();

    /**
     * <p>
     * <strong>NOTE</strong> : if {@link #isUnknown()} return true ,this method always return false.
     * </p>
     */
    boolean isArray();

    boolean isUnknown();


    /**
     * <p>
     * This method is equivalent to {@code DataType.buildIn(typeName,false)} :
     * </p>
     * <p>
     * <strong>NOTE</strong>: only when {@link JdbdType} couldn't express appropriate type,you use this method.<br/>
     * It means you should prefer {@link JdbdType}.
     * </p>
     *
     * @see #buildIn(String, boolean)
     */
    static DataType buildIn(String typeName) {
        return DataTypeFactory.buildIn(typeName, false);
    }

    /**
     * <p>
     * Get database build-in dialect {@link DataType} instance by typeName.
     * </p>
     * <p>
     * <strong>NOTE</strong>: only when {@link JdbdType} couldn't express appropriate type,you use this method.<br/>
     * It means you should prefer {@link JdbdType}.
     * </p>
     *
     * @param typeName        database build-in dialect type name,if typeName endWith '[]',then {@link DataType#isArray()} always return true.
     * @param caseSensitivity if false ,then {@link DataType#typeName()} always return upper case.
     * @return {@link DataType} that representing database build-in dialect type.
     * @throws IllegalArgumentException throw when typeName have no text.
     */
    static DataType buildIn(String typeName, boolean caseSensitivity) {
        return DataTypeFactory.buildIn(typeName, caseSensitivity);
    }


    /**
     * <p>
     * This method is equivalent to {@code DataType.userDefined(typeName,false)} :
     * </p>
     * <p>
     * <strong>NOTE</strong>: only when {@link JdbdType} couldn't express appropriate type,you use this method.<br/>
     * It means you should prefer {@link JdbdType}.
     * </p>
     *
     * @see #userDefined(String, boolean)
     */
    static UserDefinedType userDefined(String typeName) {
        return DataTypeFactory.userDefined(typeName, false);
    }

    /**
     * <p>
     * Get user-defined {@link DataType} instance by typeName.
     * </p>
     * * <p>
     * * <strong>NOTE</strong>: only when {@link JdbdType} couldn't express appropriate type,you use this method.<br/>
     * * It means you should prefer {@link JdbdType}.
     * * </p>
     *
     * @param typeName        database build-in dialect type name,if typeName endWith '[]',then {@link DataType#isArray()} always return true.
     * @param caseSensitivity if false ,then {@link DataType#typeName()} always return upper case.
     * @return {@link DataType} that representing user-defined type.
     * @throws IllegalArgumentException throw when typeName have no text.
     */
    static UserDefinedType userDefined(String typeName, boolean caseSensitivity) {
        return DataTypeFactory.userDefined(typeName, caseSensitivity);
    }


}
