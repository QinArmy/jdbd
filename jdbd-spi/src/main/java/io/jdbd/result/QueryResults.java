package io.jdbd.result;


import io.jdbd.statement.MultiResultStatement;
import io.jdbd.statement.StaticStatementSpec;

import java.util.List;

/**
 * <p>
 * <strong>NOTE</strong> : driver don't send message to database server before first subscribing.
 * <br/>
 * <p>This interface instance is crated by following methods:
 * <ul>
 *     <li>{@link MultiResultStatement#executeBatchQuery()}</li>
 *     <li>{@link StaticStatementSpec#executeBatchQuery(List)}</li>
 * </ul>
 *
 * @since 1.0
 */
public interface QueryResults extends MultiResultSpec {


}
