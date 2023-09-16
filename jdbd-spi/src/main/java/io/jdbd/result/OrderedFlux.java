package io.jdbd.result;


import io.jdbd.statement.MultiResultStatement;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.List;


/**
 * <p>
 * The interface representing the {@link ResultItem} stream of the result of sql statement.
 * <br/>
 * <p>
 * <strong>NOTE</strong>: never {@link CurrentRow} in {@link OrderedFlux}.
 * <ul>
 *     <li>The update result always is represented by just one {@link ResultStates} instance.</li>
 *     <li>The query result is represented by following sequence :
 *         <ol>
 *             <li>one {@link ResultRowMeta} instance.</li>
 *             <li>0-N {@link ResultRow} instance.</li>
 *             <li>one {@link ResultStates} instance.</li>
 *         </ol>
 *         in {@link OrderedFlux}.
 *     </li>
 * </ul>
 * <br/>
 * <p>
 * The {@link OrderedFlux} emit :
 * <ul>
 *     <li>0-N update result.</li>
 *     <li>0-N query result.</li>
 * </ul>
 * The order of result is match with sql statement.
 * <pre><br/>
 *      example 1 : SELECT * FROM user AS u LIMIT 100 ; UPDATE user AS u SET u.nick_name = ? WHERE u.id = ?
 *       The {@link OrderedFlux} emit one query result and one update result.
 *      example 2 :  UPDATE user AS u SET u.nick_name = ? WHERE u.id = ?  ; SELECT * FROM user AS u LIMIT 100
 *       The {@link OrderedFlux} emit one update result and one query result.
 * </pre>
 * <br/>
 * <p>
 *     The 'Ordered' of {@link OrderedFlux} mean that the direct {@link Subscriber}
 *     (<strong>NOTE</strong> : is <strong>direct</strong> not indirect) can safely modify the field of
 *     {@link Subscriber} instance , because following methods :
 *     <ul>
 *         <li>{@link Subscriber#onNext(Object)}</li>
 *         <li>{@link Subscriber#onComplete()}</li>
 *         <li>{@link Subscriber#onNext(Object)}</li>
 *     </ul>
 *      of the direct (<strong>NOTE</strong> : is <strong>direct</strong> not indirect) {@link Subscriber} always run
 *      in an ordered / serial fashion. Typical above method run in {@code io.netty.channel.EventLoop}.
 * <br/>
 *
 * @see io.jdbd.statement.StaticStatementSpec#executeBatchAsFlux(List)
 * @see io.jdbd.statement.StaticStatementSpec#executeMultiStmt(String)
 * @see MultiResultStatement#executeBatchAsFlux()
 * @since 1.0
 */
public interface OrderedFlux extends Publisher<ResultItem> {


}
