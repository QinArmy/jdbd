package io.jdbd.vendor.stmt;

import java.util.List;

/**
 * <p>
 * This interface representing multi {@link ParamStmt}.
 * This used by {@link io.jdbd.statement.MultiStatement} for wrap sql and params.
 * <br/>
 */
public interface ParamMultiStmt extends Stmt {

    List<ParamStmt> getStmtList();



}
