package io.jdbd.vendor.util;

import io.jdbd.JdbdException;
import io.jdbd.lang.Nullable;
import io.jdbd.meta.DataType;
import io.jdbd.meta.SQLType;
import io.jdbd.meta.SchemaMeta;
import io.jdbd.meta.TableMeta;
import io.jdbd.result.CurrentRow;
import io.jdbd.result.CursorDirection;
import io.jdbd.result.ResultStates;
import io.jdbd.result.ResultStatusConsumerException;
import io.jdbd.session.*;
import io.jdbd.statement.OutParameter;
import io.jdbd.statement.PreparedStatement;
import io.jdbd.statement.Statement;
import io.jdbd.statement.TimeoutException;
import io.jdbd.type.PathParameter;
import io.jdbd.type.TextPath;
import io.jdbd.vendor.JdbdCompositeException;
import io.jdbd.vendor.result.ColumnMeta;
import io.jdbd.vendor.stmt.NamedValue;
import io.jdbd.vendor.stmt.ParamValue;
import io.jdbd.vendor.stmt.Stmt;
import io.jdbd.vendor.stmt.Value;
import io.jdbd.vendor.task.TimeoutTask;
import org.slf4j.Logger;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class JdbdExceptions {

    protected JdbdExceptions() {
        throw new UnsupportedOperationException();
    }


    public static JdbdException wrap(Throwable cause) {
        JdbdException e;
        if (cause instanceof JdbdException) {
            e = (JdbdException) cause;
        } else {
            e = new JdbdException(String.format("Unknown error,%s", cause.getMessage()), cause);
        }
        return e;
    }

    /**
     * @return {@link NullPointerException} not {@link JdbdException}
     */
    public static NullPointerException fluxFuncIsNull() {
        return new NullPointerException("fluxFunc must be non-null");
    }

    /**
     * @return {@link NullPointerException} not {@link JdbdException}
     */
    public static NullPointerException fluxFuncReturnNull(Function<?, ?> fluxFunc) {
        return new NullPointerException(String.format("%s must return non-null", fluxFunc));
    }

    /**
     * @return {@link NullPointerException} not {@link JdbdException}
     */
    public static NullPointerException monoFuncIsNull() {
        return new NullPointerException("monoFunc must be non-null");
    }

    /**
     * @return {@link NullPointerException} not {@link JdbdException}
     */
    public static NullPointerException monoFuncReturnNull(Function<?, ?> monoFunc) {
        return new NullPointerException(String.format("%s must return non-null", monoFunc));
    }


    public static JdbdException unexpectedEnum(Enum<?> e) {
        return new JdbdException(String.format("unexpected enum %s", e));
    }

    public static JdbdException valueErrorOfKey(String key) {
        String m = String.format("value of key[%s] error.", key);
        return new JdbdException(m);
    }

    public static JdbdException queryMapFuncError(Function<CurrentRow, ?> function) {
        String m = String.format("query map function %s couldn't return %s ",
                function, CurrentRow.class.getName());
        return new JdbdException(m);
    }

    public static Throwable queryMapFuncInvokeError(Function<CurrentRow, ?> function, Throwable cause) {
        if (isJvmFatal(cause)) {
            return cause;
        }
        String m = String.format("query map function %s throw error ", function);
        return new JdbdException(m, cause);
    }

    public static Throwable resultStatusConsumerInvokingError(Consumer<ResultStates> consumer, Throwable cause) {
        if (isJvmFatal(cause)) {
            return cause;
        }
        return ResultStatusConsumerException.create(consumer, cause);
    }

    /**
     * @return {@link NullPointerException} not {@link JdbdException}
     */
    public static NullPointerException queryMapFuncIsNull() {
        return new NullPointerException("query map function must non-null");
    }

    /**
     * @return {@link NullPointerException} not {@link JdbdException}
     */
    public static NullPointerException cursorDirectionIsNull() {
        return new NullPointerException("CursorDirection must non-null");
    }

    /**
     * @return {@link NullPointerException} not {@link JdbdException}
     */
    public static NullPointerException statesConsumerIsNull() {
        return new NullPointerException(String.format("%s consumer must non-null", ResultStates.class.getName()));
    }

    /**
     * @return {@link NullPointerException} not {@link JdbdException}
     */
    public static NullPointerException dataTypeIsNull() {
        return new NullPointerException("dataType must non-null");
    }


    public static JdbdException unexpectedCursorDirection(CursorDirection direction) {
        return new JdbdException(String.format("unexpected %s", direction));
    }

    public static JdbdException unknownSchemaMeta(SchemaMeta meta) {
        String m = String.format("unknown %s %s", SchemaMeta.class.getName(), meta);
        return new JdbdException(m);
    }

    public static JdbdException unknownTableMeta(TableMeta meta) {
        String m = String.format("unknown %s %s", TableMeta.class.getName(), meta);
        return new JdbdException(m);
    }

    public static JdbdException identifierNoText() {
        return new JdbdException("sql identifier must have text.");
    }

    public static JdbdException dontSupportDataType(DataType dataType, String database) {
        return new JdbdException(String.format("%s don't support %s[%s]", database, DataType.class.getName(), dataType));
    }

    public static SessionCloseException sessionHaveClosed() {
        return new SessionCloseException("session have closed");
    }

    public static SessionCloseException unexpectedSessionClose() {
        return new SessionCloseException("unexpected session close");
    }


    public static TimeoutException statementTimeout(TimeoutTask task, int timeoutMills, @Nullable Throwable cause) {
        String m;
        m = String.format("timeout %s mills,but rest %s mills", timeoutMills,
                timeoutMills - (task.runTimeMills() - task.startTimeMills()));

        final TimeoutException error;
        if (cause == null) {
            error = new TimeoutException(m);
        } else {
            error = new TimeoutException(m, cause);
        }
        return error;
    }

    public static RuntimeException stmtVarNameHaveNoText(@Nullable String name) {
        final RuntimeException error;
        if (name == null) {
            error = new NullPointerException("statement variable name must non-null.");
        } else {
            error = new IllegalArgumentException("statement variable name must have text.");
        }
        return error;
    }

    public static JdbdException errorTypeName(DataType dataType) {
        String m = String.format("%s typeName[%s] error", DataType.class.getName(), dataType.typeName());
        return new JdbdException(m);
    }


    public static JdbdException dontSupportImporter(String database) {
        return new JdbdException(String.format("%s don't support importer.", database));
    }


    public static JdbdException dontSupportExporter(String database) {
        return new JdbdException(String.format("%s don't support exporter.", database));
    }

    /**
     * @return {@link IllegalArgumentException} not {@link JdbdException}
     * @see io.jdbd.session.DatabaseSession#bindStatement(String, boolean)
     */
    public static IllegalArgumentException bindSqlHaveNoText() {
        return new IllegalArgumentException("bind sql must have text.");
    }

    /**
     * @return {@link IllegalArgumentException} not {@link JdbdException}
     * @see io.jdbd.statement.Statement#setTimeout(int)
     */
    public static IllegalArgumentException timeoutIsNegative(int timeout) {
        return new IllegalArgumentException(String.format("timeout[%s] is negative.", timeout));
    }

    /**
     * @return {@link IllegalArgumentException} not {@link JdbdException}
     * @see io.jdbd.statement.Statement#setFetchSize(int)
     */
    public static IllegalArgumentException fetchSizeIsNegative(int fetchSize) {
        return new IllegalArgumentException(String.format("fetchSize[%s] is negative.", fetchSize));
    }


    public static String safeClassName(@Nullable Object value) {
        return value == null ? "" : value.getClass().getName();
    }

    public static JdbdException dontSupportJavaType(Object indexOrName, @Nullable Object value, String database) {
        String m;
        m = String.format("%s don't support java type[%s] at index/name[%s]", database, safeClassName(value),
                indexOrName);
        return new JdbdException(m);
    }

    public static JdbdException nonNullBindValueOf(DataType nullType) {
        String m = String.format("Must be null of %s", nullType);
        return new JdbdException(m);
    }

    public static JdbdException haveExistedTransaction() {
        return new JdbdException("Have existed transaction , couldn't start new transaction.");
    }

    public static JdbdException dontSupportOutParameter(Object indexOrName, Class<? extends Statement> stmtClass,
                                                        String database) {
        String m = String.format("%s %s don't support %s at index/name %s.", database, stmtClass.getName(),
                OutParameter.class.getName(), indexOrName);
        return new JdbdException(m);
    }

    public static JdbdException stmtVarDuplication(String name) {
        return new JdbdException(String.format("statement variable[%s] duplication.", name));
    }


    public static JdbdException dontSupportMultiStmt() {
        return new JdbdException("current session don't support multi-statement.",
                SQLStates.SYNTAX_ERROR, 0);
    }

    public static JdbdException wrapForMessage(final Throwable e) {
        final JdbdException error;
        if (e instanceof IndexOutOfBoundsException && isByteBufOutflow(e)) {
            error = tooLargeObject(e);
        } else {
            error = wrap(e);
        }
        return error;
    }

    public static boolean isByteBufOutflow(final Throwable e) {
        if (!(e instanceof IndexOutOfBoundsException)) {
            return false;
        }
        final String bufClassName = "io.netty.buffer.AbstractByteBuf";
        final String bufClassPrefix = "io.netty.buffer.";
        boolean match = false;
        String className;
        for (StackTraceElement se : e.getStackTrace()) {
            className = se.getClassName();
            if (className.equals(bufClassName)
                    || className.startsWith(bufClassPrefix)) {
                match = true;
                break;
            }
        }
        return match;
    }


    /**
     * @see Statement#setOption(Option, Object)
     */
    public static JdbdException dontSupportSetOption(Option<?> option) {
        return new JdbdException(String.format("dont' support set %s", option));
    }

    public static JdbdException concurrentStartTransaction() {
        return new JdbdException("Concurrent start transaction.");
    }


    public static JdbdException factoryClosed(String name) {
        String m = String.format("%s[%s] have closed", DatabaseSessionFactory.class.getName(), name);
        return new JdbdException(m);
    }


    public static Throwable wrapIfNonJvmFatal(Throwable e) {
        return isJvmFatal(e) ? e : wrap(e);
    }


    public static boolean isJvmFatal(@Nullable Throwable e) {
        return e instanceof VirtualMachineError
                || e instanceof LinkageError;
    }

    public static JdbdException notSupportBindJavaType(Class<?> notSupportType) {
        String m = String.format("Don't support %s", notSupportType.getName());
        return new JdbdException(m);
    }


    public static JdbdException createException(List<? extends Throwable> errorList) {
        final JdbdException e;
        if (errorList.size() == 1) {
            e = wrap(errorList.get(0));
        } else {
            e = new JdbdCompositeException(errorList);
        }
        return e;
    }

    public static void printCompositeException(final JdbdCompositeException ce, Logger logger) {
        Throwable e;
        List<? extends Throwable> list = ce.getErrorList();
        final int size = list.size();
        for (int i = 0; i < size; i++) {
            e = list.get(i);
            logger.error("JdbdCompositeException element {} : ", i, e);
        }

    }

    public static JdbdException createMultiStatementError() {
        return createSyntaxError("You have an error in your SQL syntax,sql is multi statement; near ';' ");
    }

    public static JdbdException createSyntaxError(String reason) {
        return new JdbdException(reason, SQLStates.SYNTAX_ERROR, 0);
    }

    public static JdbdException cannotReuseStatement(Class<? extends Statement> stmtClass) {
        return new JdbdException(String.format("Can't reuse %s .", stmtClass.getName()));
    }

    public static JdbdException preparedStatementClosed() {
        return new JdbdException(String.format("%s have closed.", PreparedStatement.class.getName()));
    }


    public static JdbdException multiStmtNoSql() {
        return new JdbdException("MultiStatement no sql,should invoke addStatement(String) method.");
    }

    public static JdbdException noReturnColumn() {
        return new JdbdException("No return column");
    }

    public static JdbdException invalidParameterValue(int stmtIndex, int paramIndex) {
        String m;
        if (stmtIndex == 0) {
            m = String.format("Invalid parameter at  param[index:%s]", paramIndex);
        } else {
            m = String.format("Invalid parameter at batch[index:%s] param[index:%s]", stmtIndex, paramIndex);
        }
        return new JdbdException(m, SQLStates.INVALID_PARAMETER_VALUE, 0);
    }

    public static JdbdException beyondFirstParamGroupRange(int indexBasedZero, int firstGroupSize) {
        String m = String.format("bind index[%s] beyond first param group range [0,%s) .", indexBasedZero,
                firstGroupSize);
        return new JdbdException(m, SQLStates.INVALID_PARAMETER_VALUE, 0);
    }


    public static JdbdException notMatchWithFirstParamGroupCount(int stmtIndex, int paramCount, int firstGroupSize) {
        final String m;
        if (stmtIndex == 0) {
            m = String.format("Param count[%s] and first group param count[%s] not match."
                    , paramCount, firstGroupSize);
        } else {
            m = String.format("Group[index:%s] param count[%s] and first group param count[%s] not match."
                    , stmtIndex, paramCount, firstGroupSize);
        }
        return new JdbdException(m, SQLStates.INVALID_PARAMETER_VALUE, 0);
    }

    public static JdbdException parameterCountMatch(int batchIndex, int paramCount, int bindCount) {
        String m;
        if (batchIndex < 0) {
            m = String.format("parameter count[%s] and bind count[%s] not match.", paramCount, bindCount);
        } else {
            m = String.format("Batch[index:%s] parameter count[%s] and bind count[%s] not match."
                    , batchIndex, paramCount, bindCount);
        }
        return new JdbdException(m, SQLStates.INVALID_PARAMETER_VALUE, 0);
    }

    public static JdbdException duplicationParameter(int stmtIndex, int paramIndex) {
        String m;
        if (stmtIndex == 0) {
            m = String.format("parameter [index:%s] duplication.", paramIndex);
        } else {
            m = String.format("Batch[index:%s] parameter [index:%s] duplication."
                    , stmtIndex, paramIndex);
        }
        return new JdbdException(m, SQLStates.INVALID_PARAMETER_VALUE, 0);
    }

    public static JdbdException noParameterValue(int stmtIndex, int paramIndex) {
        String m;
        if (stmtIndex == 0) {
            m = String.format("No value specified for parameter[index:%s].", paramIndex);
        } else {
            m = String.format("Batch[index:%s] No value specified for parameter[index:%s]."
                    , stmtIndex, paramIndex);
        }
        return new JdbdException(m, SQLStates.INVALID_PARAMETER_VALUE, 0);
    }

    public static JdbdException noAnyParamGroupError() {
        return new JdbdException("Not found any parameter group.", SQLStates.INVALID_PARAMETER_VALUE, 0);
    }

    public static JdbdException noInvokeAddBatch() {
        return new JdbdException("Not invoke addBatch()", SQLStates.INVALID_PARAMETER_VALUE, 0);
    }

    public static JdbdException noInvokeAndAddStatement() {
        return new JdbdException("Not invoke addStatement() method.");
    }

    public static JdbdException unknownSavePoint(SavePoint savePoint) {
        return new JdbdException(String.format("unknown %s %s", SavePoint.class.getName(), savePoint));
    }

    public static JdbdException unknownStmt(Stmt stmt) {
        return new JdbdException(String.format("unknown %s %s", Stmt.class.getName(), stmt));
    }

    public static JdbdException unknownIsolation(Isolation isolation) {
        return new JdbdException(String.format("unknown %s[%s]", Isolation.class.getName(), isolation.name()));
    }

    public static JdbdException unknownOption(Option<?> option) {
        return new JdbdException(String.format("unknown %s", option));
    }

    public static JdbdException dontSupportDeclareCursor(String database) {
        return new JdbdException(String.format("%s don't support declare cursor.", database));
    }

    public static JdbdException dontSupportStmtVar(String database) {
        return new JdbdException(String.format("%s don't support stmt variable.", database));
    }

    public static JdbdException savePointNameIsEmpty() {
        return new JdbdException("save point name must non-empty");
    }


    public static JdbdException outOfTypeRange(final int batchIndex, final Value value) {
        return outOfTypeRange(batchIndex, value, null);

    }

    public static JdbdException outOfTypeRange(final int batchIndex, final Value value, final @Nullable Throwable cause) {
        String m;
        if (batchIndex < 0) {
            m = String.format("parameter[%s] value out of number range for %s",
                    getValueLabel(value), value.getType());
        } else {
            m = String.format("batch[%s] parameter[%s] value out of number range for %s",
                    batchIndex, getValueLabel(value), value.getType());
        }
        final JdbdException e;
        if (cause == null) {
            e = new JdbdException(m, SQLStates.INVALID_PARAMETER_VALUE, 0);
        } else {
            e = new JdbdException(m, cause, SQLStates.INVALID_PARAMETER_VALUE, 0);
        }
        return e;

    }

    public static JdbdException cannotConvertColumnValue(ColumnMeta meta, Object source, Class<?> targetClass,
                                                         @Nullable Throwable cause) {
        final String valueMsg;
        if (source instanceof String) {
            valueMsg = "${text}"; // for information safe
        } else {
            valueMsg = source.toString();
        }
        String m;
        m = String.format("couldn't convert %s[%s] to %s for column[index:%s,label:%s,typeName:%s]",
                source.getClass().getName(),
                valueMsg,
                targetClass.getName(),
                meta.getColumnIndex(),
                meta.getColumnLabel(),
                meta.getDataType().typeName()
        );

        final JdbdException e;
        if (cause == null) {
            e = new JdbdException(m);
        } else {
            e = new JdbdException(m, cause);
        }
        return e;
    }

    public static JdbdException cannotConvertElementColumnValue(ColumnMeta meta, Object source, Class<?> targetClass,
                                                                Class<?> elementClass, @Nullable Throwable cause) {
        final String valueMsg;
        if (source instanceof String || source instanceof TextPath) {
            valueMsg = "${text}"; // for information safe
        } else {
            valueMsg = source.toString();
        }
        String m;
        m = String.format("couldn't convert %s[%s] to %s<%s> for column[index:%s,label:%s,typeName:%s]",
                source.getClass().getName(),
                valueMsg,
                targetClass.getName(),
                elementClass.getName(),
                meta.getColumnIndex(),
                meta.getColumnLabel(),
                meta.getDataType().typeName()
        );

        final JdbdException e;
        if (cause == null) {
            e = new JdbdException(m);
        } else {
            e = new JdbdException(m, cause);
        }
        return e;
    }

    public static JdbdException dontSupportParam(ColumnMeta meta, Object source, @Nullable Throwable cause) {
        String m = String.format("column[index:%s,label:%s,typeName:%s] don't support %s .",
                meta.getColumnIndex(),
                meta.getColumnLabel(),
                meta.getDataType().typeName(),
                safeClassName(source)
        );
        final JdbdException e;
        if (cause == null) {
            e = new JdbdException(m);
        } else {
            e = new JdbdException(m, cause);
        }
        return e;
    }

    public static JdbdException readLocalFileError(int batchIndex, ColumnMeta meta, PathParameter parameter,
                                                   Throwable cause) {
        final String batchMsg;
        if (batchIndex < 0) {
            batchMsg = "";
        } else {
            batchMsg = "batchIndex[%s] ";
        }
        String m = String.format("%s column[index:%s,label:%s,typeName:%s] read local file[%s] occur error.",
                batchMsg,
                meta.getColumnIndex(),
                meta.getColumnLabel(),
                meta.getDataType().typeName(),
                parameter.value()
        );
        return new JdbdException(m, cause);
    }

    public static JdbdException columnValueOverflow(ColumnMeta meta, Object source, Class<?> targetClass,
                                                    @Nullable Throwable cause) {
        final String valueMsg;
        if (source instanceof String) {
            valueMsg = "${text}"; // for information safe
        } else {
            valueMsg = source.toString();
        }
        String m;
        m = String.format("column[index:%s,label:%s,typeName:%s] value %s[%s] overflow for target %s ",
                meta.getColumnIndex(),
                meta.getColumnLabel(),
                meta.getDataType().typeName(),
                source.getClass().getName(),
                valueMsg,
                targetClass.getName());

        final JdbdException e;
        if (cause == null) {
            e = new JdbdException(m);
        } else {
            e = new JdbdException(m, cause);
        }
        return e;
    }

    /**
     * @return {@link NullPointerException} not {@link JdbdException}
     */
    public static NullPointerException columnIsNull(ColumnMeta meta) {
        String m = String.format("column[index:%s,label:%s] is null", meta.getColumnIndex(), meta.getColumnLabel());
        return new NullPointerException(m);
    }

    public static JdbdException beyondMessageLength(int batchIndex, ParamValue bindValue) {
        String m;
        if (batchIndex < 0) {
            m = String.format("parameter[%s] too long so beyond message rest length"
                    , bindValue.getIndex());
        } else {
            m = String.format("batch[%s] parameter[%s] too long so beyond message rest length"
                    , batchIndex, bindValue.getIndex());
        }
        return new JdbdException(m);
    }

    public static JdbdException tooLargeObject() {
        return new JdbdException("Object too large,beyond message length.");
    }

    public static JdbdException tooLargeObject(Throwable e) {
        return new JdbdException("Object too large,beyond message length.", e);
    }

    public static JdbdException localFileWriteError(int batchIndex, SQLType sqlType, ParamValue bindValue,
                                                    Throwable e) {
        Path path = (Path) bindValue.getNonNull();
        String m;
        if (batchIndex < 0) {
            m = String.format("parameter[%s] path[%s] to sql type[%s]"
                    , bindValue.getIndex(), path, sqlType);
        } else {
            m = String.format("batch[%s] parameter[%s] path[%s] to sql type[%s]"
                    , batchIndex, bindValue.getIndex(), bindValue.get(), sqlType);
        }
        throw new JdbdException(m, e);
    }

    /**
     * @param batchIndex negative:single stmt;not negative representing batch index of batch operation.
     */
    public static JdbdException nonSupportBindSqlTypeError(int batchIndex, final Value bindValue) {
        final String m;
        if (batchIndex < 0) {
            m = String.format("parameter[%s] javaType[%s] bind to sql type[%s] not supported."
                    , getValueLabel(bindValue)
                    , bindValue.getNonNull().getClass().getName()
                    , bindValue.getType());
        } else {
            m = String.format("batch[%s] parameter[%s] javaType[%s] bind to sql type[%s] not supported."
                    , batchIndex
                    , getValueLabel(bindValue)
                    , bindValue.getNonNull().getClass().getName()
                    , bindValue.getType());
        }
        return new JdbdException(m, SQLStates.INVALID_PARAMETER_VALUE, 0);
    }

    public static JdbdException dontSupportOptionMap(String database, String method, @Nullable Map<Option<?>, ?> optionMap) {
        String m = String.format("%s %s method don't support option map %s", database, method, optionMap);
        return new JdbdException(m);
    }

    public static JdbdException notSupportClientCharset(final Charset charset) {
        String m = String.format("client charset[%s] isn't supported,because %s encode ASCII to multi bytes.",
                charset.name(), charset.name());
        throw new JdbdException(m);
    }

    public static JdbdException transactionExistsRejectStart(Object sessionId) {
        String m;
        m = String.format("Session[%s] in transaction ,reject start a new transaction before commit or rollback.",
                sessionId);
        throw new JdbdException(m);
    }

    public static JdbdException startTransactionFailure(Object sessionId) {
        String m = String.format("start transaction failure in session[%s]", sessionId);
        return new JdbdException(m);
    }

    public static JdbdException commitTransactionFailure(Object sessionId) {
        String m = String.format("commit transaction failure in session[%s]", sessionId);
        return new JdbdException(m);
    }

    public static JdbdException rollbackTransactionFailure(Object sessionId) {
        String m = String.format("rollback transaction failure in session[%s]", sessionId);
        return new JdbdException(m);
    }

    public static JdbdException transactionExistsRejectSet(Object sessionId) {
        String m;
        m = String.format("Session[%s] in transaction ,reject set transaction characteristic before commit or rollback."
                , sessionId);
        throw new JdbdException(m);
    }


    public static XaException xaInvalidFlagForStart(final int flags) {
        return xaInvalidFlag(flags, "start");
    }

    public static XaException xaInvalidFlagForEnd(final int flags) {
        return xaInvalidFlag(flags, "end");
    }

    public static XaException xaInvalidFlagForRecover(final int flags) {
        return xaInvalidFlag(flags, "recover");
    }

    public static XaException xaDontSupportOptionMap(String command, @Nullable Map<Option<?>, ?> optionMap) {
        String m = String.format("%s command don't support option map %s", command, optionMap);
        return new XaException(m, SQLStates.ER_XAER_INVAL, 0, XaException.XAER_INVAL);
    }

    public static XaException xaBusyOnOtherTransaction() {
        return new XaException("session is busy with another transaction", XaException.XAER_PROTO);
    }


    public static XaException xaGtridNoText() {
        return new XaException("gtrid of xid must have text.", SQLStates.ER_XAER_NOTA, 0, XaException.XAER_NOTA);
    }

    public static XaException xaBqualNonNullAndNoText() {
        return new XaException("bqual of xid must be null or  have text.", SQLStates.ER_XAER_NOTA, 0, XaException.XAER_NOTA);
    }

    public static XaException xaGtridBeyond64Bytes() {
        return new XaException("bytes length of gtrid beyond 64 bytes.", SQLStates.ER_XAER_NOTA, 0, XaException.XAER_NOTA);
    }

    public static XaException xaBqualBeyond64Bytes() {
        return new XaException("bytes length of bqual beyond 64 bytes.", SQLStates.ER_XAER_NOTA, 0, XaException.XAER_NOTA);
    }


    public static XaException xidIsNull() {
        return new XaException("xid must be non-null", SQLStates.ER_XAER_INVAL, 0, XaException.XAER_INVAL);
    }

    public static XaException xaTransactionOptionIsNull() {
        return new XaException("xid must be non-null", SQLStates.ER_XAER_INVAL, 0, XaException.XAER_INVAL);
    }

    public static XaException xaNonCurrentTransaction(@Nullable Xid xid) {
        String m = String.format("xid[%s] not current transaction.", xid);
        return new XaException(m, XaException.XAER_PROTO);
    }

    public static XaException xaUnknownTransaction(Xid xid) {
        String m = String.format("xid[%s] is unknown.", xid);
        return new XaException(m, XaException.XAER_PROTO);
    }

    public static XaException xaTransactionRollbackOnly(@Nullable Xid xid) {
        String m = String.format("xid[%s] is rollback-only.", xid);
        return new XaException(m, XaException.XA_RBROLLBACK);
    }

    public static XaException xaTransactionDontSupportEndCommand(@Nullable Xid xid, XaStates states) {
        String m = String.format("xid[%s] %s don't support end command", xid, states);
        return new XaException(m, XaException.XAER_PROTO);
    }

    public static XaException xaStatesDontSupportPrepareCommand(@Nullable Xid xid, XaStates states) {
        String m = String.format("xid[%s] %s don't support prepare command", xid, states);
        return new XaException(m, XaException.XAER_PROTO);
    }

    public static XaException xaStatusDontSupportRollbackCommand(@Nullable Xid xid, XaStates states) {
        String m = String.format("xid[%s] %s don't support rollback command", xid, states);
        return new XaException(m, XaException.XAER_PROTO);
    }

    public static XaException xaStatesDontSupportCommitCommand(@Nullable Xid xid, XaStates states) {
        String m = String.format("xid[%s] %s don't support commit command", xid, states);
        return new XaException(m, XaException.XAER_PROTO);
    }

    public static XaException xaDontSupportSuspendResume() {
        return new XaException("suspend/resume not implemented", XaException.XAER_RMERR);
    }


    /*################################## blow protected method ##################################*/


    protected static Object getValueLabel(Value value) {
        final Object paramLabel;
        if (value instanceof ParamValue) {
            paramLabel = ((ParamValue) value).getIndex();
        } else if (value instanceof NamedValue) {
            paramLabel = ((NamedValue) value).getName();
        } else {
            throw new IllegalArgumentException(String.format("Unknown %s type[%s]", Value.class.getName(), value));
        }
        return paramLabel;
    }


    private static XaException xaInvalidFlag(final int flags, final String method) {
        String m = String.format("XA invalid flag[%s] for method %s", Integer.toBinaryString(flags), method);
        return new XaException(m, XaException.XAER_INVAL);
    }


}
