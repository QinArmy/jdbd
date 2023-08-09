package io.jdbd.result;

import io.jdbd.JdbdException;
import io.jdbd.session.Closeable;
import io.jdbd.session.OptionSpec;
import org.reactivestreams.Publisher;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * This interface representing reference of server sql cursor.
 * </p>
 * <p>
 * The cursor will be close in following scenarios :
 *     <ul>
 *         <li>the any method of {@link RefCursor} emit {@link Throwable}</li>
 *         <li>You invoke {@link #forwardAllAndClosed(Function)}</li>
 *         <li>You invoke {@link #forwardAllAndClosed(Function, Consumer)}</li>
 *         <li>You invoke {@link #forwardAllAndClosed()}</li>
 *         <li>You invoke {@link #close()}</li>
 *     </ul>
 * If the methods of {@link RefCursor} don't emit any {@link Throwable},then you should close cursor.
 * If you don't close cursor ,the {@link io.jdbd.session.DatabaseSession} that create this {@link RefCursor} can still execute new {@link io.jdbd.statement.Statement},
 * but you shouldn't do this.
 * </p>
 *
 * @see io.jdbd.meta.JdbdType#REF_CURSOR
 * @see CursorDirection
 * @since 1.0
 */
public interface RefCursor extends OptionSpec, Closeable {

    /**
     * @return cursor name
     */
    String name();

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
     * </p>
     *
     * @see #fetch(CursorDirection, Function, Consumer)
     */
    <T> Publisher<T> fetch(CursorDirection direction, Function<CurrentRow, T> function);

    /**
     * <p>
     * Retrieve rows from a query using a cursor {@link #name()}.
     * </p>
     *
     * @param direction must be one of following :
     *                  <ul>
     *                      <li>{@link CursorDirection#NEXT}</li>
     *                      <li>{@link CursorDirection#PRIOR}</li>
     *                      <li>{@link CursorDirection#FIRST}</li>
     *                      <li>{@link CursorDirection#LAST}</li>
     *                      <li>{@link CursorDirection#FORWARD_ALL}</li>
     *                      <li>{@link CursorDirection#BACKWARD_ALL}</li>
     *                  </ul>
     * @throws NullPointerException emit(not throw) when function is null or consumer is null.
     * @throws JdbdException        emit(not throw) when
     *                              <ul>
     *                                  <li>driver don't support appropriate direction.</li>
     *                                  <li>session close</li>
     *                                  <li>cursor have closed</li>
     *                                  <li>server response error,see {@link ServerException}</li>
     *                              </ul>
     */
    <T> Publisher<T> fetch(CursorDirection direction, Function<CurrentRow, T> function, Consumer<ResultStates> consumer);

    /**
     * <p>
     * Retrieve rows from a query using a cursor {@link #name()}.
     * </p>
     *
     * @param direction must be one of following :
     *                  <ul>
     *                      <li>{@link CursorDirection#NEXT}</li>
     *                      <li>{@link CursorDirection#PRIOR}</li>
     *                      <li>{@link CursorDirection#FIRST}</li>
     *                      <li>{@link CursorDirection#LAST}</li>
     *                      <li>{@link CursorDirection#FORWARD_ALL}</li>
     *                      <li>{@link CursorDirection#BACKWARD_ALL}</li>
     *                  </ul>
     * @throws JdbdException emit(not throw) when
     *                       <ul>
     *                           <li>driver don't support appropriate direction.</li>
     *                           <li>session close</li>
     *                           <li>cursor have closed</li>
     *                           <li>server response error,see {@link ServerException}</li>
     *                       </ul>
     */
    OrderedFlux fetch(CursorDirection direction);


    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // cursor is instance of RefCursor
     *             cursor.fetch(direction,count,function,states->{}) ; // ignore ResultStates instance.
     *         </code>
     *     </pre>
     * </p>
     *
     * @see #fetch(CursorDirection, long, Function, Consumer)
     */
    <T> Publisher<T> fetch(CursorDirection direction, long count, Function<CurrentRow, T> function);

    /**
     * <p>
     * Retrieve rows from a query using a cursor {@link #name()}.
     * </p>
     *
     * @param direction must be one of following :
     *                  <ul>
     *                      <li>{@link CursorDirection#ABSOLUTE}</li>
     *                      <li>{@link CursorDirection#RELATIVE}</li>
     *                      <li>{@link CursorDirection#FORWARD}</li>
     *                      <li>{@link CursorDirection#BACKWARD}</li>
     *                  </ul>
     * @param count     row count   <ul>
     *                  <li>
     *                  {@link CursorDirection#ABSOLUTE}  :
     *                                   <ul>
     *                                       <li>positive : fetch the count'th row of the query. position after last row if count is out of range</li>
     *                                       <li>negative : fetch the abs(count)'th row from the end. position before first row if count is out of range</li>
     *                                       <li>0 positions before the first row,is out of range,throw error</li>
     *                                   </ul>
     *                               </li>
     *                               <li>
     *                                   {@link CursorDirection#RELATIVE}  :
     *                                   <ul>
     *                                       <li>positive : fetch the count'th succeeding row</li>
     *                                       <li>negative : fetch the abs(count)'th prior row</li>
     *                                       <li>RELATIVE 0 re-fetches the current row, if any.</li>
     *                                   </ul>
     *                               </li>
     *                               <li>
     *                                   {@link CursorDirection#FORWARD}  :
     *                                   <ul>
     *                                      <li>positive : fetch the next count rows.</li>
     *                                      <li>0 : re-fetches the current row</li>
     *                                      <li>negative : error</li>
     *                                   </ul>
     *                               </li>
     *                               <li>
     *                                   {@link CursorDirection#BACKWARD}  :
     *                                   <ul>
     *                                      <li>positive : Fetch the prior count rows (scanning backwards).</li>
     *                                      <li>0 : re-fetches the current row</li>
     *                                      <li>negative : error</li>
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
     *                                      <li>count error</li>
     *                                  </ul>
     * @throws JdbdException            emit(not throw) when
     *                                  <ul>
     *                                      <li>driver don't support appropriate direction.</li>
     *                                      <li>session close</li>
     *                                      <li>cursor have closed</li>
     *                                      <li>server response error message,see {@link ServerException}</li>
     *                                  </ul>
     */
    <T> Publisher<T> fetch(CursorDirection direction, long count, Function<CurrentRow, T> function, Consumer<ResultStates> consumer);

    /**
     * <p>
     * Retrieve rows from a query using a cursor {@link #name()}.
     * </p>
     *
     * @param direction must be one of following :
     *                  <ul>
     *                      <li>{@link CursorDirection#ABSOLUTE}</li>
     *                      <li>{@link CursorDirection#RELATIVE}</li>
     *                      <li>{@link CursorDirection#FORWARD}</li>
     *                      <li>{@link CursorDirection#BACKWARD}</li>
     *                  </ul>
     * @param count     row count   <ul>
     *                  <li>
     *                  {@link CursorDirection#ABSOLUTE}  :
     *                                   <ul>
     *                                       <li>positive : fetch the count'th row of the query. position after last row if count is out of range</li>
     *                                       <li>negative : fetch the abs(count)'th row from the end. position before first row if count is out of range</li>
     *                                       <li>0 positions before the first row,is out of range,throw error</li>
     *                                   </ul>
     *                               </li>
     *                               <li>
     *                                   {@link CursorDirection#RELATIVE}  :
     *                                   <ul>
     *                                       <li>positive : fetch the count'th succeeding row</li>
     *                                       <li>negative : fetch the abs(count)'th prior row</li>
     *                                       <li>RELATIVE 0 re-fetches the current row, if any.</li>
     *                                   </ul>
     *                               </li>
     *                               <li>
     *                                   {@link CursorDirection#FORWARD}  :
     *                                   <ul>
     *                                      <li>positive : fetch the next count rows.</li>
     *                                      <li>0 : re-fetches the current row</li>
     *                                      <li>negative : error</li>
     *                                   </ul>
     *                               </li>
     *                               <li>
     *                                   {@link CursorDirection#BACKWARD}  :
     *                                   <ul>
     *                                      <li>positive : Fetch the prior count rows (scanning backwards).</li>
     *                                      <li>0 : re-fetches the current row</li>
     *                                      <li>negative : error</li>
     *                                   </ul>
     *                               </li>
     *                  </ul>
     * @throws NullPointerException     emit(not throw) when direction is null
     * @throws IllegalArgumentException emit(not throw) when
     *                                  <ul>
     *                                      <li>direction error</li>
     *                                      <li>count error</li>
     *                                  </ul>
     * @throws JdbdException            emit(not throw) when
     *                                  <ul>
     *                                      <li>driver don't support appropriate direction.</li>
     *                                      <li>session close</li>
     *                                      <li>cursor have closed</li>
     *                                      <li>server response error message,see {@link ServerException}</li>
     *                                  </ul>
     */
    OrderedFlux fetch(CursorDirection direction, long count);

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // cursor is instance of RefCursor
     *             cursor.forwardAllAndClosed(function,states->{}) ; // ignore ResultStates instance.
     *         </code>
     *     </pre>
     * </p>
     */
    <T> Publisher<T> forwardAllAndClosed(Function<CurrentRow, T> function);

    /**
     * <p>
     * This method is equivalent to {@link #fetch(CursorDirection FORWARD_ALL, Function, Consumer)} and {@link #close()}.
     * </p>
     * <p>
     * <pre>
     *         <code><br/>
     *     // cursor is instance of RefCursor
     *     Flux.from(cursor.fetch(FORWARD_ALL,function,consumer))
     *            .onErrorResume(error -> closeOnError(cursor,error))
     *            .concatWith(Mono.defer(()-> Mono.from(this.close())));
     *
     *    private &lt;T> Mono&lt;T> closeOnError(RefCursor cursor,Throwable error){
     *        return Mono.defer(()-> Mono.from(cursor.close()))
     *                .then(Mono.error(error));
     *    }
     *         </code>
     *     </pre>
     * </p>
     */
    <T> Publisher<T> forwardAllAndClosed(Function<CurrentRow, T> function, Consumer<ResultStates> consumer);

    /**
     * <p>
     * This method is equivalent to {@link #fetch(CursorDirection FORWARD_ALL, Function, Consumer)} and {@link #close()}.
     * </p>
     * <p>
     * <pre>
     *         <code><br/>
     *     // cursor is instance of RefCursor
     *     Flux.from(cursor.fetch(FORWARD_ALL))
     *            .onErrorResume(error -> closeOnError(cursor,error))
     *            .concatWith(Mono.defer(()-> Mono.from(this.close())));
     *
     *    private &lt;T> Mono&lt;T> closeOnError(RefCursor cursor,Throwable error){
     *        return Mono.defer(()-> Mono.from(cursor.close()))
     *                .then(Mono.error(error));
     *    }
     *         </code>
     *     </pre>
     * </p>
     */
    OrderedFlux forwardAllAndClosed();

    /**
     * <p>
     * MOVE  a cursor without retrieving any data.
     * </p>
     *
     * @param direction must be one of following :
     *                  <ul>
     *                      <li>{@link CursorDirection#NEXT}</li>
     *                      <li>{@link CursorDirection#PRIOR}</li>
     *                      <li>{@link CursorDirection#FIRST}</li>
     *                      <li>{@link CursorDirection#LAST}</li>
     *                      <li>{@link CursorDirection#FORWARD_ALL}</li>
     *                      <li>{@link CursorDirection#BACKWARD_ALL}</li>
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
    Publisher<ResultStates> move(CursorDirection direction);

    /**
     * <p>
     * MOVE  a cursor without retrieving any data.
     * </p>
     *
     * @param direction must be one of following :
     *                  <ul>
     *                      <li>{@link CursorDirection#ABSOLUTE}</li>
     *                      <li>{@link CursorDirection#RELATIVE}</li>
     *                      <li>{@link CursorDirection#FORWARD}</li>
     *                      <li>{@link CursorDirection#BACKWARD}</li>
     *                  </ul>
     * @param count     row count   <ul>
     *                  <li>
     *                  {@link CursorDirection#ABSOLUTE}  :
     *                                   <ul>
     *                                       <li>positive : move to the count'th row. position after last row if count is out of range</li>
     *                                       <li>negative : move to the abs(count)'th row from the end. position before first row if count is out of range</li>
     *                                       <li>0 positions before the first row,is out of range,throw error</li>
     *                                   </ul>
     *                               </li>
     *                               <li>
     *                                   {@link CursorDirection#RELATIVE}  :
     *                                   <ul>
     *                                       <li>positive : move to the count'th succeeding row</li>
     *                                       <li>negative : move to the abs(count)'th prior row</li>
     *                                       <li>RELATIVE 0 re-position the current row, if any.</li>
     *                                   </ul>
     *                               </li>
     *                               <li>
     *                                   {@link CursorDirection#FORWARD}  :
     *                                   <ul>
     *                                      <li>positive : move to the next count rows.</li>
     *                                      <li>0 : re-position the current row</li>
     *                                      <li>negative : error</li>
     *                                   </ul>
     *                               </li>
     *                               <li>
     *                                   {@link CursorDirection#BACKWARD}  :
     *                                   <ul>
     *                                      <li>positive : move to the prior count rows (scanning backwards).</li>
     *                                      <li>0 : re-position the current row</li>
     *                                      <li>negative : error</li>
     *                                   </ul>
     *                               </li>
     *                  </ul>
     * @return the {@link Publisher} that emit just one {@link ResultStates} or {@link Throwable}.
     * @throws NullPointerException     emit(not throw) when direction is null
     * @throws IllegalArgumentException emit(not throw) when
     *                                  <ul>
     *                                      <li>direction error</li>
     *                                      <li>count error</li>
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
    Publisher<ResultStates> move(CursorDirection direction, int count);


    /**
     * <p>
     * close cursor. <strong>NOTE</strong> :
     * <ul>
     *     <li>If cursor have closed,emit nothing</li>
     *     <li>If cursor don't need to close (eg : postgre - current transaction is aborted, commands ignored until end of transaction ),emit nothing</li>
     * </ul>
     * </p>
     *
     * @return the {@link Publisher} that emit nothing or emit {@link JdbdException}
     * @throws JdbdException emit(not throw) when
     *                       <ul>
     *                           <li>session have closed</li>
     *                           <li>server response error,see {@link ServerException}</li>
     *                       </ul>
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
