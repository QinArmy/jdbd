package io.jdbd.result;

import io.jdbd.JdbdException;
import io.jdbd.lang.Nullable;
import io.jdbd.type.TextPath;
import org.reactivestreams.Publisher;

import java.math.BigDecimal;
import java.nio.file.CopyOption;
import java.nio.file.Path;
import java.time.*;
import java.util.*;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * <p>
 * This interface is base interface of following :
 *     <ul>
 *         <li>{@link ResultRow}</li>
 *         <li>{@link CurrentRow}</li>
 *     </ul>
 * </p>
 * <p>
 * The {@link #getResultNo()} of this interface always return same value with {@link ResultRowMeta} in same query result.
 * See {@link #getRowMeta()}
 * </p>
 * <p>
 *     This interface have following core 'get' methods:
 *     <ul>
 *         <li>{@link #get(int)}</li>
 *         <li>{@link #get(int, Class)}</li>
 *         <li>{@link #getString(int)} ,this method is similar to {@linkplain  #get(int, Class String.class)}, except that binary or blob return normal string not hex string.</li>
 *         <li>{@link #getList(int, Class, IntFunction)}</li>
 *         <li>{@link #getSet(int, Class, IntFunction)}</li>
 *         <li>{@link #getMap(int, Class, Class, IntFunction)}</li>
 *         <li>{@link #getPublisher(int, Class)}</li>
 *     </ul>
 *     other 'get' methods are only the decoration of above methods.
 * </p>
 *
 * @since 1.0
 */
public interface DataRow extends ResultItem, ResultItem.ResultAccessSpec {


    ResultRowMeta getRowMeta();

    /**
     * <p>
     * Bit row means that exists at least one big column. see {@link #isBigColumn(int)}
     * </p>
     *
     * @return true : big row
     */
    boolean isBigRow();

    /**
     * <p>
     * Big column means that byte size is large,possibly is 1GB or more. For example LONG BLOB ,LONG TEXT,GEOMETRY_COLLECTION.
     * Driver cache them to local temp directory.
     * So {@link #get(int)} return :
     *      <ul>
     *          <li>{@link io.jdbd.type.BlobPath}</li>
     *          <li>{@link io.jdbd.type.TextPath}</li>
     *      </ul>
     *      If you need to store them ,then you must move them to other directory by {@link java.nio.file.Files#move(Path, Path, CopyOption...)} method.
     *      Else driver will delete them after result set end.
     * </p>
     *
     * @param indexBasedZero index based zero,the first value is 0 .
     * @return true : big column
     * @throws JdbdException throw when indexBasedZero error
     */
    boolean isBigColumn(int indexBasedZero) throws JdbdException;

    /**
     * @throws JdbdException throw when indexBasedZero error
     */
    boolean isNull(int indexBasedZero) throws JdbdException;

    /**
     * <p>
     * Get the value of output of column.
     * </p>
     *
     * @return must be one of following : <ol>
     * <li>null</li>
     * <li>the instance of {@link ResultRowMeta#getFirstJavaType(int) }</li>
     * <li>the instance of {@link ResultRowMeta#getSecondJavaType(int)}</li>
     * </ol>
     * @see ResultRowMeta#getFirstJavaType(int)
     * @see ResultRowMeta#getSecondJavaType(int)
     * @see #isBigColumn(int)
     */
    @Nullable
    Object get(int indexBasedZero) throws JdbdException;

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // row is instance of {@link DataRow}
     *             Object value;
     *             value = row.get(indexBasedZero);
     *              if(value == null){
     *                  value = this.checkDefaultValue(defaultValue); // checkDefaultValue is driver developer provide private method.
     *              }
     *         </code>
     *     </pre>
     * </p>
     *
     * @param indexBasedZero index based zero,the first value is 0 .
     * @param defaultValue   must be the instance of column first java type or second java type.
     * @return non-null value
     * @throws JdbdException throw when
     *                       <ul>
     *                           <li>{@link #get(int)} throw {@link JdbdException}</li>
     *                           <li>defaultValue isn't the instance of column first java type or second java type</li>
     *                       </ul>
     * @see ResultRowMeta#getFirstJavaType(int)
     * @see ResultRowMeta#getSecondJavaType(int)
     */
    Object getOrDefault(int indexBasedZero, Object defaultValue) throws JdbdException;

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // row is instance of {@link DataRow}
     *             Object value;
     *             value = row.get(indexBasedZero);
     *              if(value == null){
     *                  value = this.checkDefaultValue(supplier.get()); // checkDefaultValue is driver developer provide private method.
     *              }
     *         </code>
     *     </pre>
     * </p>
     *
     * @param supplier       must return the instance of column first java type or second java type.
     * @param indexBasedZero index based zero,the first value is 0 .
     * @return non-null value
     * @throws JdbdException throw when
     *                       <ul>
     *                           <li>{@link #get(int)} throw {@link JdbdException}</li>
     *                           <li>defaultValue isn't the instance of column first java type or second java type</li>
     *                       </ul>
     * @see ResultRowMeta#getFirstJavaType(int)
     * @see ResultRowMeta#getSecondJavaType(int)
     */
    Object getOrSupplier(int indexBasedZero, Supplier<Object> supplier) throws JdbdException;


    /**
     * <p>
     * Get appropriate column value and try to convert to target type.
     * </p>
     * <p>
     * The convert rule :
     *     <ul>
     *         <li>columnClass is {@link String} class column value can be following type:
     *              <ol>
     *                  <li>{@code null}</li>
     *                  <li>{@link String}</li>
     *                  <li>{@link Boolean}</li>
     *                  <li>{@link BigDecimal},use {@link BigDecimal#toPlainString()}</li>
     *                  <li>{@link LocalDateTime}</li>
     *                  <li>{@link java.time.LocalDate}</li>
     *                  <li>{@link YearMonth}</li>
     *                  <li>{@link MonthDay}</li>
     *                  <li>{@link LocalTime}</li>
     *                  <li>{@link OffsetTime}</li>
     *                  <li>{@code byte[]} convert to hex string</li>
     *                  <li>{@link io.jdbd.type.BlobPath} convert to hex string</li>
     *                  <li>{@link BitSet}</li>
     *                  <li>{@link TextPath}</li>
     *              </ol>
     *              if overflow ,throw {@link JdbdException}
     *         </li>
     *         <li>columnClass is {@code byte[]} class column value can be following type:
     *              <ol>
     *                  <li>{@code null}</li>
     *                  <li>{@code byte[]} convert to hex string</li>
     *                  <li>{@link io.jdbd.type.BlobPath} convert to hex string</li>
     *              </ol>
     *               if overflow ,throw {@link JdbdException}
     *         </li>
     *         <li>columnClass is {@link Boolean} or {@link io.jdbd.meta.BooleanMode} class, column value can be following type:
     *              <ol>
     *                  <li>{@code null}</li>
     *                  <li>{@link Boolean}</li>
     *                  <li>{@link Number}, zero value : convert to {@link Boolean#FALSE} ; non-zero value : convert to {@link Boolean#TRUE}</li>
     *                  <li>{@link String}
     *                      <ol>
     *                          <li>ignore case 'TRUE','T','ON','YES','1' :  convert to {@link Boolean#TRUE} ; ignore case 'FALSE','F','OFF','NO','0' :  convert to {@link Boolean#FALSE};</li>
     *                      </ol>
     *                  </li>
     *              </ol>
     *               if overflow ,throw {@link JdbdException}
     *         </li>
     *         <li>columnClass is integer number type:
     *           <ul>
     *               <li>{@link Byte}</li>
     *               <li>{@link Short}</li>
     *               <li>{@link Integer}</li>
     *               <li>{@link Long}</li>
     *               <li>{@link java.math.BigInteger}</li>
     *           </ul>
     *              column value can be following type:
     *           <ol>
     *               <li>{@code null}</li>
     *               <li>{@link Byte}</li>
     *               <li>{@link Short}</li>
     *               <li>{@link Integer}</li>
     *               <li>{@link Long}</li>
     *               <li>{@link java.math.BigInteger}</li>
     *               <li>{@link String}</li>
     *               <li>{@link BigDecimal}, fractional part must be 0</li>
     *                <li>{@link Boolean} , true : 1 ; false : 0</li>
     *           </ol>
     *              if overflow ,throw {@link JdbdException}
     *         </li>
     *         <li>columnClass is {@link BigDecimal},column value can be following type:
     *           <ol>
     *               <li>{@code null}</li>
     *               <li>{@link Byte}</li>
     *               <li>{@link Short}</li>
     *               <li>{@link Integer}</li>
     *               <li>{@link Long}</li>
     *               <li>{@link java.math.BigInteger}</li>
     *               <li>{@link String}</li>
     *               <li>{@link BigDecimal}, fractional part must be 0</li>
     *               <li>{@link Boolean} , true : 1 ; false : 0</li>
     *               <li>{@link Float}, use {@link java.math.BigDecimal#BigDecimal(String)} constructor</li>
     *               <li>{@link Double}, use {@link java.math.BigDecimal#BigDecimal(String)} constructor</li>
     *           </ol>
     *              if overflow ,throw {@link JdbdException}
     *         </li>
     *         <li>columnClass is {@link Double} ,column value can be following type:
     *           <ol>
     *               <li>{@code null}</li>
     *               <li>{@link Double}</li>
     *               <li>{@link Float}</li>
     *               <li>{@link Byte}</li>
     *               <li>{@link Short}</li>
     *               <li>{@link Integer}</li>
     *               <li>{@link String}</li>
     *               <li>{@link Boolean} , true : 1.0 ; false : 0.0</li>
     *           </ol>
     *              if overflow ,throw {@link JdbdException}
     *         </li>
     *         <li>columnClass is {@link Float} ,column value can be following type:
     *           <ol>
     *               <li>{@code null}</li>
     *               <li>{@link Float}</li>
     *               <li>{@link Byte}</li>
     *               <li>{@link Short}</li>
     *               <li>{@link String}</li>
     *               <li>{@link Boolean} , true : 1.0 ; false : 0.0</li>
     *           </ol>
     *              if overflow ,throw {@link JdbdException}
     *         </li>
     *         <li>columnClass is {@link LocalDateTime} ,column value can be following type:
     *           <ol>
     *               <li>{@code null}</li>
     *               <li>{@link LocalDateTime}</li>
     *               <li>{@link String}</li>
     *               <li>{@link LocalDate},time part is {@link LocalTime#MIDNIGHT}</li>
     *           </ol>
     *              if overflow ,throw {@link JdbdException}
     *         </li>
     *         <li>columnClass is {@link OffsetDateTime} or {@link ZonedDateTime} ,column value can be following type:
     *           <ol>
     *               <li>{@code null}</li>
     *               <li>{@link OffsetDateTime}</li>
     *                <li>{@link ZonedDateTime}</li>
     *               <li>{@link LocalDateTime},only when database don't support timestamp with timezone ,eg : MySQL,{@link ZoneOffset} part is server zone</li>
     *               <li>{@link String}</li>
     *           </ol>
     *              if overflow ,throw {@link JdbdException}
     *         </li>
     *         <li>columnClass is {@link LocalTime} ,column value can be following type:
     *           <ol>
     *               <li>{@code null}</li>
     *               <li>{@link LocalTime}</li>
     *               <li>{@link String}</li>
     *           </ol>
     *              if overflow ,throw {@link JdbdException}
     *         </li>
     *         <li>columnClass is {@link OffsetTime} ,column value can be following type:
     *           <ol>
     *               <li>{@code null}</li>
     *               <li>{@link OffsetTime}</li>
     *               <li>{@link LocalTime},only when database don't support time with timezone ,eg : MySQL,{@link ZoneOffset} part is server zone</li>
     *               <li>{@link String}</li>
     *           </ol>
     *              if overflow ,throw {@link JdbdException}
     *         </li>
     *         <li>columnClass is {@link LocalDate} ,column value can be following type:
     *           <ol>
     *               <li>{@code null}</li>
     *               <li>{@link LocalDate}</li>
     *               <li>{@link String}</li>
     *           </ol>
     *              if overflow ,throw {@link JdbdException}
     *         </li>
     *         <li>columnClass is {@link YearMonth} ,column value can be following type:
     *           <ol>
     *               <li>{@code null}</li>
     *               <li>{@link LocalDate}</li>
     *               <li>{@link YearMonth}</li>
     *               <li>{@link LocalDateTime}</li>
     *               <li>{@link String}</li>
     *           </ol>
     *              if overflow ,throw {@link JdbdException}
     *         </li>
     *         <li>columnClass is {@link MonthDay} ,column value can be following type:
     *           <ol>
     *               <li>{@code null}</li>
     *                <li>{@link LocalDate}</li>
     *               <li>{@link MonthDay}</li>
     *               <li>{@link LocalDateTime}</li>
     *               <li>{@link String}</li>
     *           </ol>
     *              if overflow ,throw {@link JdbdException}
     *         </li>
     *         <li>columnClass is {@link Month} ,column value can be following type:
     *           <ol>
     *               <li>{@code null}</li>
     *                <li>{@link LocalDate}</li>
     *               <li>{@link Month}</li>
     *               <li>{@link LocalDateTime}</li>
     *               <li>{@link YearMonth}</li>
     *               <li>{@link MonthDay}</li>
     *               <li>{@link String}</li>
     *           </ol>
     *              if overflow ,throw {@link JdbdException}
     *         </li>
     *         <li>columnClass is {@link DayOfWeek} ,column value can be following type:
     *           <ol>
     *               <li>{@code null}</li>
     *                <li>{@link LocalDate}</li>
     *               <li>{@link DayOfWeek}</li>
     *               <li>{@link LocalDateTime}</li>
     *               <li>{@link String}</li>
     *           </ol>
     *              if overflow ,throw {@link JdbdException}
     *         </li>
     *         <li>columnClass is {@link Enum} ,column value can be following type:
     *           <ol>
     *               <li>{@code null}</li>
     *               <li>{@link String}</li>
     *           </ol>
     *              if overflow ,throw {@link JdbdException}
     *         </li>
     *         <li>columnClass is {@link BitSet} ,column value can be following type:
     *           <ol>
     *               <li>{@code null}</li>
     *               <li>{@link BitSet}</li>
     *               <li>{@link Boolean}</li>
     *               <li>{@link String}</li>
     *               <li>{@link Byte}</li>
     *               <li>{@link Short}</li>
     *               <li>{@link Integer}</li>
     *               <li>{@link Long}</li>
     *               <li>{@link java.math.BigInteger}</li>
     *           </ol>
     *              if overflow ,throw {@link JdbdException}
     *         </li>
     *         <li>columnClass is {@link io.jdbd.type.Point} ,column value can be following type:
     *           <ol>
     *               <li>{@code null}</li>
     *               <li>{@link io.jdbd.type.Point}</li>
     *               <li>{@code byte[]}, WKB</li>
     *               <li>{@link String}, WKT</li>
     *           </ol>
     *              if overflow ,throw {@link JdbdException}
     *         </li>
     *         <li>columnClass is {@link Duration} ,column value can be following type:
     *           <ol>
     *               <li>{@code null}</li>
     *               <li>{@link Duration}</li>
     *               <li>{@link LocalTime}, for example : MySQL time</li>
     *               <li>{@link String}</li>
     *           </ol>
     *              if overflow ,throw {@link JdbdException}
     *         </li>
     *         <li>columnClass is {@link Period} ,column value can be following type:
     *           <ol>
     *               <li>{@code null}</li>
     *               <li>{@link Period}</li>
     *               <li>{@link String}</li>
     *           </ol>
     *              if overflow ,throw {@link JdbdException}
     *         </li>
     *     </ul>
     * </p>
     *
     * @param indexBasedZero index based zero,the first value is 0 .
     * @param columnClass    target type class
     * @throws JdbdException throw when
     *                       <ul>
     *                           <li>indexBasedZero error</li>
     *                           <li>appropriate column value couldn't convert to target type</li>
     *                       </ul>
     */
    @Nullable
    <T> T get(int indexBasedZero, Class<T> columnClass) throws JdbdException;

    /**
     * <p>
     * This method is similar to {@linkplain  #get(int, Class String.class)}, except that binary or blob return normal string not hex string.
     * </p>
     *
     * @param indexBasedZero index based zero,the first value is 0 .
     * @throws JdbdException throw when
     *                       <ul>
     *                           <li>indexBasedZero error</li>
     *                           <li>appropriate column value couldn't convert to {@link String} type</li>
     *                       </ul>
     */
    @Nullable
    String getString(int indexBasedZero) throws JdbdException;

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // row is instance of {@link DataRow}
     *             String value;
     *             value = row.getString(indexBasedZero);
     *              if(value == null){
     *                  value = defaultValue;
     *              }
     *         </code>
     *     </pre>
     * </p>
     *
     * @param indexBasedZero index based zero,the first value is 0 .
     * @param defaultValue   non-null
     * @return non-null value
     * @throws JdbdException throw when {@link #getString(int)} throw {@link JdbdException}
     * @see #getString(int)
     */
    String getStringOrDefault(int indexBasedZero, String defaultValue) throws JdbdException;

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // row is instance of {@link DataRow}
     *             String value;
     *             value = row.getString(indexBasedZero);
     *              if(value == null){
     *                  value = supplier.get();
     *              }
     *         </code>
     *     </pre>
     * </p>
     *
     * @param indexBasedZero index based zero,the first value is 0 .
     * @param supplier       return non-null
     * @return non-null value
     * @throws JdbdException throw when {@link #getString(int)} throw {@link JdbdException}
     * @see #getString(int)
     */
    String getStringOrSupplier(int indexBasedZero, Supplier<String> supplier) throws JdbdException;

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // row is instance of {@link DataRow}
     *             String value;
     *             value = row.getString(indexBasedZero);
     *              if(value == null){
     *                  throw new NullPointerException();
     *              }
     *         </code>
     *     </pre>
     * </p>
     *
     * @param indexBasedZero index based zero,the first value is 0 .
     * @return non-null value
     * @throws JdbdException        throw when {@link #getString(int)} throw {@link JdbdException}
     * @throws NullPointerException throw when {@link #getString(int)} return null.
     * @see #getString(int)
     */
    String getNonNullString(int indexBasedZero) throws JdbdException, NullPointerException;

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // row is instance of {@link DataRow}
     *             T value;
     *             value = row.get(indexBasedZero,columnClass);
     *              if(value == null){
     *                  value = defaultValue;
     *              }
     *         </code>
     *     </pre>
     * </p>
     *
     * @param defaultValue non-null
     * @return non-null value
     * @throws JdbdException throw when {@link #get(int, Class)} throw {@link JdbdException}
     * @see #get(int, Class)
     */
    <T> T getOrDefault(int indexBasedZero, Class<T> columnClass, T defaultValue) throws JdbdException;

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // row is instance of {@link DataRow}
     *             T value;
     *             value = row.get(indexBasedZero,columnClass);
     *              if(value == null){
     *                  value = Objects.requireNonNull(supplier.get());
     *              }
     *         </code>
     *     </pre>
     * </p>
     *
     * @param supplier return non-null
     * @return non-null value
     * @throws JdbdException        throw when {@link #get(int, Class)} throw {@link JdbdException}
     * @throws NullPointerException throw when supplier return null.
     * @see #get(int, Class)
     */
    <T> T getOrSupplier(int indexBasedZero, Class<T> columnClass, Supplier<T> supplier) throws JdbdException;

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // row is instance of {@link DataRow}
     *             Object value;
     *             value = row.get(indexBasedZero);
     *              if(value == null){
     *                  throw new NullPointerException();
     *              }
     *         </code>
     *     </pre>
     * </p>
     *
     * @return non-null value
     * @throws JdbdException        throw when {@link #get(int)} throw {@link JdbdException}
     * @throws NullPointerException throw when {@link #get(int)} return null.
     * @see #get(int)
     */
    Object getNonNull(int indexBasedZero) throws NullPointerException, JdbdException;

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // row is instance of {@link DataRow}
     *             T value;
     *             value = row.get(indexBasedZero,columnClass);
     *              if(value == null){
     *                  throw new NullPointerException();
     *              }
     *         </code>
     *     </pre>
     * </p>
     *
     * @return non-null value
     * @throws JdbdException        throw when {@link #get(int, Class)} throw {@link JdbdException}
     * @throws NullPointerException throw when {@link #get(int, Class)} return null.
     * @see #get(int, Class)
     */
    <T> T getNonNull(int indexBasedZero, Class<T> columnClass) throws NullPointerException, JdbdException;


    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // row is instance of {@link DataRow}
     *             List&lt;T> list;
     *             list = row.get(indexBasedZero,elementClass,ArrayList::new);
     *         </code>
     *     </pre>
     * </p>
     *
     * @return non-null
     * @throws JdbdException throw when {@link #getList(int, Class, IntFunction) } throw {@link JdbdException}
     * @see #getList(int, Class, IntFunction)
     */
    <T> List<T> getList(int indexBasedZero, Class<T> elementClass) throws JdbdException;

    /**
     * <p>
     * Convert appropriate column value to {@link List}
     * </p>
     *
     * @param indexBasedZero index based zero,the first value is 0 .
     * @return <ul>
     * <li>null : always {@link Collections#emptyList()}</li>
     * <li>the instance that constructor return</li>
     * </ul>
     * @throws JdbdException throw when
     *                       <ul>
     *                           <li>indexBasedZero error</li>
     *                           <li>appropriate column value couldn't convert to {@link List} type</li>
     *                       </ul>
     */
    <T> List<T> getList(int indexBasedZero, Class<T> elementClass, IntFunction<List<T>> constructor) throws JdbdException;

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // row is instance of {@link DataRow}
     *             Set&lt;T> set;
     *             set = row.getSet(indexBasedZero,elementClass,HashSet::new);
     *         </code>
     *     </pre>
     * </p>
     *
     * @return non-null
     * @throws JdbdException throw when {@link #getSet(int, Class, IntFunction) } throw {@link JdbdException}
     * @see #getSet(int, Class, IntFunction)
     */
    <T> Set<T> getSet(int indexBasedZero, Class<T> elementClass) throws JdbdException;

    /**
     * <p>
     * Convert appropriate column value to {@link Set}
     * </p>
     *
     * @param indexBasedZero index based zero,the first value is 0 .
     * @return <ul>
     * <li>null : always {@link Collections#emptySet()}</li>
     * <li>the instance that constructor return</li>
     * </ul>
     * @throws JdbdException throw when
     *                       <ul>
     *                           <li>indexBasedZero error</li>
     *                           <li>appropriate column value couldn't convert to {@link Set} type</li>
     *                       </ul>
     */
    <T> Set<T> getSet(int indexBasedZero, Class<T> elementClass, IntFunction<Set<T>> constructor) throws JdbdException;

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // row is instance of {@link DataRow}
     *             Map&lt;K,V> map;
     *             map = row.getMap(indexBasedZero,keyClass,valueClass,HashMap::new);
     *         </code>
     *     </pre>
     * </p>
     *
     * @return non-null
     * @throws JdbdException throw when {@link #getMap(int, Class, Class, IntFunction) } throw {@link JdbdException}
     * @see #getMap(int, Class, Class, IntFunction)
     */
    <K, V> Map<K, V> getMap(int indexBasedZero, Class<K> keyClass, Class<V> valueClass) throws JdbdException;

    /**
     * <p>
     * Convert appropriate column value to {@link Map}
     * </p>
     *
     * @param indexBasedZero index based zero,the first value is 0 .
     * @return <ul>
     * <li>null : always {@link Collections#emptyMap()}</li>
     * <li>the instance that constructor return</li>
     * </ul>
     * @throws JdbdException throw when
     *                       <ul>
     *                           <li>indexBasedZero error</li>
     *                           <li>appropriate column value couldn't convert to {@link Map} type</li>
     *                       </ul>
     */
    <K, V> Map<K, V> getMap(int indexBasedZero, Class<K> keyClass, Class<V> valueClass, IntFunction<Map<K, V>> constructor) throws JdbdException;

    /**
     * <p>
     * Convert appropriate column value to {@link Publisher}
     * </p>
     *
     * @param indexBasedZero index based zero,the first value is 0 .
     * @throws JdbdException throw when
     *                       <ul>
     *                           <li>indexBasedZero error</li>
     *                           <li>appropriate column value couldn't convert to {@link Publisher} type</li>
     *                       </ul>
     */
    <T> Publisher<T> getPublisher(int indexBasedZero, Class<T> valueClass) throws JdbdException;

    /*-------------------below column label methods -------------------*/

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *              // row is instance of {@link DataRow}
     *             final int index;
     *             index = row.getColumnIndex(columnLabel);
     *             return row.isBigColumn(index) ;
     *         </code>
     *     </pre>
     * </p>
     *
     * @throws JdbdException throw when {@link #getColumnIndex(String)} throw error
     * @see #getColumnIndex(String)
     */
    boolean isBigColumn(String columnLabel);

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *              // row is instance of {@link DataRow}
     *             final int index;
     *             index = row.getColumnIndex(columnLabel);
     *             return row.isNull(index) ;
     *         </code>
     *     </pre>
     * </p>
     *
     * @throws JdbdException throw when {@link #getColumnIndex(String)} throw error
     * @see #getColumnIndex(String)
     */
    boolean isNull(String columnLabel) throws JdbdException;

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *              // row is instance of {@link DataRow}
     *             final int index;
     *             index = row.getColumnIndex(columnLabel);
     *             return row.get(index) ;
     *         </code>
     *     </pre>
     * </p>
     *
     * @throws JdbdException throw when {@link #getColumnIndex(String)} throw error
     * @see #getColumnIndex(String)
     */
    @Nullable
    Object get(String columnLabel) throws JdbdException;


    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *              // row is instance of {@link DataRow}
     *             final int index;
     *             index = row.getColumnIndex(columnLabel);
     *             return row.getOrDefault(index,defaultValue) ;
     *         </code>
     *     </pre>
     * </p>
     *
     * @throws JdbdException throw when {@link #getColumnIndex(String)} throw error
     * @see #getColumnIndex(String)
     */
    Object getOrDefault(String columnLabel, Object defaultValue) throws JdbdException;

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *              // row is instance of {@link DataRow}
     *             final int index;
     *             index = row.getColumnIndex(columnLabel);
     *             return row.getOrSupplier(index,supplier) ;
     *         </code>
     *     </pre>
     * </p>
     *
     * @throws JdbdException throw when {@link #getColumnIndex(String)} throw error
     * @see #getColumnIndex(String)
     */
    Object getOrSupplier(String columnLabel, Supplier<Object> supplier) throws JdbdException;


    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *              // row is instance of {@link DataRow}
     *             final int index;
     *             index = row.getColumnIndex(columnLabel);
     *             return row.get(index,columnClass) ;
     *         </code>
     *     </pre>
     * </p>
     *
     * @throws JdbdException throw when {@link #getColumnIndex(String)} throw error
     * @see #getColumnIndex(String)
     */
    @Nullable
    <T> T get(String columnLabel, Class<T> columnClass) throws JdbdException;

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *              // row is instance of {@link DataRow}
     *             final int index;
     *             index = row.getColumnIndex(columnLabel);
     *             return row.getString(index) ;
     *         </code>
     *     </pre>
     * </p>
     *
     * @throws JdbdException throw when {@link #getColumnIndex(String)} throw error
     * @see #getColumnIndex(String)
     */
    @Nullable
    String getString(String columnLabel) throws JdbdException;

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *              // row is instance of {@link DataRow}
     *             final int index;
     *             index = row.getColumnIndex(columnLabel);
     *             return row.getStringOrDefault(index,defaultValue) ;
     *         </code>
     *     </pre>
     * </p>
     *
     * @throws JdbdException throw when {@link #getColumnIndex(String)} throw error
     * @see #getColumnIndex(String)
     */
    String getStringOrDefault(String columnLabel, String defaultValue) throws JdbdException;

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *              // row is instance of {@link DataRow}
     *             final int index;
     *             index = row.getColumnIndex(columnLabel);
     *             return row.getStringOrSupplier(index,supplier) ;
     *         </code>
     *     </pre>
     * </p>
     *
     * @throws JdbdException throw when {@link #getColumnIndex(String)} throw error
     * @see #getColumnIndex(String)
     */
    String getStringOrSupplier(String columnLabel, Supplier<String> supplier) throws JdbdException;

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *              // row is instance of {@link DataRow}
     *             final int index;
     *             index = row.getColumnIndex(columnLabel);
     *             return row.getNonNullString(index) ;
     *         </code>
     *     </pre>
     * </p>
     *
     * @throws JdbdException throw when {@link #getColumnIndex(String)} throw error
     * @see #getColumnIndex(String)
     */
    String getNonNullString(String columnLabel) throws JdbdException, NullPointerException;

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *              // row is instance of {@link DataRow}
     *             final int index;
     *             index = row.getColumnIndex(columnLabel);
     *             return row.getOrDefault(index,columnClass,defaultValue) ;
     *         </code>
     *     </pre>
     * </p>
     *
     * @throws JdbdException throw when {@link #getColumnIndex(String)} throw error
     * @see #getColumnIndex(String)
     */
    <T> T getOrDefault(String columnLabel, Class<T> columnClass, T defaultValue) throws JdbdException;

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *              // row is instance of {@link DataRow}
     *             final int index;
     *             index = row.getColumnIndex(columnLabel);
     *             return row.getOrSupplier(index,columnClass,supplier) ;
     *         </code>
     *     </pre>
     * </p>
     *
     * @throws JdbdException throw when {@link #getColumnIndex(String)} throw error
     * @see #getColumnIndex(String)
     */
    <T> T getOrSupplier(String columnLabel, Class<T> columnClass, Supplier<T> supplier) throws JdbdException;

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *              // row is instance of {@link DataRow}
     *             final int index;
     *             index = row.getColumnIndex(columnLabel);
     *             return row.getNonNull(index) ;
     *         </code>
     *     </pre>
     * </p>
     *
     * @throws JdbdException throw when {@link #getColumnIndex(String)} throw error
     * @see #getColumnIndex(String)
     */
    Object getNonNull(String columnLabel) throws NullPointerException, JdbdException;

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *              // row is instance of {@link DataRow}
     *             final int index;
     *             index = row.getColumnIndex(columnLabel);
     *             return row.getNonNull(index,columnClass) ;
     *         </code>
     *     </pre>
     * </p>
     *
     * @throws JdbdException throw when {@link #getColumnIndex(String)} throw error
     * @see #getColumnIndex(String)
     */
    <T> T getNonNull(String columnLabel, Class<T> columnClass) throws NullPointerException, JdbdException;


    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *              // row is instance of {@link DataRow}
     *             final int index;
     *             index = row.getColumnIndex(columnLabel);
     *             return row.getList(index,elementClass) ;
     *         </code>
     *     </pre>
     * </p>
     *
     * @throws JdbdException throw when {@link #getColumnIndex(String)} throw error
     * @see #getColumnIndex(String)
     */
    <T> List<T> getList(String columnLabel, Class<T> elementClass) throws JdbdException;

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *              // row is instance of {@link DataRow}
     *             final int index;
     *             index = row.getColumnIndex(columnLabel);
     *             return row.getList(index,elementClass,constructor) ;
     *         </code>
     *     </pre>
     * </p>
     *
     * @throws JdbdException throw when {@link #getColumnIndex(String)} throw error
     * @see #getColumnIndex(String)
     */
    <T> List<T> getList(String columnLabel, Class<T> elementClass, IntFunction<List<T>> constructor) throws JdbdException;

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *              // row is instance of {@link DataRow}
     *             final int index;
     *             index = row.getColumnIndex(columnLabel);
     *             return row.getSet(index,elementClass) ;
     *         </code>
     *     </pre>
     * </p>
     *
     * @throws JdbdException throw when {@link #getColumnIndex(String)} throw error
     * @see #getColumnIndex(String)
     */
    <T> Set<T> getSet(String columnLabel, Class<T> elementClass) throws JdbdException;

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *              // row is instance of {@link DataRow}
     *             final int index;
     *             index = row.getColumnIndex(columnLabel);
     *             return row.getSet(index,elementClass,constructor) ;
     *         </code>
     *     </pre>
     * </p>
     *
     * @throws JdbdException throw when {@link #getColumnIndex(String)} throw error
     * @see #getColumnIndex(String)
     */
    <T> Set<T> getSet(String columnLabel, Class<T> elementClass, IntFunction<Set<T>> constructor) throws JdbdException;


    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *              // row is instance of {@link DataRow}
     *             final int index;
     *             index = row.getColumnIndex(columnLabel);
     *             return row.getMap(index,keyClass,keyClass) ;
     *         </code>
     *     </pre>
     * </p>
     *
     * @throws JdbdException throw when {@link #getColumnIndex(String)} throw error
     * @see #getColumnIndex(String)
     */
    <K, V> Map<K, V> getMap(String columnLabel, Class<K> keyClass, Class<V> valueClass) throws JdbdException;

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *              // row is instance of {@link DataRow}
     *             final int index;
     *             index = row.getColumnIndex(columnLabel);
     *             return row.getMap(index,keyClass,keyClass,constructor) ;
     *         </code>
     *     </pre>
     * </p>
     *
     * @throws JdbdException throw when {@link #getColumnIndex(String)} throw error
     * @see #getColumnIndex(String)
     */
    <K, V> Map<K, V> getMap(String columnLabel, Class<K> keyClass, Class<V> valueClass, IntFunction<Map<K, V>> constructor) throws JdbdException;

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *              // row is instance of {@link DataRow}
     *             final int index;
     *             index = row.getColumnIndex(columnLabel);
     *             return row.getPublisher(index,valueClass) ;
     *         </code>
     *     </pre>
     * </p>
     *
     * @throws JdbdException throw when
     *                       <ul>
     *                           <li>{@link #getColumnIndex(String)} throw error</li>
     *                           <li>{@link #getPublisher(int, Class)}</li>
     *                       </ul>
     * @see #getColumnIndex(String)
     * @see #getPublisher(int, Class)
     */
    <T> Publisher<T> getPublisher(String columnLabel, Class<T> valueClass) throws JdbdException;


}
