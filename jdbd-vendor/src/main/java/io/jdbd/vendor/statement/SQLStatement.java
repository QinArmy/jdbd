package io.jdbd.vendor.statement;

import java.util.List;

public interface SQLStatement {

    List<String> getStaticSql();

    int getParamCount();

    String getSql();

}