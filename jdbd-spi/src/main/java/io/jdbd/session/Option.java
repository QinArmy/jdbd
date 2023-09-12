package io.jdbd.session;

import io.jdbd.Driver;
import io.jdbd.meta.SchemaMeta;
import io.jdbd.meta.TableMeta;
import io.jdbd.result.RefCursor;
import io.jdbd.util.JdbdUtils;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * <p>
 * This class is supported by following methods :
 * <ul>
 *     <li>{@link OptionSpec#valueOf(Option)}</li>
 *     <li>{@link io.jdbd.statement.Statement#setOption(Option, Object)}</li>
 *     <li>{@link io.jdbd.result.ResultRowMeta#getOf(int, Option)}</li>
 *     <li>{@link io.jdbd.result.ResultRowMeta#getNonNullOf(int, Option)}</li>
 * </ul>
 * for more dialectal driver.
 * </p>
 *
 * @see OptionSpec
 * @since 1.0
 */
public final class Option<T> {


    @SuppressWarnings("unchecked")
    public static <T> Option<T> from(final String name, final Class<T> javaType) {
        if (JdbdUtils.hasNoText(name)) {
            throw new IllegalArgumentException("no text");
        }
        Objects.requireNonNull(javaType);
        final Option<?> option;
        option = INSTANCE_MAP.computeIfAbsent(name, k -> new Option<>(name, javaType));

        if (option.javaType == javaType) {
            return (Option<T>) option;
        }
        return new Option<>(name, javaType);
    }

    public static final Function<Option<?>, ?> EMPTY_OPTION_FUNC = option -> null;

    private static final ConcurrentMap<String, Option<?>> INSTANCE_MAP = JdbdUtils.concurrentHashMap();

    /**
     * <p>
     * Representing a name option. For example : transaction name in firebird database.
     * </p>
     */
    public static final Option<String> NAME = Option.from("NAME", String.class);

    /**
     * <p>
     * Representing a wait option. For example : transaction wait option.
     * </p>
     *
     * @see <a href="https://firebirdsql.org/file/documentation/html/en/refdocs/fblangref40/firebird-40-language-reference.html#fblangref40-transacs-settransac">firebird : SET TRANSACTION</a>
     */
    public static final Option<Boolean> WAIT = Option.from("WAIT", Boolean.class);

    /**
     * <p>
     * Representing transaction LOCK TIMEOUT,for example firebird database.
     * </p>
     *
     * @see <a href="https://firebirdsql.org/file/documentation/html/en/refdocs/fblangref40/firebird-40-language-reference.html#fblangref40-transacs-settransac">firebird : SET TRANSACTION</a>
     */
    public static final Option<Duration> LOCK_TIMEOUT = Option.from("LOCK TIMEOUT", Duration.class);

    /**
     * <p>
     * This option representing transaction isolation level.
     * </p>
     * <p>
     * This option always is supported by {@link TransactionOption#valueOf(Option)}.
     * </p>
     *
     * @see #READ_ONLY
     */
    public static final Option<Isolation> ISOLATION = Option.from("ISOLATION", Isolation.class);

    /**
     * <p>
     * This option representing read-only transaction.
     * </p>
     * <p>
     * This option always is supported by {@link TransactionOption#valueOf(Option)}.
     * </p>
     * <p>
     * When this option is supported by {@link DatabaseSession#valueOf(Option)} , this option representing the session in read-only transaction block<br/>
     * after last statement executing , now the {@link #IN_TRANSACTION} always true.
     * </p>
     * <p>
     * When this option is supported by {@link io.jdbd.result.ResultStates#valueOf(Option)} , this option representing the session in read-only transaction block
     * after current statement executing, now the {@link #IN_TRANSACTION} always true. <br/>
     * <strong>NOTE</strong> : the 'current' statement perhaps is a part of multi-statement or is CALL command that execute procedures,<br/>
     * that means the read-only transaction maybe have ended by next statement.
     * </p>
     *
     * @see #IN_TRANSACTION
     */
    public static final Option<Boolean> READ_ONLY = Option.from("READ ONLY", Boolean.class);


    /**
     * <p>
     * This option representing {@link DatabaseSession} in transaction block.
     * </p>
     * <p>
     * This option always is supported by {@link TransactionStatus#valueOf(Option)}.
     * </p>
     * <p>
     * When this option is supported by {@link DatabaseSession#valueOf(Option)} , this option representing the session in transaction block<br/>
     * after last statement executing. Now this option is equivalent to {@link DatabaseSession#inTransaction()}.
     * </p>
     * <p>
     * When this option is supported by {@link io.jdbd.result.ResultStates#valueOf(Option)} , this option representing the session in transaction block
     * after current statement executing.<br/>
     * <strong>NOTE</strong> : the 'current' statement perhaps is a part of multi-statement or is CALL command that execute procedures<br/>
     * that means the transaction block maybe have ended by next statement.
     * </p>
     *
     * @see #READ_ONLY
     * @see DatabaseSession#inTransaction()
     */
    public static final Option<Boolean> IN_TRANSACTION = Option.from("IN TRANSACTION", Boolean.class);

    /**
     * <p>
     * When this option is supported by {@link DatabaseSession#valueOf(Option)} , this option representing the session is auto commit<br/>
     * after last statement executing.
     * </p>
     * <p>
     * When this option is supported by {@link io.jdbd.result.ResultStates#valueOf(Option)} , this option representing the session is auto commit
     * after current statement executing.<br/>
     * <strong>NOTE</strong> : the 'current' statement perhaps is a part of multi-statement or is CALL command that execute procedures<br/>
     * that means the auto commit status maybe have modified by next statement.
     * </p>
     *
     * @see #READ_ONLY
     * @see #IN_TRANSACTION
     */
    public static final Option<Boolean> AUTO_COMMIT = Option.from("AUTO COMMIT", Boolean.class);

    /**
     * <p>
     * This option representing {@link DatabaseSession} is read only, <strong>usually</strong> (not always) database is read only. <br/>
     * That means application developer can't modify the read only status by sql.
     * </p>
     * <p>
     * This option <strong>perhaps</strong> is supported by following :
     *     <ul>
     *         <li>{@link io.jdbd.result.ResultStates#valueOf(Option)}</li>
     *     </ul>
     * </p>
     */
    public static final Option<Boolean> READ_ONLY_SESSION = Option.from("READ ONLY SESSION", Boolean.class);


    /**
     * <p>
     * This option representing a auto close on error , for example : {@link io.jdbd.result.RefCursor}.
     * </p>
     *
     * @see io.jdbd.result.RefCursor
     */
    public static final Option<Boolean> AUTO_CLOSE_ON_ERROR = Option.from("AUTO CLOSE ON ERROR", Boolean.class);

    /**
     * <p>
     * [NO] CHAIN option of COMMIT command.
     * </p>
     *
     * @see LocalDatabaseSession#commit(java.util.function.Function)
     * @see LocalDatabaseSession#rollback(java.util.function.Function)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/commit.html">MySQL : COMMIT [WORK] [AND [NO] CHAIN]</a>
     * @see <a href="https://www.postgresql.org/docs/current/sql-commit.html">postgre : COMMIT [ WORK | TRANSACTION ] [ AND [ NO ] CHAIN ]</a>
     */
    public static final Option<Boolean> CHAIN = Option.from("CHAIN", Boolean.class);


    /**
     * <p>
     * [NO] RELEASE option of COMMIT/ROLLBACK command.
     * </p>
     *
     * @see LocalDatabaseSession#commit(java.util.function.Function)
     * @see LocalDatabaseSession#rollback(java.util.function.Function)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/commit.html">MySQL : ROLLBACK [WORK] [[NO] RELEASE]</a>
     */
    public static final Option<Boolean> RELEASE = Option.from("RELEASE", Boolean.class);

    /**
     * <p>
     * Transaction option of some database(eg: MySQL)
     * </p>
     *
     * @see LocalDatabaseSession#startTransaction(TransactionOption, HandleMode)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/commit.html">MySQL : WITH CONSISTENT SNAPSHOT</a>
     */
    public static final Option<Boolean> WITH_CONSISTENT_SNAPSHOT = Option.from("WITH CONSISTENT SNAPSHOT", Boolean.class);

    /**
     * <p>
     * Transaction option of some database(eg: PostgreSQL)
     * </p>
     *
     * @see LocalDatabaseSession#startTransaction(TransactionOption, HandleMode)
     * @see <a href="https://www.postgresql.org/docs/current/sql-start-transaction.html">postgre : DEFERRABLE</a>
     */
    public static final Option<Boolean> DEFERRABLE = Option.from("DEFERRABLE", Boolean.class);


    /**
     * <p>
     * representing the XID option of {@link TransactionStatus#valueOf(Option)} from {@link RmDatabaseSession}.
     * </p>
     *
     * @see Xid
     */
    public static final Option<Xid> XID = Option.from("XID", Xid.class);

    /**
     * <p>
     * representing the XA_STATES option of {@link TransactionStatus#valueOf(Option)} from {@link RmDatabaseSession}.
     * </p>
     *
     * @see XaStates
     */
    public static final Option<XaStates> XA_STATES = Option.from("XA STATES", XaStates.class);

    /**
     * <p>
     * representing the xa flags option of {@link TransactionStatus#valueOf(Option)} from {@link RmDatabaseSession}.
     * </p>
     */
    public static final Option<Integer> XA_FLAGS = Option.from("XA FLAGS", Integer.class);

    /**
     * @see io.jdbd.result.ServerException#valueOf(Option)
     * @see io.jdbd.result.Warning#valueOf(Option)
     */
    public static final Option<String> SQL_STATE = Option.from("SQL STATE", String.class);

    /**
     * @see io.jdbd.result.ServerException#valueOf(Option)
     */
    public static final Option<String> MESSAGE = Option.from("MESSAGE", String.class);

    /**
     * @see io.jdbd.result.ServerException#valueOf(Option)
     */
    public static final Option<Integer> VENDOR_CODE = Option.from("VENDOR CODE", Integer.class);


    public static final Option<Integer> WARNING_COUNT = Option.from("WARNING COUNT", Integer.class);

    /**
     * @see Driver#USER
     */
    public static final Option<String> USER = Option.from(Driver.USER, String.class);


    public static final Option<ZoneOffset> CLIENT_ZONE = Option.from("CLIENT ZONE", ZoneOffset.class);

    public static final Option<ZoneOffset> SERVER_ZONE = Option.from("SERVER ZONE", ZoneOffset.class);

    public static final Option<Charset> CLIENT_CHARSET = Option.from("CLIENT CHARSET", Charset.class);

    /**
     * true : text value support backslash escapes
     */
    public static final Option<Boolean> BACKSLASH_ESCAPES = Option.from("BACKSLASH ESCAPES", Boolean.class);

    /**
     * true : binary value support hex escapes
     */
    public static final Option<Boolean> BINARY_HEX_ESCAPES = Option.from("BINARY HEX ESCAPES", Boolean.class);


    /**
     * <p>
     * When this option is supported by {@link io.jdbd.result.ResultStates},this option representing the statement that produce
     * the {@link io.jdbd.result.ResultStates} declare a cursor. For example : execute postgre DECLARE command.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/sql-declare.html">PostgreSQL DECLARE</a>
     */
    public static final Option<RefCursor> CURSOR = Option.from("CURSOR", RefCursor.class);

    public static final Option<Integer> PREPARE_THRESHOLD = Option.from(Driver.PREPARE_THRESHOLD, Integer.class);

    /**
     * @see io.jdbd.result.ResultRowMeta#getOf(int, Option)
     */
    public static final Option<Long> PRECISION = Option.from("PRECISION", Long.class);

    /**
     * The type name of something.
     *
     * @see io.jdbd.meta.DatabaseMetaData#tablesOfSchema(SchemaMeta, Function)
     * @see io.jdbd.meta.TableMeta#valueOf(Option)
     */
    public static final Option<String> TYPE_NAME = Option.from("TYPE NAME", String.class);

    /**
     * this option representing charset option.
     *
     * @see io.jdbd.meta.TableMeta#valueOf(Option)
     * @see io.jdbd.meta.TableColumnMeta#valueOf(Option)
     */
    public static final Option<Charset> CHARSET = Option.from("CHARSET", Charset.class);

    /**
     * this option representing collation option.
     *
     * @see io.jdbd.meta.TableMeta#valueOf(Option)
     * @see io.jdbd.meta.TableColumnMeta#valueOf(Option)
     */
    public static final Option<String> COLLATION = Option.from("COLLATION", String.class);

    /**
     * this option representing privilege option.
     *
     * @see io.jdbd.meta.TableMeta#valueOf(Option)
     * @see io.jdbd.meta.TableColumnMeta#valueOf(Option)
     */
    public static final Option<String> PRIVILEGE = Option.from("PRIVILEGE", String.class);

    /**
     * this option representing unique option ,for example index.
     *
     * @see io.jdbd.meta.DatabaseMetaData#indexesOfTable(TableMeta, Function)
     */
    public static final Option<Boolean> UNIQUE = from("UNIQUE", Boolean.class);

    private final String name;

    private final Class<T> javaType;

    /**
     * private constructor
     */
    private Option(String name, Class<T> javaType) {
        this.name = name;
        this.javaType = javaType;
    }


    public String name() {
        return this.name;
    }

    public Class<T> javaType() {
        return this.javaType;
    }


    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.javaType);
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean match;
        if (obj == this) {
            match = true;
        } else if (obj instanceof Option) {
            final Option<?> o = (Option<?>) obj;
            match = o.name.equals(this.name) && o.javaType == this.javaType;
        } else {
            match = false;
        }
        return match;
    }

    @Override
    public String toString() {
        return String.format("%s[ name : %s , javaType : %s , hash : %s]",
                Option.class.getName(),
                this.name,
                this.javaType.getName(),
                System.identityHashCode(this)
        );
    }



    /*-------------------below private method -------------------*/

    private void readObject(ObjectInputStream in) throws IOException {
        throw new InvalidObjectException("can't deserialize Option");
    }

    private void readObjectNoData() throws ObjectStreamException {
        throw new InvalidObjectException("can't deserialize Option");
    }


}
