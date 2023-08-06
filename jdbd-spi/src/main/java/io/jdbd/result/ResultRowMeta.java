package io.jdbd.result;

import io.jdbd.JdbdException;
import io.jdbd.lang.Nullable;
import io.jdbd.meta.*;

import java.util.List;

/**
 * <p>
 * This interface representing the meta data of data row of query result (eg: SELECT command).
 * </p>
 * <p>
 * The instance of this interface always is the first item of same query result in the {@link OrderedFlux}.
 * </p>
 *
 * @see ResultRow
 * @see ResultStates
 * @since 1.0
 */
public interface ResultRowMeta extends ResultItem {


    /**
     * Returns the number of columns
     *
     * @return the number of columns
     */
    int getColumnCount();


    /**
     * @see #getJdbdType(int)
     */
    DataType getDataType(int indexBasedZero) throws JdbdException;

    /**
     * @see #getDataType(int)
     */
    JdbdType getJdbdType(int indexBasedZero) throws JdbdException;


    FieldType getFieldType(int indexBasedZero) throws JdbdException;


    /**
     * @param indexBasedZero base 0,the first column is 0, the second is 1, ...
     * @throws JdbdException if a database access error occurs
     * @see #getColumnIndex(String)
     */
    BooleanMode getAutoIncrementMode(int indexBasedZero) throws JdbdException;


    /**
     * @param indexBasedZero base 0,the first column is 0, the second is 1, ...
     * @throws JdbdException if a database access error occurs
     * @see #getColumnIndex(String)
     */
    @Nullable
    String getCatalogName(int indexBasedZero) throws JdbdException;


    /**
     * @param indexBasedZero base 0,the first column is 0, the second is 1, ...
     * @throws JdbdException if a database access error occurs
     * @see #getColumnIndex(String)
     */
    @Nullable
    String getSchemaName(int indexBasedZero) throws JdbdException;


    /**
     * @param indexBasedZero base 0,the first column is 0, the second is 1, ...
     * @throws JdbdException if a database access error occurs
     * @see #getColumnIndex(String)
     */
    @Nullable
    String getTableName(int indexBasedZero) throws JdbdException;

    /**
     * Get the designated column's name.
     *
     * @param indexBasedZero base 0,the first column is 0, the second is 1, ..
     * @return column name
     * @throws JdbdException if a database access error occurs
     * @see #getColumnIndex(String)
     */
    @Nullable
    String getColumnName(int indexBasedZero) throws JdbdException;


    /**
     * get precision of column.
     * <p>
     * follow below principle:
     * <ul>
     *     <li>decimal type: max precision</li>
     *     <li>integer type: -1</li>
     *     <li>float type: - 1</li>
     *     <li>boolean type: -1</li>
     *     <li>character type: maximum character length</li>
     *     <li>datetime type: maximum allowed precision of the fractional seconds component</li>
     *     <li>binary type: maximum length of byte</li>
     *     <li>ROWID type: maximum length of byte</li>
     *     <li>other : -1</li>
     * </ul>
     * </p>
     *
     * @param indexBasedZero base 0,the first column is 0, the second is 1, ..
     * @return precision
     * @throws JdbdException if a database access error occurs
     *                       @see #getColumnIndex(String)
     */
    int getPrecision(int indexBasedZero) throws JdbdException;


    /**
     * Gets the designated column's number of digits to right of the decimal point.
     * 0 is returned for data types where the scale is not applicable.
     *
     * @param indexBasedZero base 0,the first column is 0, the second is 1, ..
     * @return scale ,-1 or scale
     * @throws JdbdException if a database access error occurs
     * @see #getColumnIndex(String)
     */
    int getScale(int indexBasedZero) throws JdbdException;

    KeyMode getKeyMode(int indexBasedZero) throws JdbdException;

    /**
     * @param indexBasedZero base 0,the first column is 0, the second is 1, ...
     * @throws JdbdException if a database access error occurs
     * @see #getColumnIndex(String)
     */
    NullMode getNullMode(int indexBasedZero) throws JdbdException;


    /**
     * <p>Returns the fully-qualified name of the Java class whose instances
     * are manufactured if the method <code>ResultSet.getObject</code>
     * is called to retrieve a value
     * from the column.  <code>io.jdbd.result.ResultRow.getObject</code> may return a subclass of the
     * class returned by this method.
     *
     * @param indexBasedZero base 0,the first column is 0, the second is 1, ..
     * @return the class in the Java programming
     * language that would be used by the method
     * <code>io.jdbd.result.ResultRow.getObject</code> to retrieve the value in the specified
     * column. This is the class name used for custom mapping.
     * @throws JdbdException if a database access error occurs
     * @see #getColumnIndex(String)
     */
    Class<?> getFirstJavaType(int indexBasedZero) throws JdbdException;

    @Nullable
    Class<?> getSecondJavaType(int indexBasedZero) throws JdbdException;


    /*-------------------below column label method-------------------*/


    DataType getDataType(String columnLabel) throws JdbdException;


    JdbdType getJdbdType(String columnLabel) throws JdbdException;


    FieldType getFieldType(String columnLabel) throws JdbdException;


    /**
     * @see #getPrecision(int)
     */
    int getPrecision(String columnLabel) throws JdbdException;


    int getScale(String columnLabel) throws JdbdException;


    KeyMode getKeyMode(String columnLabel) throws JdbdException;


    /**
     * @see #getNullMode(int)
     */
    NullMode getNullMode(String columnLabel) throws JdbdException;


    /**
     * @see #getAutoIncrementMode(int)
     */
    BooleanMode getAutoIncrementMode(String columnLabel) throws JdbdException;


    /**
     * @param columnLabel base 0,the first column is 0, the second is 1, ...
     * @throws JdbdException if a database access error occurs
     * @see #getColumnIndex(String)
     */
    @Nullable
    String getCatalogName(String columnLabel) throws JdbdException;


    /**
     * @param columnLabel base 0,the first column is 0, the second is 1, ...
     * @throws JdbdException if a database access error occurs
     * @see #getColumnIndex(String)
     */
    @Nullable
    String getSchemaName(String columnLabel) throws JdbdException;


    /**
     * @param columnLabel base 0,the first column is 0, the second is 1, ...
     * @throws JdbdException if a database access error occurs
     * @see #getColumnIndex(String)
     */
    @Nullable
    String getTableName(String columnLabel) throws JdbdException;

    /**
     * Get the designated column's name.
     *
     * @param columnLabel base 0,the first column is 0, the second is 1, ..
     * @return column name
     * @throws JdbdException if a database access error occurs
     * @see #getColumnIndex(String)
     */
    @Nullable
    String getColumnName(String columnLabel) throws JdbdException;


    Class<?> getFirstJavaType(String columnLabel) throws JdbdException;

    @Nullable
    Class<?> getSecondJavaType(String columnLabel) throws JdbdException;

    /*-------------------below column label end-------------------*/


    /**
     * @return a unmodifiable list
     */
    List<String> getColumnLabelList();


    /**
     * Gets the designated column's suggested title for use in printouts and
     * displays. The suggested title is usually specified by the SQL <code>AS</code>
     * clause.  If a SQL <code>AS</code> is not specified, the value returned from
     * <code>getColumnLabel</code> will be the same as the value returned by the
     * <code>getColumnName</code> method.
     *
     * @param indexBasedZero base 0,the first column is 0, the second is 1, ..
     * @return the suggested column title              .
     * @throws JdbdException if a database access error occurs
     */
    String getColumnLabel(int indexBasedZero) throws JdbdException;


    /**
     * <p>
     * Get column index , if columnLabel duplication ,then return last index that have same columnLabel.
     * </p>
     *
     * @param columnLabel column alias
     * @return index base 0,the first column is 0, the second is 1, ..
     * @throws JdbdException if a database access error occurs
     */
    int getColumnIndex(String columnLabel) throws JdbdException;


}
