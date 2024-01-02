/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.jdbd.vendor.stmt;


import io.jdbd.lang.Nullable;
import io.jdbd.meta.DataType;
import io.jdbd.session.ChunkOption;
import io.jdbd.session.DatabaseSession;
import io.jdbd.vendor.util.JdbdCollections;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public abstract class Stmts {

    protected Stmts() {
        throw new UnsupportedOperationException();
    }

    protected static final List<ParamValue> EMPTY_PARAM_GROUP = Collections.emptyList();


    public static StaticStmt stmt(final String sql) {
        return new JustSessionStaticUpdateStmt(sql, null);
    }

    public static StaticStmt stmtWithTimeout(final String sql, int timeout) {
        return new JustTimeoutStaticQueryStmt(sql, timeout);
    }


    public static StaticStmt stmtWithSession(final String sql, DatabaseSession session) {
        return new JustSessionStaticUpdateStmt(sql, session);
    }


    public static StaticBatchStmt batch(List<String> sqlGroup) {
        return new JustSessionStaticBatchStmt(sqlGroup, null);
    }

    public static StaticBatchStmt batchWithSession(List<String> sqlGroup, DatabaseSession session) {
        return new JustSessionStaticBatchStmt(sqlGroup, session);
    }

    public static StaticStmt stmt(String sql, StmtOption option) {
        return new SessionStaticUpdateStmt(sql, option);
    }


    public static StaticBatchStmt batch(List<String> sqlGroup, StmtOption option) {
        return new SessionStaticBatchStmt(sqlGroup, option);
    }


    public static ParamStmt single(String sql, DataType type, @Nullable Object value) {
        return single(sql, JdbdValues.paramValue(0, type, value));
    }

    public static ParamStmt single(String sql, ParamValue paramValue) {
        return new ParamUpdateStmt(sql, Collections.singletonList(paramValue));
    }

    public static ParamStmt paramStmt(final String sql, @Nullable List<ParamValue> paramGroup) {
        if (paramGroup == null) {
            paramGroup = EMPTY_PARAM_GROUP;
        } else {
            paramGroup = JdbdCollections.unmodifiableList(paramGroup);
        }
        return new ParamUpdateStmt(sql, paramGroup);
    }


    public static ParamStmt paramStmt(final String sql, @Nullable List<ParamValue> paramGroup, StmtOption option) {
        if (paramGroup == null) {
            paramGroup = EMPTY_PARAM_GROUP;
        } else {
            paramGroup = JdbdCollections.unmodifiableList(paramGroup);
        }
        return new SessionParamUpdateStmt(sql, paramGroup, option);
    }


    public static ParamBatchStmt paramBatch(String sql, List<List<ParamValue>> groupList) {
        return new MinParamBatchStmt(sql, groupList, null);
    }


    public static ParamBatchStmt paramBatch(String sql, List<List<ParamValue>> groupList, StmtOption option) {
        return new OptionParamBatchStmt(sql, groupList, option);
    }

    public static StaticMultiStmt multiStmt(String multiStmt) {
        return new JustStaticMultiStmt(multiStmt, null);
    }

    public static StaticMultiStmt multiStmtWithSession(String multiStmt, DatabaseSession session) {
        return new JustStaticMultiStmt(multiStmt, session);
    }

    public static StaticMultiStmt multiStmt(String multiStmt, StmtOption option) {
        return new SessionStaticMultiStmt(multiStmt, option);
    }


    public static ParamMultiStmt paramMultiStmt(List<ParamStmt> stmtList, StmtOption option) {
        return new OptionMultiStmt(stmtList, option);
    }


    private static abstract class StmtWithoutOption implements Stmt {

        @Override
        public final int getTimeout() {
            return 0;
        }

        @Override
        public final int getFetchSize() {
            return 0;
        }

        @Override
        public final int getFrequency() {
            return 0;
        }

        @Override
        public final List<NamedValue> getStmtVarList() {
            return Collections.emptyList();
        }

        @Override
        public final Function<ChunkOption, Publisher<byte[]>> getImportFunction() {
            return null;
        }

        @Override
        public final Function<ChunkOption, Subscriber<byte[]>> getExportFunction() {
            return null;
        }


    }//StmtWithoutOption

    private static abstract class NonSessionStmt extends StmtWithoutOption {

        @Override
        public final boolean isSessionCreated() {
            return false;
        }

        @Override
        public final DatabaseSession databaseSession() {
            throw new UnsupportedOperationException();
        }

    }// NonSessionStmt


    private static abstract class SingleStmtWithoutOption extends StmtWithoutOption implements SingleStmt {

        private final String sql;

        private SingleStmtWithoutOption(String sql) {
            this.sql = sql;
        }

        @Override
        public final String getSql() {
            return this.sql;
        }


    }// SingleStmtWithoutOption


    private static abstract class JustSessionSingleStmt extends SingleStmtWithoutOption {

        private final DatabaseSession session;

        private JustSessionSingleStmt(String sql, @Nullable DatabaseSession session) {
            super(sql);
            this.session = session;
        }

        @Override
        public final boolean isSessionCreated() {
            return this.session != null;
        }

        @Override
        public final DatabaseSession databaseSession() {
            final DatabaseSession session = this.session;
            if (session == null) {
                throw new UnsupportedOperationException();
            }
            return session;
        }

    } // NonSessionSingleStmt


    private static final class JustSessionStaticUpdateStmt extends JustSessionSingleStmt implements StaticStmt {


        private JustSessionStaticUpdateStmt(String sql, @Nullable DatabaseSession session) {
            super(sql, session);
        }


    }//JustSessionStaticUpdateStmt


    private static final class JustSessionStaticBatchStmt extends StmtWithoutOption implements StaticBatchStmt {

        private final List<String> sqlGroup;

        private final DatabaseSession session;


        private JustSessionStaticBatchStmt(final List<String> sqlGroup, @Nullable DatabaseSession session) {
            this.sqlGroup = JdbdCollections.asUnmodifiableList(sqlGroup); // must create new ArrayList
            this.session = session;
        }

        @Override
        public List<String> getSqlGroup() {
            return this.sqlGroup;
        }

        @Override
        public boolean isSessionCreated() {
            return this.session != null;
        }

        @Override
        public DatabaseSession databaseSession() {
            final DatabaseSession session = this.session;
            if (session == null) {
                throw new UnsupportedOperationException();
            }
            return session;
        }

    }//JustSessionStaticBatchStmt


    private static abstract class SessionStmt implements Stmt {

        private final int timeout;

        private final int fetchSize;

        private final int frequency;

        private final List<NamedValue> stmtVarList;

        private final Function<ChunkOption, Publisher<byte[]>> importFunc;

        private final Function<ChunkOption, Subscriber<byte[]>> exportFunc;

        private final DatabaseSession session;

        private SessionStmt(StmtOption option) {
            this.timeout = option.getTimeout();
            this.fetchSize = option.getFetchSize();
            this.frequency = option.getFrequency();
            this.stmtVarList = option.getStmtVarList();

            this.importFunc = option.getImportFunction();

            this.exportFunc = option.getExportFunction();
            this.session = option.databaseSession();
        }

        @Override
        public final int getTimeout() {
            return this.timeout;
        }

        @Override
        public final int getFetchSize() {
            return this.fetchSize;
        }

        @Override
        public final int getFrequency() {
            return this.frequency;
        }

        @Override
        public final List<NamedValue> getStmtVarList() {
            return this.stmtVarList;
        }

        @Override
        public final Function<ChunkOption, Publisher<byte[]>> getImportFunction() {
            return this.importFunc;
        }

        @Override
        public final Function<ChunkOption, Subscriber<byte[]>> getExportFunction() {
            return this.exportFunc;
        }

        @Override
        public final boolean isSessionCreated() {
            return true;
        }

        @Override
        public final DatabaseSession databaseSession() {
            return this.session;
        }

    }//SessionStmt


    private static abstract class SessionSingleStmt extends SessionStmt implements SingleStmt {

        private final String sql;

        private SessionSingleStmt(String sql, StmtOption option) {
            super(option);
            this.sql = sql;
        }

        @Override
        public final String getSql() {
            return this.sql;
        }


    }//SessionSingleStmt


    private static final class SessionStaticUpdateStmt extends SessionSingleStmt implements StaticStmt {

        private SessionStaticUpdateStmt(String sql, StmtOption option) {
            super(sql, option);
        }


    }//SessionStaticUpdateStmt


    protected static final class SessionStaticBatchStmt extends SessionStmt implements StaticBatchStmt {

        private final List<String> sqlGroup;


        private SessionStaticBatchStmt(List<String> sqlGroup, StmtOption option) {
            super(option);
            this.sqlGroup = JdbdCollections.unmodifiableList(sqlGroup);
        }

        @Override
        public List<String> getSqlGroup() {
            return this.sqlGroup;
        }

    }//SessionStaticBatchStmt


    private static abstract class NonSessionParamStmt extends NonSessionStmt implements ParamStmt {

        private final String sql;

        private final List<ParamValue> bindGroup;

        private NonSessionParamStmt(String sql, List<ParamValue> bindGroup) {
            this.sql = sql;
            this.bindGroup = JdbdCollections.unmodifiableList(bindGroup);
        }


        @Override
        public final List<ParamValue> getParamGroup() {
            return this.bindGroup;
        }

        @Override
        public final String getSql() {
            return this.sql;
        }

    }//NonSessionParamStmt


    private static final class ParamUpdateStmt extends NonSessionParamStmt {

        private ParamUpdateStmt(String sql, List<ParamValue> bindGroup) {
            super(sql, bindGroup);
        }


    }//ParamUpdateStmt


    private static abstract class SessionParamStmt extends SessionStmt implements ParamStmt {

        private final String sql;

        private final List<ParamValue> bindGroup;

        private SessionParamStmt(String sql, List<ParamValue> bindGroup, StmtOption option) {
            super(option);
            this.sql = sql;
            this.bindGroup = JdbdCollections.unmodifiableList(bindGroup);
        }


        @Override
        public final List<ParamValue> getParamGroup() {
            return this.bindGroup;
        }

        @Override
        public final String getSql() {
            return this.sql;
        }

    }//SessionParamStmt


    private static final class SessionParamUpdateStmt extends SessionParamStmt {

        private SessionParamUpdateStmt(String sql, List<ParamValue> bindGroup, StmtOption option) {
            super(sql, bindGroup, option);
        }


    }//SessionParamUpdateStmt


    private static final class QueryFetchParamStmt implements ParamStmt {

        private final String sql;

        private final List<ParamValue> paramList;
        private final int fetchSize;

        private QueryFetchParamStmt(String sql, List<ParamValue> paramList, int fetchSize) {
            this.sql = sql;
            this.paramList = JdbdCollections.unmodifiableList(paramList);
            this.fetchSize = fetchSize;
        }


        @Override
        public String getSql() {
            return this.sql;
        }

        @Override
        public List<ParamValue> getParamGroup() {
            return this.paramList;
        }

        @Override
        public int getFetchSize() {
            return this.fetchSize;
        }

        @Override
        public int getFrequency() {
            //TODO
            throw new UnsupportedOperationException();
        }

        @Override
        public int getTimeout() {
            return 0;
        }

        @Override
        public List<NamedValue> getStmtVarList() {
            return Collections.emptyList();
        }

        @Override
        public Function<ChunkOption, Publisher<byte[]>> getImportFunction() {
            return null;
        }

        @Override
        public Function<ChunkOption, Subscriber<byte[]>> getExportFunction() {
            return null;
        }


        @Override
        public boolean isSessionCreated() {
            //TODO correct ?
            return false;
        }

        @Override
        public DatabaseSession databaseSession() {
            //TODO correct ?
            throw new UnsupportedOperationException();
        }

    }//QueryFetchParamStmt


    private static final class MinParamBatchStmt extends StmtWithoutOption implements ParamBatchStmt {

        private final String sql;

        private final List<List<ParamValue>> groupList;

        private final DatabaseSession session;

        private MinParamBatchStmt(String sql, List<List<ParamValue>> groupList, @Nullable DatabaseSession session) {
            this.sql = sql;
            this.groupList = JdbdCollections.unmodifiableList(groupList);
            this.session = session;
        }

        @Override
        public String getSql() {
            return this.sql;
        }

        @Override
        public List<List<ParamValue>> getGroupList() {
            return this.groupList;
        }

        @Override
        public boolean isSessionCreated() {
            return this.session != null;
        }

        @Override
        public DatabaseSession databaseSession() {
            final DatabaseSession s = this.session;
            if (s == null) {
                throw new UnsupportedOperationException();
            }
            return s;
        }

    }//MinParamBatchStmt

    private static final class OptionParamBatchStmt extends SessionStmt implements ParamBatchStmt {

        private final String sql;

        private final List<List<ParamValue>> groupList;

        private OptionParamBatchStmt(String sql, List<List<ParamValue>> groupList, StmtOption option) {
            super(option);
            this.sql = sql;
            this.groupList = JdbdCollections.unmodifiableList(groupList);
        }

        @Override
        public String getSql() {
            return this.sql;
        }

        @Override
        public List<List<ParamValue>> getGroupList() {
            return this.groupList;
        }


    }//OptionParamBatchStmt


    private static final class JustStaticMultiStmt extends StmtWithoutOption implements StaticMultiStmt {

        private final String multiStmt;

        private final DatabaseSession session;

        private JustStaticMultiStmt(String multiStmt, @Nullable DatabaseSession session) {
            this.multiStmt = multiStmt;
            this.session = session;
        }

        @Override
        public String getMultiStmt() {
            return this.multiStmt;
        }

        @Override
        public boolean isSessionCreated() {
            return this.session != null;
        }

        @Override
        public DatabaseSession databaseSession() {
            final DatabaseSession s = this.session;
            if (s == null) {
                throw new UnsupportedOperationException();
            }
            return s;
        }


    }//MinStaticMultiStmt

    private static final class SessionStaticMultiStmt extends SessionStmt implements StaticMultiStmt {

        private final String multiStmt;

        private SessionStaticMultiStmt(String multiStmt, StmtOption option) {
            super(option);
            this.multiStmt = multiStmt;
        }

        @Override
        public String getMultiStmt() {
            return this.multiStmt;
        }


    }//OptionStaticMultiStmt


    private static final class OptionMultiStmt extends SessionStmt implements ParamMultiStmt {

        private final List<ParamStmt> stmtList;

        private OptionMultiStmt(List<ParamStmt> stmtList, StmtOption option) {
            super(option);
            this.stmtList = JdbdCollections.unmodifiableList(stmtList);
        }

        @Override
        public List<ParamStmt> getStmtList() {
            return this.stmtList;
        }

    }//OptionMultiStmt


    private static final class JustTimeoutStaticQueryStmt implements StaticStmt {

        private final String sql;

        private final int timeout;

        private JustTimeoutStaticQueryStmt(String sql, int timeout) {
            this.sql = sql;
            this.timeout = timeout;
        }

        @Override
        public String getSql() {
            return this.sql;
        }

        @Override
        public int getTimeout() {
            return this.timeout;
        }

        @Override
        public boolean isSessionCreated() {
            return false;
        }


        @Override
        public int getFetchSize() {
            return 0;
        }

        @Override
        public int getFrequency() {
            return 0;
        }

        @Override
        public List<NamedValue> getStmtVarList() {
            return Collections.emptyList();
        }

        @Override
        public Function<ChunkOption, Publisher<byte[]>> getImportFunction() {
            return null;
        }

        @Override
        public Function<ChunkOption, Subscriber<byte[]>> getExportFunction() {
            return null;
        }

        @Override
        public DatabaseSession databaseSession() {
            throw new UnsupportedOperationException();
        }


    }//JustTimeoutStaticQueryStmt


}
