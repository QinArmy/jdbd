package io.jdbd.util;

public interface SqlLogger {

    void logSql(String sessionName, int sessionHash, String sql);

}
