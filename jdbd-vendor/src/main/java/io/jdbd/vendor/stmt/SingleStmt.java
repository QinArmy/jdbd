package io.jdbd.vendor.stmt;


/**
 * <p>
 * This interface representing this {@link Stmt} only one sql.
 * <br/>
 * <p>
 * This is a base interface of :
 *     <ul>
 *         <li>{@link StaticStmt}</li>
 *         <li>{@link ParamStmt}</li>
 *         <li>{@link ParamBatchStmt}</li>
 *     </ul>
 * <br/>
 */
public interface SingleStmt extends Stmt {

    String getSql();


}
