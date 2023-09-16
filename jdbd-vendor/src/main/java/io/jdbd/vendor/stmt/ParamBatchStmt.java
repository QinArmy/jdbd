package io.jdbd.vendor.stmt;

import java.util.List;

/**
 * <p>
 * This interface representing stmt have only one sql that has parameter placeholder,and is batch.
 * <br/>
 */
public interface ParamBatchStmt extends ParamSingleStmt, BatchStmt {


    List<List<ParamValue>> getGroupList();


}
