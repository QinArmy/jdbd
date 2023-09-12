package io.jdbd.meta;

import io.jdbd.DriverVersion;
import io.jdbd.JdbdException;
import io.jdbd.lang.Nullable;
import io.jdbd.session.DatabaseMetaSpec;
import io.jdbd.session.Option;
import io.jdbd.session.OptionSpec;
import io.jdbd.session.SessionHolderSpec;
import org.reactivestreams.Publisher;

import java.util.Map;
import java.util.function.Function;

/**
 * <p>
 * This interface provider the methods for database meta data.
 * </p>
 *
 * @since 1.0
 */
public interface DatabaseMetaData extends DatabaseMetaSpec, SessionHolderSpec, OptionSpec {


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
     * A possible return value for the method
     * {@link  DatabaseMetaData#sqlStateType()} which is used to indicate
     * whether the value returned by the method
     * {@link JdbdException#getSqlState()} is an
     * X/Open (now know as Open Group) SQL CLI SQLSTATE value.
     */
    byte SQL_STATE_X_OPEN = 1;

    /**
     * A possible return value for the method
     * {@link  DatabaseMetaData#sqlStateType()} which is used to indicate
     * whether the value returned by the method
     * {@link JdbdException#getSqlState()} is an SQLSTATE value.
     */
    byte SQL_STATE_SQL = 2;


    /**
     * <p>
     * <ul>
     *     <li>this method return value probably equals {@link #productName()} , For example : MySQL</li>
     *     <li>this method return value probably not equals {@link #productName()} , For example : SQL Server</li>
     * </ul>
     * </p>
     *
     * @return database product family name. For example : MySQL , PostgreSQL , SQL Server .
     * @see #productName()
     */
    String productFamily();

    /**
     * @return database product name,For example :  MySQL , PostgreSQL ,MySQL-ENTERPRISE, PostgreSQL-ENTERPRISE
     * @see #productFamily()
     */
    String productName();

    DriverVersion driverVersion();

    Publisher<SchemaMeta> currentSchema();

    Publisher<SchemaMeta> schemas(Function<Option<?>, ?> optionFunc);


    Publisher<TableMeta> tablesOfCurrentSchema(Function<Option<?>, ?> optionFunc);

    /**
     * <p>
     * The implementation of this method must support following :
     * <ul>
     *     <li>{@link Option#NAME}</li>
     *     <li>{@link Option#TYPE_NAME},Typical types are :
     *     <ul>
     *         <li>TABLE</li>
     *         <li>VIEW</li>
     *         <li>SYSTEM TABLE</li>
     *         <li>SYSTEM VIEW</li>
     *         <li>GLOBAL TEMPORARY</li>
     *         <li>LOCAL TEMPORARY</li>
     *         <li>ALIAS</li>
     *         <li>SYNONYM</li>
     *     </ul>
     *     </li>
     * </ul>
     * </p>
     */
    Publisher<TableMeta> tablesOfSchema(SchemaMeta schemaMeta, Function<Option<?>, ?> optionFunc);

    /**
     * <p>
     * The implementation of this method must support following :
     * <ul>
     *     <li>{@link Option#NAME}</li>
     * </ul>
     * </p>
     */
    Publisher<TableColumnMeta> columnsOfTable(TableMeta tableMeta, Function<Option<?>, ?> optionFunc);

    /**
     * <p>
     * The implementation of this method must support following :
     * <ul>
     *     <li>{@link Option#NAME}</li>
     *     <li>{@link #INDEX_TYPE}</li>
     * </ul>
     * </p>
     */
    Publisher<TableIndexMeta> indexesOfTable(TableMeta tableMeta, Function<Option<?>, ?> optionFunc);


    /**
     * <p>
     * This implementation of this method must support following :
     *     <ul>
     *         <li>{@link Option#USER} : representing current user name of session</li>
     *     </ul>
     * </p>
     */
    <R> Publisher<R> queryOption(Option<R> option);


    Publisher<Map<String, Boolean>> sqlKeyWords(boolean onlyReserved);

    /**
     * @return the quoting string or a space if quoting is not supported
     */
    String identifierQuoteString();

    Publisher<FunctionMeta> sqlFunctions(@Nullable SchemaMeta metaData, Function<Option<?>, ?> optionFunc);

    Publisher<FunctionColumnMeta> sqlFunctionColumn(@Nullable SchemaMeta metaData, Function<Option<?>, ?> optionFunc);

    Publisher<FunctionColumnMeta> sqlFunctionColumnOf(FunctionMeta functionMeta, Function<Option<?>, ?> optionFunc);

    Publisher<ProcedureMeta> sqlProcedures(@Nullable SchemaMeta metaData, Function<Option<?>, ?> optionFunc);

    Publisher<ProcedureColumnMeta> sqlProcedureColumn(@Nullable SchemaMeta metaData, Function<Option<?>, ?> optionFunc);

    Publisher<ProcedureColumnMeta> sqlProcedureColumnOf(FunctionMeta functionMeta, Function<Option<?>, ?> optionFunc);


    /**
     * Indicates whether the SQLSTATE returned by {@link  JdbdException#getSqlState()}
     * is X/Open (now known as Open Group) SQL CLI or SQL:2003.
     *
     * @return the type of SQLSTATE; one of:
     * <ul>
     *     <li>{@link #SQL_STATE_X_OPEN}</li>
     *     <li>{@link #SQL_STATE_SQL}</li>
     * </ul>
     *        sqlStateXOpen or
     *        sqlStateSQL
     * @since 1.4
     */
    int sqlStateType() throws JdbdException;


    Publisher<DataTypeMeta> sqlDataTypes();


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
