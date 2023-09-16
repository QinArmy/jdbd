package io.jdbd.result;

import io.jdbd.JdbdException;
import io.jdbd.lang.Nullable;
import io.jdbd.meta.BooleanMode;
import io.jdbd.meta.DataType;
import io.jdbd.meta.JdbdType;
import io.jdbd.meta.KeyType;
import io.jdbd.session.Option;

import java.util.List;

/**
 * <p>
 * This interface representing the meta data of data row of query result (eg: SELECT command).
 * <br/>
 * <p>
 * The instance of this interface always is the first item of same query result in the {@link OrderedFlux}.
 * <br/>
 *
 * @see ResultRow
 * @see ResultStates
 * @since 1.0
 */
public interface ResultRowMeta extends ResultItem, ResultItem.ResultAccessSpec {


    /**
     * <p>
     * Get the {@link DataType} of appropriate column.
     * <br/>
     *
     * @param indexBasedZero index based zero,the first value is 0 .
     * @return can be following sub type:
     * <ul>
     *     <li>{@link io.jdbd.meta.SQLType},database build-in type</li>
     *     <li>{@link io.jdbd.meta.UserDefinedType},developer defined type</li>
     * </ul>
     * @throws JdbdException throw when indexBasedZero error
     * @see #getJdbdType(int)
     */
    DataType getDataType(int indexBasedZero) throws JdbdException;

    /**
     * <p>
     * Get the appropriate {@link JdbdType} of appropriate column.
     * <br/>
     *
     * @param indexBasedZero index based zero,the first value is 0 .
     * @return the {@link JdbdType} of appropriate column
     * @throws JdbdException throw when indexBasedZero error
     * @see #getDataType(int)
     */
    JdbdType getJdbdType(int indexBasedZero) throws JdbdException;

    /**
     * <p>
     * Get the  {@link FieldType} of appropriate column.
     * <br/>
     *
     * @param indexBasedZero index based zero,the first value is 0 .
     * @return {@link FieldType} of appropriate column
     * @throws JdbdException throw when indexBasedZero error
     */
    FieldType getFieldType(int indexBasedZero) throws JdbdException;


    /**
     * Get auto increment mode
     *
     * @param indexBasedZero index based zero,the first value is 0 .
     * @throws JdbdException throw when indexBasedZero error
     */
    BooleanMode getAutoIncrementMode(int indexBasedZero) throws JdbdException;


    /**
     * Get catalog name
     *
     * @param indexBasedZero index based zero,the first value is 0 .
     * @throws JdbdException throw when indexBasedZero error
     */
    @Nullable
    String getCatalogName(int indexBasedZero) throws JdbdException;


    /**
     * Get schema name
     * @param indexBasedZero index based zero,the first value is 0 .
     * @throws JdbdException throw when indexBasedZero error
     */
    @Nullable
    String getSchemaName(int indexBasedZero) throws JdbdException;


    /**
     * Get table name
     * @param indexBasedZero index based zero,the first value is 0 .
     * @throws JdbdException throw when indexBasedZero error
     */
    @Nullable
    String getTableName(int indexBasedZero) throws JdbdException;

    /**
     * Get the column's name.
     *
     * @param indexBasedZero index based zero,the first value is 0 .
     * @return column name
     * @throws JdbdException throw when indexBasedZero error
     */
    @Nullable
    String getColumnName(int indexBasedZero) throws JdbdException;


    /**
     * get precision of column.
     * <p>
     * follow below principle:
     * <ul>
     *     <li>decimal type : return precision of decimal,for example decimal(14,2),return 14</li>
     *     <li>text string type : return maximum char length</li>
     *     <li>binary type : return maximum byte length</li>
     *     <li>bit string type : return maximum bit length</li>
     *     <li>integer and float :  return 0</li>
     *     <li>time/date : return 0</li>
     *     <li>other dialect type : it's up to driver developer</li>
     * </ul>
     * <br/>
     *
     * @param indexBasedZero index based zero,the first value is 0 .
     * @return precision
     * @throws JdbdException throw when indexBasedZero error
     */
    int getPrecision(int indexBasedZero) throws JdbdException;


    /**
     * get precision of column.
     * <p>
     * follow below principle:
     * <ul>
     *     <li>decimal type : return scale of decimal,for example decimal(14,2),return 2</li>
     *     <li>integer and float :  return 0</li>
     *     <li>time and timestamp : return micro second precision,for example : time(5) return 5</li>
     *     <li>other dialect type : it's up to driver developer</li>
     * </ul>
     * <br/>
     *
     * @param indexBasedZero index based zero,the first value is 0 .
     * @return precision
     * @throws JdbdException throw when indexBasedZero error
     */
    int getScale(int indexBasedZero) throws JdbdException;

    /**
     * Get key mode
     * @param indexBasedZero index based zero,the first value is 0 .
     * @throws JdbdException throw when indexBasedZero error
     */
    KeyType getKeyMode(int indexBasedZero) throws JdbdException;

    /**
     * Get nullable mode
     * @param indexBasedZero index based zero,the first value is 0 .
     * @throws JdbdException throw when indexBasedZero error
     */
    BooleanMode getNullableMode(int indexBasedZero) throws JdbdException;


    /**
     * <p>
     * Get the first java type of appropriate column.
     * For example :
     *    <ul>
     *        <li>{@link JdbdType#BIGINT} first java type is {@link Long},second java type is null</li>
     *        <li>{@link JdbdType#LONGTEXT} first java type is {@link String},second java type is {@link io.jdbd.type.TextPath}</li>
     *         <li>{@link JdbdType#LONGBLOB} first java type is {@code  byte[]},second java type is {@link io.jdbd.type.BlobPath}</li>
     *        <li>MySQL time first java type is {@link java.time.LocalTime},second java type is {@link java.time.Duration}</li>
     *    </ul>
     * <br/>
     *
     * @param indexBasedZero index based zero,the first value is 0 .
     * @throws JdbdException throw when indexBasedZero error
     */
    Class<?> getFirstJavaType(int indexBasedZero) throws JdbdException;

    /**
     * <p>
     * Get the second java type of appropriate column.
     * For example :
     *    <ul>
     *        <li>{@link JdbdType#BIGINT} first java type is {@link Long},second java type is null</li>
     *        <li>{@link JdbdType#LONGTEXT} first java type is {@link String},second java type is {@link io.jdbd.type.TextPath}</li>
     *         <li>{@link JdbdType#LONGBLOB} first java type is {@code  byte[]},second java type is {@link io.jdbd.type.BlobPath}</li>
     *        <li>MySQL time first java type is {@link java.time.LocalTime},second java type is {@link java.time.Duration}</li>
     *    </ul>
     * <br/>
     *
     * @param indexBasedZero index based zero,the first value is 0 .
     * @throws JdbdException throw when indexBasedZero error
     */
    @Nullable
    Class<?> getSecondJavaType(int indexBasedZero) throws JdbdException;


    /**
     * <p>
     * This at least support following:
     *     <ul>
     *         <li>{@link Option#PRECISION},this option representing column precision greater than {@link Integer#MAX_VALUE},for example :  MySQL LONG TEXT</li>
     *     </ul>
     * <br/>
     *
     * @param indexBasedZero index based zero,the first value is 0 .
     * @return null if value is null or option isn't supported driver.
     * @throws JdbdException throw when indexBasedZero error
     */
    @Nullable
    <T> T getOf(int indexBasedZero, Option<T> option) throws JdbdException;


    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // rowMeta is instance of {@link ResultRowMeta}
     *             final T value;
     *             value = rowMeta.getOf(indexBasedZero,option) ;
     *             if(value == null){
     *                 throw new NullPointerException();
     *             }
     *         </code>
     *     </pre>
     * <br/>
     *
     * @throws JdbdException        throw when {@link #getOf(int, Option)} throw error
     * @throws NullPointerException throw when {@link #getOf(int, Option)} return null.
     * @see #getOf(int, Option)
     */
    <T> T getNonNullOf(int indexBasedZero, Option<T> option) throws JdbdException, NullPointerException;


    /*-------------------below column label method-------------------*/

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // rowMeta is instance of {@link ResultRowMeta}
     *             final int index;
     *             index = rowMeta.getColumnIndex(columnLabel);
     *             rowMeta.getDataType(index) ;
     *         </code>
     *     </pre>
     * <br/>
     *
     * @throws JdbdException throw when {@link #getColumnIndex(String)} throw error
     * @see #getColumnIndex(String)
     */
    DataType getDataType(String columnLabel) throws JdbdException;

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // rowMeta is instance of {@link ResultRowMeta}
     *             final int index;
     *             index = rowMeta.getColumnIndex(columnLabel);
     *             rowMeta.getJdbdType(index) ;
     *         </code>
     *     </pre>
     * <br/>
     *
     * @throws JdbdException throw when {@link #getColumnIndex(String)} throw error
     * @see #getColumnIndex(String)
     */
    JdbdType getJdbdType(String columnLabel) throws JdbdException;

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // rowMeta is instance of {@link ResultRowMeta}
     *             final int index;
     *             index = rowMeta.getColumnIndex(columnLabel);
     *             rowMeta.getFieldType(index) ;
     *         </code>
     *     </pre>
     * <br/>
     *
     * @throws JdbdException throw when {@link #getColumnIndex(String)} throw error
     * @see #getColumnIndex(String)
     */
    FieldType getFieldType(String columnLabel) throws JdbdException;


    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // rowMeta is instance of {@link ResultRowMeta}
     *             final int index;
     *             index = rowMeta.getColumnIndex(columnLabel);
     *             rowMeta.getPrecision(index) ;
     *         </code>
     *     </pre>
     * <br/>
     *
     * @throws JdbdException throw when {@link #getColumnIndex(String)} throw error
     * @see #getColumnIndex(String)
     */
    int getPrecision(String columnLabel) throws JdbdException;


    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // rowMeta is instance of {@link ResultRowMeta}
     *             final int index;
     *             index = rowMeta.getColumnIndex(columnLabel);
     *             rowMeta.getScale(index) ;
     *         </code>
     *     </pre>
     * <br/>
     *
     * @throws JdbdException throw when {@link #getColumnIndex(String)} throw error
     * @see #getColumnIndex(String)
     */
    int getScale(String columnLabel) throws JdbdException;

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // rowMeta is instance of {@link ResultRowMeta}
     *             final int index;
     *             index = rowMeta.getColumnIndex(columnLabel);
     *             rowMeta.getKeyMode(index) ;
     *         </code>
     *     </pre>
     * <br/>
     *
     * @throws JdbdException throw when {@link #getColumnIndex(String)} throw error
     * @see #getColumnIndex(String)
     */
    KeyType getKeyMode(String columnLabel) throws JdbdException;


    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // rowMeta is instance of {@link ResultRowMeta}
     *             final int index;
     *             index = rowMeta.getColumnIndex(columnLabel);
     *             rowMeta.getNullableMode(index) ;
     *         </code>
     *     </pre>
     * <br/>
     *
     * @throws JdbdException throw when {@link #getColumnIndex(String)} throw error
     * @see #getColumnIndex(String)
     */
    BooleanMode getNullableMode(String columnLabel) throws JdbdException;


    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // rowMeta is instance of {@link ResultRowMeta}
     *             final int index;
     *             index = rowMeta.getColumnIndex(columnLabel);
     *             rowMeta.getAutoIncrementMode(index) ;
     *         </code>
     *     </pre>
     * <br/>
     *
     * @throws JdbdException throw when {@link #getColumnIndex(String)} throw error
     * @see #getColumnIndex(String)
     */
    BooleanMode getAutoIncrementMode(String columnLabel) throws JdbdException;


    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // rowMeta is instance of {@link ResultRowMeta}
     *             final int index;
     *             index = rowMeta.getColumnIndex(columnLabel);
     *             rowMeta.getCatalogName(index) ;
     *         </code>
     *     </pre>
     * <br/>
     *
     * @throws JdbdException throw when {@link #getColumnIndex(String)} throw error
     * @see #getColumnIndex(String)
     */
    @Nullable
    String getCatalogName(String columnLabel) throws JdbdException;


    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // rowMeta is instance of {@link ResultRowMeta}
     *             final int index;
     *             index = rowMeta.getColumnIndex(columnLabel);
     *             rowMeta.getSchemaName(index) ;
     *         </code>
     *     </pre>
     * <br/>
     *
     * @throws JdbdException throw when {@link #getColumnIndex(String)} throw error
     * @see #getColumnIndex(String)
     */
    @Nullable
    String getSchemaName(String columnLabel) throws JdbdException;


    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // rowMeta is instance of {@link ResultRowMeta}
     *             final int index;
     *             index = rowMeta.getColumnIndex(columnLabel);
     *             rowMeta.getTableName(index) ;
     *         </code>
     *     </pre>
     * <br/>
     *
     * @throws JdbdException throw when {@link #getColumnIndex(String)} throw error
     * @see #getColumnIndex(String)
     */
    @Nullable
    String getTableName(String columnLabel) throws JdbdException;

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // rowMeta is instance of {@link ResultRowMeta}
     *             final int index;
     *             index = rowMeta.getColumnIndex(columnLabel);
     *             rowMeta.getColumnName(index) ;
     *         </code>
     *     </pre>
     * <br/>
     *
     * @throws JdbdException throw when {@link #getColumnIndex(String)} throw error
     * @see #getColumnIndex(String)
     */
    @Nullable
    String getColumnName(String columnLabel) throws JdbdException;

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // rowMeta is instance of {@link ResultRowMeta}
     *             final int index;
     *             index = rowMeta.getColumnIndex(columnLabel);
     *             rowMeta.getFirstJavaType(index) ;
     *         </code>
     *     </pre>
     * <br/>
     *
     * @throws JdbdException throw when {@link #getColumnIndex(String)} throw error
     * @see #getColumnIndex(String)
     */
    Class<?> getFirstJavaType(String columnLabel) throws JdbdException;

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // rowMeta is instance of {@link ResultRowMeta}
     *             final int index;
     *             index = rowMeta.getColumnIndex(columnLabel);
     *             rowMeta.getSecondJavaType(index) ;
     *         </code>
     *     </pre>
     * <br/>
     *
     * @throws JdbdException throw when {@link #getColumnIndex(String)} throw error
     * @see #getColumnIndex(String)
     */
    @Nullable
    Class<?> getSecondJavaType(String columnLabel) throws JdbdException;


    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // rowMeta is instance of {@link ResultRowMeta}
     *             final int index;
     *             index = rowMeta.getColumnIndex(columnLabel);
     *             rowMeta.getOf(index,option) ;
     *         </code>
     *     </pre>
     * <br/>
     *
     * @throws JdbdException throw when {@link #getColumnIndex(String)} throw error
     * @see #getColumnIndex(String)
     */
    @Nullable
    <T> T getOf(String columnLabel, Option<T> option) throws JdbdException;

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // rowMeta is instance of {@link ResultRowMeta}
     *             final int index;
     *             index = rowMeta.getColumnIndex(columnLabel);
     *             rowMeta.getNonNullOf(index,option) ;
     *         </code>
     *     </pre>
     * <br/>
     *
     * @throws JdbdException        throw when {@link #getColumnIndex(String)} throw error
     * @throws NullPointerException throw when {@link #getNonNullOf(int, Option)}
     * @see #getColumnIndex(String)
     */
    <T> T getNonNullOf(String columnLabel, Option<T> option) throws JdbdException, NullPointerException;


    /*-------------------below column label end-------------------*/


    /**
     * <p>
     * Get the column label list of row meta
     * <br/>
     *
     * @return a unmodifiable list
     */
    List<String> getColumnLabelList();


}
