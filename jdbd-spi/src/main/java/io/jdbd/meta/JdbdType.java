package io.jdbd.meta;

import io.jdbd.session.DatabaseSession;

/**
 * <p>
 * This enum is a implementation of {@link DataType} for the convenience that application bind parameter.
 * <br/>
 *
 * @see io.jdbd.statement.ParametrizedStatement#bind(int, DataType, Object)
 * @see io.jdbd.statement.Statement#bindStmtVar(String, DataType, Object)
 * @see io.jdbd.result.ResultRowMeta#getJdbdType(int)
 */
public enum JdbdType implements DataType {

    /**
     * Identifies the generic SQL value {@code NULL} . This enum instance representing sql type is unknown and value is null.
     * <p>
     *     <ul>
     *         <li>{@link io.jdbd.result.ResultRowMeta#getJdbdType(int)} return this enm instance if sql type is unknown and value is null. For example : {@code SELECT NULL as result}</li>
     *         <li>{@link io.jdbd.statement.ParametrizedStatement#bind(int, DataType, Object)} support this enum instance,if application developer don't known type and value is null.</li>
     *         <li>{@link io.jdbd.statement.Statement#bindStmtVar(String, DataType, Object)} support this enum instance,if application developer don't known type and value is null.</li>
     *     </ul>
     *     Actually in most case , application developer known the type of null ,so dont' need this enum instance. For example:
     *     <pre><br/>
     *         // stmt is io.jdbd.statement.ParametrizedStatement instance
     *         stmt.bind(0,JdbdType.BIGINT,null)
     *     </pre>
     *<br/>
     */
    NULL,

    /**
     * Identifies the generic SQL type {@code BOOLEAN}.
     */
    BOOLEAN,

    /**
     * Identifies the generic SQL type {@code BIT}, not boolean.
     * {@code BIT} are strings of 1's and 0's. They can be used to store or visualize bit masks.
     * {@code BIT} is similar to {@link #VARBIT}, except that must match fixed length.
     * <p>
     * {@link io.jdbd.result.DataRow#get(int, Class)} support following java type:
     * <ul>
     *     <li>{@link Integer}</li>
     *     <li>{@link Long}</li>
     *     <li>{@link String}</li>
     *     <li>{@link java.util.BitSet}</li>
     * </ul>
     *<br/>
     */
    BIT,

    /**
     * Identifies the generic SQL type {@code VARBIT}, not boolean.
     * {@code VARBIT} are strings of 1's and 0's. They can be used to store or visualize bit masks.
     * <p>
     * {@link io.jdbd.result.DataRow#get(int, Class)} support following java type:
     * <ul>
     *     <li>{@link Integer}</li>
     *     <li>{@link Long}</li>
     *     <li>{@link String}</li>
     *     <li>{@link java.util.BitSet}</li>
     * </ul>
     *<br/>
     */
    VARBIT,

    /**
     * Identifies the generic SQL type {@code TINYINT}, one byte integer number.
     */
    TINYINT,

    /**
     * Identifies the generic SQL type {@code SMALLINT}, two byte integer number.
     */
    SMALLINT,

    /**
     * Identifies the generic SQL type {@code MEDIUMINT}, three byte integer number.
     */
    MEDIUMINT,
    /**
     * Identifies the generic SQL type {@code INTEGER}, four byte integer number.
     */
    INTEGER,
    /**
     * Identifies the generic SQL type {@code BIGINT}, eight byte integer number.
     */
    BIGINT,

    /**
     * Identifies the generic SQL type {@code TINYINT_UNSIGNED}, one byte integer number.
     */
    TINYINT_UNSIGNED,

    /**
     * Identifies the generic SQL type {@code SMALLINT_UNSIGNED}, two byte integer number.
     */
    SMALLINT_UNSIGNED,

    /**
     * Identifies the generic SQL type {@code MEDIUMINT_UNSIGNED}, three byte integer number.
     */
    MEDIUMINT_UNSIGNED,
    /**
     * Identifies the generic SQL type {@code INTEGER_UNSIGNED}, four byte integer number.
     */
    INTEGER_UNSIGNED,
    /**
     * Identifies the generic SQL type {@code BIGINT_UNSIGNED}, eight byte integer number.
     */
    BIGINT_UNSIGNED,

    /**
     * Identifies the generic SQL type {@code FLOAT}.
     */
    FLOAT,
    /**
     * Identifies the generic SQL type {@code REAL}.
     */
    REAL,
    /**
     * Identifies the generic SQL type {@code DOUBLE}.
     */
    DOUBLE,
    /**
     * Identifies the generic SQL type {@code NUMERIC}.
     */
    NUMERIC,
    /**
     * Identifies the generic SQL type {@code DECIMAL}.
     */
    DECIMAL,

    /**
     * Identifies the generic SQL type {@code DECIMAL}.
     */
    DECIMAL_UNSIGNED,

    /**
     * Identifies the generic SQL type {@code CHAR}.
     */
    CHAR,
    /**
     * Identifies the generic SQL type {@code VARCHAR}.
     */
    VARCHAR,

    /**
     * Identifies the generic SQL type {@code ENUM}.
     */
    ENUM,

    /**
     * min precision text type
     */
    TINYTEXT,

    /**
     * precision greater than {@link #TINYTEXT} text type
     */
    TEXT,

    /**
     * precision greater than {@link #TEXT} text type
     */
    MEDIUMTEXT,

    /**
     * max precision text type
     */
    LONGTEXT,

    /**
     * Identifies the generic SQL type {@code BINARY}.
     */
    BINARY,
    /**
     * Identifies the generic SQL type {@code VARBINARY}.
     */
    VARBINARY,

    /**
     * min precision blob type
     */
    TINYBLOB,

    /**
     * precision greater than {@link #TINYBLOB} blob type
     */
    BLOB,

    /**
     * precision greater than {@link #BLOB} blob type
     */
    MEDIUMBLOB,
    /**
     * max precision blob type
     */
    LONGBLOB,

    /**
     * Identifies the generic SQL type {@code TIME}.
     */
    TIME,

    YEAR,

    YEAR_MONTH,

    MONTH_DAY,

    /**
     * Identifies the generic SQL type {@code DATE}.
     */
    DATE,

    /**
     * Identifies the generic SQL type {@code TIMESTAMP}.
     */
    TIMESTAMP,

    /**
     * Identifies the generic SQL type {@code TIME_WITH_TIMEZONE}.
     */
    TIME_WITH_TIMEZONE,

    /**
     * Identifies the generic SQL type {@code TIMESTAMP_WITH_TIMEZONE}.
     */
    TIMESTAMP_WITH_TIMEZONE,

    /**
     * A time-based amount of time, such as '34.5 seconds'.
     */
    DURATION,

    /**
     * A date-based amount of time.
     */
    PERIOD,

    /**
     * A date-time-based amount of time.
     */
    INTERVAL,


    /**
     * Identifies the SQL type {@code ROWID}.
     */
    ROWID,

    /**
     * Identifies the generic SQL type {@code XML}.
     */
    XML,

    JSON,

    JSONB,

    /**
     * Identifies the generic SQL type {@code GEOMETRY}, for example Point , LineString,polygon
     *
     * @see <a href="https://www.ogc.org/standards/sfa">Simple Feature Access - Part 1: Common Architecture PDF</a>
     */
    GEOMETRY,


    /*-------------------below isn't supported by io.jdbd.statement.ParametrizedStatement.bind() and io.jdbd.statement.Statement.bindStmtVar()-------------------*/

    UNKNOWN,


    /**
     * Identifies the generic SQL type {@code REF_CURSOR}.
     * <p>
     * If {@link io.jdbd.result.ResultRowMeta#getJdbdType(int)} is this enum instance,then {@link io.jdbd.result.DataRow#get(int)} always is {@link String} instance.
     *<br/>
     * <p>
     * Application developer can get the instance of {@link io.jdbd.result.RefCursor} by {@link DatabaseSession#refCursor(String)}
     *<br/>
     *
     * @see io.jdbd.result.RefCursor
     */
    REF_CURSOR,


    /**
     * <p>
     * Identifies the generic SQL type {@code ARRAY}.
     *     <ul>
     *         <li>{@link io.jdbd.statement.ParametrizedStatement#bind(int, DataType, Object)} don't support this enum instance.</li>
     *         <li>{@link io.jdbd.statement.Statement#bindStmtVar(String, DataType, Object)} don't support this enum instance.</li>
     *     </ul>
     *  This enum instance is only returned by {@link io.jdbd.result.ResultRowMeta#getJdbdType(int)}
     *<br/>
     */
    ARRAY,

    /**
     * <p>
     * Identifies the generic SQL type {@code COMPOSITE}.
     *     <ul>
     *         <li>{@link io.jdbd.statement.ParametrizedStatement#bind(int, DataType, Object)} don't support this enum instance.</li>
     *         <li>{@link io.jdbd.statement.Statement#bindStmtVar(String, DataType, Object)} don't support this enum instance.</li>
     *     </ul>
     *  This enum instance is only returned by {@link io.jdbd.result.ResultRowMeta#getJdbdType(int)}
     *<br/>
     */
    COMPOSITE,


    /**
     * Indicates that the dialect data type  , this enum instance is only returned by {@link io.jdbd.result.ResultRowMeta#getJdbdType(int)}.
     * This enum instance representing the dialect data type than couldn't be expressed other instance of {@link JdbdType}.
     * <p>
     *     <ul>
     *         <li>{@link io.jdbd.statement.ParametrizedStatement#bind(int, DataType, Object)} don't support this enum instance.</li>
     *         <li>{@link io.jdbd.statement.Statement#bindStmtVar(String, DataType, Object)} don't support this enum instance.</li>
     *     </ul>
     *     If application developer want to bind dialect type ,then application developer should define the new {@link DataType} type
     *     that it's {@link #typeName()} is supported by database.
     *<br/>
     */
    DIALECT_TYPE;


    @Override
    public final String typeName() {
        return this.name();
    }


    @Override
    public final boolean isArray() {
        return this == ARRAY;
    }

    @Override
    public final boolean isUnknown() {
        return this == UNKNOWN;
    }

    /**
     * @return true : unsigned number
     */
    public final boolean isUnsigned() {
        final boolean match;
        switch (this) {
            case TINYINT_UNSIGNED:
            case SMALLINT_UNSIGNED:
            case MEDIUMINT_UNSIGNED:
            case INTEGER_UNSIGNED:
            case BIGINT_UNSIGNED:
            case DECIMAL_UNSIGNED:
                match = true;
                break;
            default:
                match = false;
        }
        return match;
    }

    /**
     * @return true :  signed number
     */
    public final boolean isSigned() {
        final boolean match;
        switch (this) {
            case TINYINT:
            case SMALLINT:
            case MEDIUMINT:
            case INTEGER:
            case BIGINT:
            case DECIMAL:
            case NUMERIC:
            case FLOAT:
            case REAL:
            case DOUBLE:
                match = true;
                break;
            default:
                match = false;
        }
        return match;
    }


//            case NULL:
//            case BOOLEAN:
//
//            case TINYINT:
//            case SMALLINT:
//            case MEDIUMINT:
//            case INTEGER:
//            case BIGINT:
//
//            case DECIMAL:
//            case NUMERIC:
//            case FLOAT:
//            case REAL:
//            case DOUBLE:
//
//            case TINYINT_UNSIGNED:
//            case SMALLINT_UNSIGNED:
//            case MEDIUMINT_UNSIGNED:
//            case INTEGER_UNSIGNED:
//            case BIGINT_UNSIGNED:
//            case DECIMAL_UNSIGNED:
//
//            case CHAR:
//            case VARCHAR:
//            case ENUM:
//            case TINYTEXT:
//            case MEDIUMTEXT:
//            case TEXT:
//            case LONGTEXT:
//
//            case JSON:
//            case JSONB:
//            case XML:
//
//            case BINARY:
//            case VARBINARY:
//            case TINYBLOB:
//            case MEDIUMBLOB:
//            case BLOB:
//            case LONGBLOB:
//
//            case YEAR:
//            case MONTH_DAY:
//            case YEAR_MONTH:
//
//            case TIME:
//            case TIME_WITH_TIMEZONE:
//            case DATE:
//            case TIMESTAMP:
//            case TIMESTAMP_WITH_TIMEZONE:
//
//            case BIT:
//            case VARBIT:
//
//            case DURATION:
//            case PERIOD:
//            case INTERVAL:
//
//            case UNKNOWN:
//            case ARRAY:
//            case INTERNAL_USE:
//            case COMPOSITE:
//            case USER_DEFINED:
//            case ROWID:
//            case REF_CURSOR:
//            case GEOMETRY:
//            case DIALECT_TYPE:


}
