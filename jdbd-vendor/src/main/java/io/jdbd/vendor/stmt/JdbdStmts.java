package io.jdbd.vendor.stmt;


import io.jdbd.result.MultiResult;
import io.jdbd.result.ResultState;
import io.jdbd.vendor.util.JdbdCollections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class JdbdStmts {

    protected JdbdStmts() {
        throw new UnsupportedOperationException();
    }


    public static BatchParamStmt<ParamValue> batch(String sql, List<List<ParamValue>> groupList) {
        if (groupList.size() < 1) {
            throw new IllegalArgumentException("groupList is empty.");
        }
        return new BatchStmtImpl(sql, groupList, 0);
    }

    public static BatchParamStmt<ParamValue> batch(String sql, List<List<ParamValue>> groupList, int timeOut) {
        if (groupList.size() < 2) {
            throw new IllegalArgumentException("groupList size < 2");
        }
        return new BatchStmtImpl(sql, groupList, timeOut);
    }

    public static Stmt stmt(String sql) {
        Objects.requireNonNull(sql, "sql");
        return new StmtImpl1(sql);
    }

    public static Stmt stmt(String sql, int timeout) {
        Objects.requireNonNull(sql, "sql");
        return timeout > 0 ? new StmtImpl2(sql, timeout) : new StmtImpl1(sql);
    }

    public static Stmt stmt(String sql, Consumer<ResultState> statusConsumer) {
        Objects.requireNonNull(sql, "sql");
        return new StmtImpl2C(sql, statusConsumer);
    }

    public static Stmt stmt(String sql, Consumer<ResultState> statusConsumer, int timeout) {
        Objects.requireNonNull(sql, "sql");
        Objects.requireNonNull(statusConsumer, "statusConsumer");
        return timeout > 0
                ? new StmtImpl3(sql, timeout, statusConsumer)
                : new StmtImpl2C(sql, statusConsumer);
    }

    public static List<Stmt> stmts(List<String> sqlList) {
        return stmts(sqlList, 0);
    }

    public static List<Stmt> stmts(List<String> sqlList, int timeout) {
        Objects.requireNonNull(sqlList, "sqlList");
        if (sqlList.isEmpty()) {
            throw new IllegalArgumentException("sqlList is empty.");
        }
        final List<Stmt> list;
        final int size = sqlList.size();
        if (size == 1) {
            list = Collections.singletonList(stmt(sqlList.get(0), timeout));
        } else {
            List<Stmt> tempList = new ArrayList<>(size);
            for (String sql : sqlList) {
                tempList.add(stmt(sql, timeout));
            }
            list = Collections.unmodifiableList(tempList);
        }
        return list;
    }

    public static ParamStmt singlePrepare(String sql, ParamValue paramValue) {
        return new SimpleParamStmt(sql, paramValue);
    }

    public static ParamStmt multiPrepare(String sql, List<? extends ParamValue> paramGroup) {
        return new SimpleParamStmt(sql, paramGroup);
    }


    private static final class BatchStmtImpl implements BatchParamStmt<ParamValue> {

        private final String sql;

        private final List<List<ParamValue>> groupList;

        private final int timeOut;

        private BatchStmtImpl(String sql, List<List<ParamValue>> groupList, int timeOut) {
            this.sql = sql;
            this.groupList = JdbdCollections.unmodifiableList(groupList);
            this.timeOut = timeOut;
        }

        @Override
        public final String getSql() {
            return this.sql;
        }

        @Override
        public final List<List<ParamValue>> getGroupList() {
            return this.groupList;
        }

        @Override
        public final int getTimeout() {
            return this.timeOut;
        }

        @Override
        public final Consumer<ResultState> getStatusConsumer() {
            return MultiResult.EMPTY_CONSUMER;
        }
    }


    private static final class SimpleParamStmt implements ParamStmt {

        private final String sql;

        private final List<? extends ParamValue> paramGroup;

        private SimpleParamStmt(String sql, ParamValue paramValue) {
            this.sql = sql;
            this.paramGroup = Collections.singletonList(paramValue);
        }

        private SimpleParamStmt(String sql, List<? extends ParamValue> paramGroup) {
            this.sql = sql;
            this.paramGroup = Collections.unmodifiableList(paramGroup);
        }

        @Override
        public String getSql() {
            return this.sql;
        }

        @Override
        public List<? extends ParamValue> getParamGroup() {
            return this.paramGroup;
        }

        @Override
        public Consumer<ResultState> getStatusConsumer() {
            return MultiResult.EMPTY_CONSUMER;
        }

        @Override
        public int getFetchSize() {
            return 0;
        }

        @Override
        public int getTimeout() {
            return 0;
        }
    }


    private static class StmtImpl2 implements Stmt {

        private final String sql;

        private final int timeout;

        private StmtImpl2(String sql, int timeout) {
            this.sql = sql;
            this.timeout = timeout;
        }

        @Override
        public final String getSql() {
            return this.sql;
        }

        @Override
        public final int getTimeout() {
            return this.timeout;
        }

        @Override
        public final Consumer<ResultState> getStatusConsumer() {
            return MultiResult.EMPTY_CONSUMER;
        }

    }

    private static class StmtImpl2C implements Stmt {

        private final String sql;

        private final Consumer<ResultState> consumer;

        private StmtImpl2C(String sql, Consumer<ResultState> consumer) {
            this.sql = sql;
            this.consumer = consumer;
        }

        @Override
        public final String getSql() {
            return this.sql;
        }

        @Override
        public final int getTimeout() {
            return 0;
        }

        @Override
        public final Consumer<ResultState> getStatusConsumer() {
            return this.consumer;
        }

    }


    private static class StmtImpl1 implements Stmt {

        private final String sql;

        private StmtImpl1(String sql) {
            this.sql = sql;
        }

        @Override
        public final String getSql() {
            return this.sql;
        }

        @Override
        public final int getTimeout() {
            return 0;
        }

        @Override
        public final Consumer<ResultState> getStatusConsumer() {
            return MultiResult.EMPTY_CONSUMER;
        }

    }


    private static class StmtImpl3 implements Stmt {

        private final String sql;

        private final int timeout;

        private final Consumer<ResultState> consumer;

        private StmtImpl3(String sql, int timeout, Consumer<ResultState> consumer) {
            this.sql = sql;
            this.timeout = timeout;
            this.consumer = consumer;
        }

        @Override
        public final String getSql() {
            return this.sql;
        }

        @Override
        public final int getTimeout() {
            return this.timeout;
        }

        @Override
        public final Consumer<ResultState> getStatusConsumer() {
            return this.consumer;
        }


    }


}
