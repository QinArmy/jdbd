package io.jdbd.result;

import io.jdbd.JdbdException;
import io.jdbd.session.Closeable;
import io.jdbd.session.DatabaseSession;
import io.jdbd.session.Option;
import io.jdbd.session.OptionSpec;
import org.reactivestreams.Publisher;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * This interface representing reference of server database cursor.
 * <br/>
 * <p>
 * This interface is similar to {@code java.sql.ResultSet}, except that this interface is reactive.
 * <br/>
 * <p>
 * Application developer can get the instance of this interface by following method:
 * <ul>
 *     <li>{@link DatabaseSession#refCursor(String, Function)}</li>
 *     <li>{@link DataRow#get(int, Class)}</li>
 *     <li>{@link ResultStates#valueOf(Option)}, see {@link Option#CURSOR}</li>
 * </ul>
 * <br/>
 * <p>
 * The cursor will be close in following scenarios :
 *     <ul>
 *         <li>If {@link io.jdbd.session.Option#AUTO_CLOSE_ON_ERROR} is true and the any method of {@link Cursor} emit {@link Throwable}</li>
 *         <li>You invoke {@link #forwardAllAndClosed(Function)}</li>
 *         <li>You invoke {@link #forwardAllAndClosed(Function, Consumer)}</li>
 *         <li>You invoke {@link #forwardAllAndClosed()}</li>
 *         <li>You invoke {@link #close()}</li>
 *     </ul>
 * If the methods of {@link Cursor} don't emit any {@link Throwable},then you should close cursor.
 * If you don't close cursor ,the {@link io.jdbd.session.DatabaseSession} that create this {@link Cursor} can still execute new {@link io.jdbd.statement.Statement},
 * but you shouldn't do this.
 * <br/>
 *
 * @see io.jdbd.session.Option#AUTO_CLOSE_ON_ERROR
 * @see io.jdbd.meta.JdbdType#REF_CURSOR
 * @see Direction
 * @since 1.0
 */
public interface Cursor extends OptionSpec, Closeable {


    /**
     * @return cursor name
     */
    String name();

    /**
     * @return the {@link DatabaseSession} that create this {@link Cursor}.
     */
    DatabaseSession databaseSession();

    /*-------------------below fetch method-------------------*/

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // cursor is instance of RefCursor
     *             cursor.fetch(direction,function,states->{}) ; // ignore ResultStates instance.
     *         </code>
     *     </pre>
     *<br/>
     *
     * @see #fetch(Direction, Function, Consumer)
     */
    <T> Publisher<T> fetch(Direction direction, Function<CurrentRow, T> function);

    /**
     * <p>
     * Retrieve rows from a query using a cursor {@link #name()}.
     *<br/>
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     *<br/>
     *
     * @param direction must be one of following :
     *                  <ul>
     *                      <li>{@link Direction#NEXT}</li>
     *                      <li>{@link Direction#PRIOR}</li>
     *                      <li>{@link Direction#FIRST}</li>
     *                      <li>{@link Direction#LAST}</li>
     *                      <li>{@link Direction#FORWARD_ALL}</li>
     *                      <li>{@link Direction#BACKWARD_ALL}</li>
     *                  </ul>
     * @throws NullPointerException emit(not throw) when direction is null or function is null or consumer is null.
     * @throws JdbdException        emit(not throw) when
     *                              <ul>
     *                                  <li>driver don't support appropriate direction.</li>
     *                                  <li>direction error</li>
     *                                  <li>session close</li>
     *                                  <li>cursor have closed</li>
     *                                  <li>server response error,see {@link ServerException}</li>
     *                              </ul>
     */
    <T> Publisher<T> fetch(Direction direction, Function<CurrentRow, T> rowFunc, Consumer<ResultStates> statesConsumer);


    /**
     * <p>
     * Retrieve rows from a query using a cursor {@link #name()}.
     *<br/>
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     *<br/>
     *
     * @param direction must be one of following :
     *                  <ul>
     *                      <li>{@link Direction#NEXT}</li>
     *                      <li>{@link Direction#PRIOR}</li>
     *                      <li>{@link Direction#FIRST}</li>
     *                      <li>{@link Direction#LAST}</li>
     *                      <li>{@link Direction#FORWARD_ALL}</li>
     *                      <li>{@link Direction#BACKWARD_ALL}</li>
     *                  </ul>
     * @throws JdbdException emit(not throw) when
     *                       <ul>
     *                           <li>driver don't support appropriate direction.</li>
     *                           <li>session close</li>
     *                           <li>cursor have closed</li>
     *                           <li>server response error,see {@link ServerException}</li>
     *                       </ul>
     */
    OrderedFlux fetch(Direction direction);


    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // cursor is instance of RefCursor
     *             cursor.fetch(direction,count,function,states->{}) ; // ignore ResultStates instance.
     *         </code>
     *     </pre>
     *<br/>
     *
     * @see #fetch(Direction, long, Function, Consumer)
     */
    <T> Publisher<T> fetch(Direction direction, long count, Function<CurrentRow, T> function);

    /**
     * <p>
     * Retrieve rows from a query using a cursor {@link #name()}.
     *<br/>
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     *<br/>
     *
     * @param direction must be one of following :
     *                  <ul>
     *                      <li>{@link Direction#ABSOLUTE}</li>
     *                      <li>{@link Direction#RELATIVE}</li>
     *                      <li>{@link Direction#FORWARD}</li>
     *                      <li>{@link Direction#BACKWARD}</li>
     *                  </ul>
     * @param count     row count   <ul>
     *                  <li>
     *                  {@link Direction#ABSOLUTE}  :
     *                                   <ul>
     *                                       <li>positive : fetch the count'th row of the query. position after last row if count is out of range</li>
     *                                       <li>negative : fetch the abs(count)'th row from the end. position before first row if count is out of range</li>
     *                                       <li>0 positions before the first row,is out of range</li>
     *                                   </ul>
     *                               </li>
     *                               <li>
     *                                   {@link Direction#RELATIVE}  :
     *                                   <ul>
     *                                       <li>positive : fetch the count'th succeeding row</li>
     *                                       <li>negative : fetch the abs(count)'th prior row</li>
     *                                       <li>RELATIVE 0 re-fetches the current row, if any.</li>
     *                                   </ul>
     *                               </li>
     *                               <li>
     *                                   {@link Direction#FORWARD}  :
     *                                   <ul>
     *                                      <li>positive : fetch the next count rows.</li>
     *                                      <li>0 : re-fetches the current row</li>
     *                                      <li>negative :  is equivalent to {@link Direction#BACKWARD} abs(count)</li>
     *                                   </ul>
     *                               </li>
     *                               <li>
     *                                   {@link Direction#BACKWARD}  :
     *                                   <ul>
     *                                      <li>positive : Fetch the prior count rows (scanning backwards).</li>
     *                                      <li>0 : re-fetches the current row</li>
     *                                      <li>negative : is equivalent to {@link Direction#FORWARD} abs(count)</li>
     *                                   </ul>
     *                               </li>
     *                  </ul>
     * @throws NullPointerException     emit(not throw) when
     *                                  <ul>
     *                                      <li>direction is null</li>
     *                                      <li>function is null</li>
     *                                      <li>consumer is null</li>
     *                                  </ul>
     * @throws IllegalArgumentException emit(not throw) when
     *                                  <ul>
     *                                      <li>direction error</li>
     *                                  </ul>
     * @throws JdbdException            emit(not throw) when
     *                                  <ul>
     *                                      <li>driver don't support appropriate direction.</li>
     *                                      <li>session close</li>
     *                                      <li>cursor have closed</li>
     *                                      <li>server response error message,see {@link ServerException}</li>
     *                                  </ul>
     */
    <T> Publisher<T> fetch(Direction direction, long count, Function<CurrentRow, T> function, Consumer<ResultStates> consumer);


    /**
     * <p>
     * Retrieve rows from a query using a cursor {@link #name()}.
     *<br/>
     *
     * @param direction must be one of following :
     *                  <ul>
     *                      <li>{@link Direction#ABSOLUTE}</li>
     *                      <li>{@link Direction#RELATIVE}</li>
     *                      <li>{@link Direction#FORWARD}</li>
     *                      <li>{@link Direction#BACKWARD}</li>
     *                  </ul>
     * @param count     row count   <ul>
     *                  <li>
     *                  {@link Direction#ABSOLUTE}  :
     *                                   <ul>
     *                                       <li>positive : fetch the count'th row of the query. position after last row if count is out of range</li>
     *                                       <li>negative : fetch the abs(count)'th row from the end. position before first row if count is out of range</li>
     *                                       <li>0 positions before the first row,is out of range</li>
     *                                   </ul>
     *                               </li>
     *                               <li>
     *                                   {@link Direction#RELATIVE}  :
     *                                   <ul>
     *                                       <li>positive : fetch the count'th succeeding row</li>
     *                                       <li>negative : fetch the abs(count)'th prior row</li>
     *                                       <li>RELATIVE 0 re-fetches the current row, if any.</li>
     *                                   </ul>
     *                               </li>
     *                               <li>
     *                                   {@link Direction#FORWARD}  :
     *                                   <ul>
     *                                      <li>positive : fetch the next count rows.</li>
     *                                      <li>0 : re-fetches the current row</li>
     *                                      <li>negative : is equivalent to {@link Direction#BACKWARD} abs(count)</li>
     *                                   </ul>
     *                               </li>
     *                               <li>
     *                                   {@link Direction#BACKWARD}  :
     *                                   <ul>
     *                                      <li>positive : Fetch the prior count rows (scanning backwards).</li>
     *                                      <li>0 : re-fetches the current row</li>
     *                                      <li>negative : is equivalent to {@link Direction#FORWARD} abs(count)</li>
     *                                   </ul>
     *                               </li>
     *                  </ul>
     * @throws NullPointerException     emit(not throw) when direction is null
     * @throws IllegalArgumentException emit(not throw) when
     *                                  <ul>
     *                                      <li>direction error</li>
     *                                  </ul>
     * @throws JdbdException            emit(not throw) when
     *                                  <ul>
     *                                      <li>driver don't support appropriate direction.</li>
     *                                      <li>session close</li>
     *                                      <li>cursor have closed</li>
     *                                      <li>server response error message,see {@link ServerException}</li>
     *                                  </ul>
     */
    OrderedFlux fetch(Direction direction, long count);

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // cursor is instance of RefCursor
     *             cursor.forwardAllAndClosed(function,states->{}) ; // ignore ResultStates instance.
     *         </code>
     *     </pre>
     *<br/>
     */
    <T> Publisher<T> forwardAllAndClosed(Function<CurrentRow, T> function);

    /**
     * <p>
     * This method is equivalent to {@link #fetch(Direction FORWARD_ALL, Function, Consumer)} and {@link #close()}.
     *<br/>
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     *<br/>
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *     // cursor is instance of RefCursor
     *     Flux.from(cursor.fetch(FORWARD_ALL,function,consumer))
     *            .concatWith(Mono.defer(()-> Mono.from(this.close())))
     *            .onErrorResume(error -> closeOnError(cursor,error));
     *
     *    private &lt;T> Mono&lt;T> closeOnError(RefCursor cursor,Throwable error){
     *        return Mono.defer(()-> Mono.from(cursor.close()))
     *                .then(Mono.error(error));
     *    }
     *         </code>
     *     </pre>
     *<br/>
     */
    <T> Publisher<T> forwardAllAndClosed(Function<CurrentRow, T> function, Consumer<ResultStates> consumer);

    /**
     * <p>
     * This method is equivalent to {@link #fetch(Direction FORWARD_ALL)} and {@link #close()}.
     *<br/>
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     *<br/>
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *     // cursor is instance of RefCursor
     *     Flux.from(cursor.fetch(FORWARD_ALL))
     *            .concatWith(Mono.defer(()-> Mono.from(this.close())))
     *            .onErrorResume(error -> closeOnError(cursor,error));
     *
     *    private &lt;T> Mono&lt;T> closeOnError(RefCursor cursor,Throwable error){
     *        return Mono.defer(()-> Mono.from(cursor.close()))
     *                .then(Mono.error(error));
     *    }
     *         </code>
     *     </pre>
     *<br/>
     */
    OrderedFlux forwardAllAndClosed();

    /**
     * <p>
     * MOVE  a cursor without retrieving any data.
     *<br/>
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     *<br/>
     *
     * @param direction must be one of following :
     *                  <ul>
     *                      <li>{@link Direction#NEXT}</li>
     *                      <li>{@link Direction#PRIOR}</li>
     *                      <li>{@link Direction#FIRST}</li>
     *                      <li>{@link Direction#LAST}</li>
     *                      <li>{@link Direction#FORWARD_ALL}</li>
     *                      <li>{@link Direction#BACKWARD_ALL}</li>
     *                  </ul>
     * @return the {@link Publisher} that emit one {@link ResultStates} or {@link Throwable}.
     * @throws NullPointerException     emit(not throw) when direction is null
     * @throws IllegalArgumentException emit(not throw) when direction error
     * @throws JdbdException            emit(not throw) when
     *                                  <ul>
     *                                      <li>driver don't support this method.</li>
     *                                      <li>driver don't support appropriate direction.</li>
     *                                      <li>session close</li>
     *                                      <li>cursor have closed</li>
     *                                      <li>server response error message,see {@link ServerException}</li>
     *                                  </ul>
     */
    Publisher<ResultStates> move(Direction direction);

    /**
     * <p>
     * MOVE  a cursor without retrieving any data.
     *<br/>
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     *<br/>
     *
     * @param direction must be one of following :
     *                  <ul>
     *                      <li>{@link Direction#ABSOLUTE}</li>
     *                      <li>{@link Direction#RELATIVE}</li>
     *                      <li>{@link Direction#FORWARD}</li>
     *                      <li>{@link Direction#BACKWARD}</li>
     *                  </ul>
     * @param count     row count   <ul>
     *                  <li>
     *                  {@link Direction#ABSOLUTE}  :
     *                                   <ul>
     *                                       <li>positive : move to the count'th row. position after last row if count is out of range</li>
     *                                       <li>negative : move to the abs(count)'th row from the end. position before first row if count is out of range</li>
     *                                       <li>0 positions before the first row,is out of range</li>
     *                                   </ul>
     *                               </li>
     *                               <li>
     *                                   {@link Direction#RELATIVE}  :
     *                                   <ul>
     *                                       <li>positive : move to the count'th succeeding row</li>
     *                                       <li>negative : move to the abs(count)'th prior row</li>
     *                                       <li>RELATIVE 0 re-position the current row, if any.</li>
     *                                   </ul>
     *                               </li>
     *                               <li>
     *                                   {@link Direction#FORWARD}  :
     *                                   <ul>
     *                                      <li>positive : move to the next count rows.</li>
     *                                      <li>0 : re-position the current row</li>
     *                                      <li>negative :  is equivalent to {@link Direction#BACKWARD} abs(count)</li>
     *                                   </ul>
     *                               </li>
     *                               <li>
     *                                   {@link Direction#BACKWARD}  :
     *                                   <ul>
     *                                      <li>positive : move to the prior count rows (scanning backwards).</li>
     *                                      <li>0 : re-position the current row</li>
     *                                      <li>negative :  is equivalent to {@link Direction#FORWARD} abs(count)</li>
     *                                   </ul>
     *                               </li>
     *                  </ul>
     * @return the {@link Publisher} that emit just one {@link ResultStates} or {@link Throwable}.
     * @throws NullPointerException     emit(not throw) when direction is null
     * @throws IllegalArgumentException emit(not throw) when
     *                                  <ul>
     *                                      <li>direction error</li>
     *                                  </ul>
     * @throws JdbdException            emit(not throw) when
     *                                  <ul>
     *                                      <li>driver don't support this method.</li>
     *                                      <li>driver don't support appropriate direction.</li>
     *                                      <li>session close</li>
     *                                      <li>cursor have closed</li>
     *                                      <li>server response error message,see {@link ServerException}</li>
     *                                  </ul>
     */
    Publisher<ResultStates> move(Direction direction, long count);


    /**
     * <p>
     * close cursor. <strong>NOTE</strong> :
     * <ul>
     *     <li>If cursor have closed,emit nothing</li>
     *     <li>If cursor don't need to close (eg : postgre - current transaction is aborted, commands ignored until end of transaction ),emit nothing</li>
     * </ul>
     *<br/>
     * <p>
     *     <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     *<br/>
     *
     * @return the {@link Publisher} that emit nothing or emit {@link JdbdException}
     * @throws JdbdException emit(not throw) when only server response error,see {@link ServerException}.
     */
    @Override
    <T> Publisher<T> close();


    /**
     * override {@link Object#toString()}
     *
     * @return RefCursor info, contain : <ol>
     * <li>class name</li>
     * <li>{@link #name()}</li>
     * <li>column index if exists</li>
     * <li>column label if exists</li>
     * <li>{@link System#identityHashCode(Object)}</li>
     * </ol>
     */
    @Override
    String toString();


}
