package io.jdbd.vendor.stmt;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * This interface representing stmt have only multi sql,and no parameter placeholder.
 * This implementation of this interface is used by the implementation of below methods:
 * <ul>
 * <li>{@link io.jdbd.statement.StaticStatement#executeUpdate(String)}</li>
 * <li>{@link io.jdbd.statement.StaticStatement#executeQuery(String, Function)}</li>
 * <li>{@link io.jdbd.statement.StaticStatement#executeQuery(String, Function, Consumer)}</li>
 * </ul>
 * <br/>
 */
public interface StaticBatchStmt extends BatchStmt {

    /**
     * @return a unmodified list
     */
    List<String> getSqlGroup();


}
