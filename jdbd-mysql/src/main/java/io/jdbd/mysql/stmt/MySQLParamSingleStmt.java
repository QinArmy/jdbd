package io.jdbd.mysql.stmt;

import io.jdbd.vendor.stmt.ParamSingleStmt;

import java.util.Map;

/**
 * <p>
 * This interface extends {@link ParamSingleStmt},representing this stmt has MySQL query attributes.
 * </p>
 */
public interface MySQLParamSingleStmt extends ParamSingleStmt {

    Map<String, QueryAttr> getQueryAttrs();

}
