package io.jdbd.mysql.stmt;

import io.jdbd.vendor.stmt.ParamBatchStmt;

import java.util.List;

@Deprecated
public interface BindBatchStmt extends ParamBatchStmt {

    @Override
    List<List<BindValue>> getGroupList();

}
