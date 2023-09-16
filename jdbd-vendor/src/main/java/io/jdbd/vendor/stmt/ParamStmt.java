package io.jdbd.vendor.stmt;


import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * This interface representing {@link Stmt} have only one sql that has parameter placeholder and isn't batch.
 * The implementation of this interface is used by the implementation of below methods:
 * <ul>
 * <li>{@link io.jdbd.statement.PreparedStatement#executeUpdate()}</li>
 * <li>{@link io.jdbd.statement.PreparedStatement#executeQuery()}</li>
 * <li>{@link io.jdbd.statement.PreparedStatement#executeQuery(Function, Consumer)}</li>
 * <li>{@link io.jdbd.statement.BindStatement#executeUpdate()}</li>
 * <li>{@link io.jdbd.statement.BindStatement#executeQuery()}</li>
 * <li>{@link io.jdbd.statement.BindStatement#executeQuery(Function, Consumer)}</li>
 * </ul>
 * <br/>
 */
public interface ParamStmt extends ParamSingleStmt, SingleStmt {

    /**
     * Get parameter group
     *
     * @return a unmodifiable list
     */
    List<ParamValue> getParamGroup();


}
