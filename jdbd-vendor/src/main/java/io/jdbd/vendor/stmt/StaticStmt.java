package io.jdbd.vendor.stmt;

/**
 * <p>
 * This interface representing stmt have only one sql,and no parameter placeholder.
 * This implementation of this interface is used by the implementation of below methods:
 * <u>
 * <li>{@link io.jdbd.statement.StaticStatement#executeUpdate(String)}</li>
 * <li>{@link io.jdbd.statement.StaticStatement#executeQuery(String)}</li>
 * </u>
 * </p>
 */
public interface StaticStmt extends SingleStmt {


}
