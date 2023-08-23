package io.jdbd.meta;

import io.jdbd.DriverVersion;
import io.jdbd.session.DatabaseMetaSpec;
import io.jdbd.session.Option;
import io.jdbd.session.OptionSpec;
import org.reactivestreams.Publisher;

import java.util.Map;

/**
 * <p>
 * This interface provider the methods for database meta data.
 * </p>
 *
 * @since 1.0
 */
public interface DatabaseMetaData extends DatabaseMetaSpec, OptionSpec {

    Option<String> TABLE_NAME_PATTERN = Option.from("TABLE NAME PATTERN", String.class);

    Option<String> COLUMN_NAME_PATTERN = Option.from("COLUMN NAME PATTERN", String.class);

    Option<String> INDEX_NAME_PATTERN = Option.from("INDEX NAME PATTERN", String.class);

    /**
     * <p>
     * Typical types are :
     *     <ul>
     *         <li>TABLE</li>
     *         <li>VIEW</li>
     *         <li>SYSTEM TABLE</li>
     *         <li>GLOBAL TEMPORARY</li>
     *         <li>LOCAL TEMPORARY</li>
     *         <li>ALIAS</li>
     *         <li>SYNONYM</li>
     *     </ul>
     * </p>
     */
    Option<String> TABLE_TYPE = Option.from("TABLE TYPE", String.class);

    /**
     * <p>
     * Typical types are :
     *     <ul>
     *         <li>BTREE</li>
     *         <li>HASH</li>
     *     </ul>
     * </p>
     */
    Option<String> INDEX_TYPE = Option.from("INDEX TYPE", String.class);

    /**
     * <p>
     * <ul>
     *     <li>this method return value probably equals {@link #productName()} , For example : MySQL</li>
     *     <li>this method return value probably not equals {@link #productName()} , For example : SQL Server</li>
     * </ul>
     * </p>
     *
     * @return database product family name. For example : MySQL , PostgreSQL , SQL Server .
     */
    String productFamily();

    /**
     * @return database product name,For example :  MySQL , PostgreSQL .
     */
    String productName();

    DriverVersion driverVersion();

    Publisher<DatabaseSchemaMetaData> currentSchema();

    Publisher<DatabaseSchemaMetaData> schemas();


    Publisher<DatabaseTableMetaData> tableOfCurrentSchema();

    Publisher<DatabaseTableMetaData> tableOfSchema(DatabaseSchemaMetaData schemaMeta);

    /**
     * <p>
     * The implementation of this method must support following :
     * <ul>
     *     <li>{@link #TABLE_NAME_PATTERN}</li>
     *     <li>{@link #TABLE_TYPE} : single value or {@link java.util.List}</li>
     * </ul>
     * </p>
     */
    Publisher<DatabaseTableMetaData> tableOfSchema(DatabaseSchemaMetaData schemaMeta, Map<Option<?>, ?> optionPairMap);

    /**
     * <p>
     * The implementation of this method must support following :
     * <ul>
     *     <li>{@link #COLUMN_NAME_PATTERN}</li>
     * </ul>
     * </p>
     */
    Publisher<TableColumnMetaData> columnOfTable(DatabaseTableMetaData tableMeta, Map<Option<?>, ?> optionPairMap);

    /**
     * <p>
     * The implementation of this method must support following :
     * <ul>
     *     <li>{@link #INDEX_NAME_PATTERN}</li>
     *     <li>{@link #INDEX_TYPE}</li>
     * </ul>
     * </p>
     */
    Publisher<TableIndexMetaData> indexOfTable(DatabaseTableMetaData tableMeta, Map<Option<?>, ?> optionPairMap);

    /**
     * <p>
     * The implementation of this method must support following :
     * <ul>
     *     <li>{@link #TABLE_NAME_PATTERN}</li>
     *     <li>{@link #TABLE_TYPE} : single value or {@link java.util.List}</li>
     * </ul>
     * </p>
     */
    Publisher<DatabaseTableMetaData> tables(Map<Option<?>, ?> optionPairMap);

    /**
     * <p>
     * The implementation of this method must support following :
     * <ul>
     *     <li>{@link #COLUMN_NAME_PATTERN}</li>
     * </ul>
     * </p>
     */
    Publisher<TableColumnMetaData> columns(Map<Option<?>, ?> optionPairMap);

    /**
     * <p>
     * The implementation of this method must support following :
     * <ul>
     *     <li>{@link #INDEX_NAME_PATTERN}</li>
     *     <li>{@link #INDEX_TYPE}  : single value or {@link java.util.List}</li>
     * </ul>
     * </p>
     */
    Publisher<TableIndexMetaData> indexes(Map<Option<?>, ?> optionPairMap);


    /**
     * <p>
     * This implementation of this method must support following :
     *     <ul>
     *         <li>{@link DatabaseMetaData#TABLE_TYPE} : database support table types</li>
     *         <li>{@link Option#USER} : representing current user name of session</li>
     *     </ul>
     * </p>
     */
    <R> Publisher<R> queryOption(Option<R> option);


    Publisher<String> sqlKeyWords();


    /**
     * <p>
     * The implementation of this method must support following :
     * <ul>
     *     <li>{@link Option#USER}</li>
     * </ul>
     * </p>
     */
    @Override
    <T> T valueOf(Option<T> option);


}
