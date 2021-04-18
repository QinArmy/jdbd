package io.jdbd.mysql.protocol.client;

import io.jdbd.*;
import io.jdbd.mysql.protocol.conf.PropertyKey;
import io.jdbd.mysql.session.ServerPreparedStatement;
import io.jdbd.mysql.stmt.BatchBindWrapper;
import io.jdbd.mysql.stmt.BindableWrapper;
import io.jdbd.mysql.stmt.PrepareStmtTask;
import io.jdbd.mysql.util.MySQLCollections;
import io.jdbd.mysql.util.MySQLExceptions;
import io.jdbd.result.ResultRow;
import io.jdbd.result.ResultStates;
import io.jdbd.stmt.*;
import io.jdbd.vendor.JdbdCompositeException;
import io.jdbd.vendor.result.JdbdMultiResults;
import io.jdbd.vendor.result.MultiResultSink;
import io.jdbd.vendor.result.ReactorMultiResult;
import io.jdbd.vendor.result.ResultRowSink;
import io.jdbd.vendor.stmt.BatchWrapper;
import io.jdbd.vendor.stmt.ParamValue;
import io.jdbd.vendor.stmt.ParamWrapper;
import io.jdbd.vendor.stmt.StmtWrapper;
import io.jdbd.vendor.task.TaskAdjutant;
import io.netty.buffer.ByteBuf;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <p>  code navigation :
 *     <ol>
 *         <li>decode entrance method : {@link #decode(ByteBuf, Consumer)}</li>
 *         <li>send COM_STMT_PREPARE : {@link #start()} </li>
 *         <li>read COM_STMT_PREPARE Response : {@link #readPrepareResponse(ByteBuf)}
 *              <ol>
 *                  <li>read parameter meta : {@link #readPrepareParameterMeta(ByteBuf, Consumer)}</li>
 *                  <li>read prepare column meta : {@link #readPrepareColumnMeta(ByteBuf, Consumer)}</li>
 *              </ol>
 *         </li>
 *         <li>send COM_STMT_EXECUTE :
 *              <ul>
 *                  <li>{@link #executeStatement()}</li>
 *                  <li> {@link PrepareExecuteCommandWriter#writeCommand(int, List)}</li>
 *                  <li>send COM_STMT_SEND_LONG_DATA:{@link PrepareLongParameterWriter#write(int, List)}</li>
 *              </ul>
 *         </li>
 *         <li>read COM_STMT_EXECUTE Response : {@link #readExecuteResponse(ByteBuf, Consumer)}</li>
 *         <li>read Binary Protocol ResultSet Row : {@link BinaryResultSetReader#read(ByteBuf, Consumer)}</li>
 *         <li>send COM_STMT_FETCH:{@link #createFetchPacket()}</li>
 *         <li>read COM_STMT_FETCH response:{@link #readFetchResponse(ByteBuf)}</li>
 *         <li>send COM_STMT_RESET:{@link #createResetPacket()}</li>
 *         <li>read COM_STMT_RESET response:{@link #readResetResponse(ByteBuf, Consumer)}</li>
 *         <li>send COM_STMT_CLOSE : {@link #createCloseStatementPacket()}</li>
 *     </ol>
 * </p>
 *
 * <p>
 * below is chinese signature:<br/>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 *
 * @see PrepareExecuteCommandWriter
 * @see PrepareLongParameterWriter
 * @see BinaryResultSetReader
 * @see <a href="https://dev.mysql.com/doc/dev/mysql-server/latest/page_protocol_command_phase_ps.html">Prepared Statements</a>
 */
final class ComPreparedTask extends MySQLPrepareCommandTask implements StatementTask, PrepareStmtTask {


    /**
     * <p>
     * This method is one of underlying api of {@link BindableStatement#executeUpdate()} method:
     * </p>
     *
     * @see #ComPreparedTask(ParamWrapper, MonoSink, MySQLTaskAdjutant)
     * @see ComQueryTask#bindableUpdate(BindableWrapper, MySQLTaskAdjutant)
     */
    static Mono<ResultStates> update(final ParamWrapper wrapper, final MySQLTaskAdjutant adjutant) {
        return Mono.create(sink -> {
            try {
                ComPreparedTask task = new ComPreparedTask(wrapper, sink, adjutant);
                task.submit(sink::error);
            } catch (Throwable e) {
                sink.error(MySQLExceptions.wrap(e));
            }
        });
    }

    /**
     * <p>
     * This method is one of underlying api of {@link BindableStatement#executeQuery()} method:
     * </p>
     *
     * @see #ComPreparedTask(ParamWrapper, FluxSink, MySQLTaskAdjutant)
     * @see ComQueryTask#bindableQuery(BindableWrapper, MySQLTaskAdjutant)
     */
    static Flux<ResultRow> query(final ParamWrapper wrapper, final MySQLTaskAdjutant adjutant) {
        return Flux.create(sink -> {
            try {
                ComPreparedTask task = new ComPreparedTask(wrapper, sink, adjutant);
                task.submit(sink::error);
            } catch (Throwable e) {
                sink.error(MySQLExceptions.wrap(e));
            }
        });
    }

    /**
     * <p>
     * This method is one underlying api of {@link BindableStatement#executeBatch()} method:
     * </p>
     *
     * @see #ComPreparedTask(FluxSink, BatchWrapper, MySQLTaskAdjutant)
     * @see ComQueryTask#bindableBatch(BatchBindWrapper, MySQLTaskAdjutant)
     */
    static Flux<ResultStates> batchUpdate(final BatchWrapper<? extends ParamValue> wrapper
            , final MySQLTaskAdjutant adjutant) {
        return Flux.create(sink -> {
            try {
                ComPreparedTask task = new ComPreparedTask(sink, wrapper, adjutant);
                task.submit(sink::error);
            } catch (Throwable e) {
                sink.error(MySQLExceptions.wrap(e));
            }

        });
    }

    /**
     * <p>
     * This method is one of underlying api of below methods:
     * <ul>
     *     <li>{@link BindableStatement#executeMulti()}</li>
     *     <li>{@link MultiStatement#executeMulti()}</li>
     * </ul>
     * </p>
     *
     * @return deferred {@link ReactorMultiResult} created by {@link JdbdMultiResults#create(TaskAdjutant, Consumer)} method.
     * @see #ComPreparedTask(ParamWrapper, MultiResultSink, MySQLTaskAdjutant)
     * @see ComQueryTask#bindableMultiStmt(List, MySQLTaskAdjutant)
     */
    public ReactorMultiResult multiResults(final ParamWrapper wrapper, final MySQLTaskAdjutant adjutant) {
        return JdbdMultiResults.create(adjutant, sink -> {
            try {
                ComPreparedTask task = new ComPreparedTask(wrapper, sink, adjutant);
                task.submit(sink::error);
            } catch (Throwable e) {
                sink.error(MySQLExceptions.wrap(e));
            }
        });
    }

    /**
     * <p>
     * This method is one of underlying api of below methods:
     * <ul>
     *     <li>{@link BindableStatement#executeBatchMulti()}</li>
     *     <li>{@link MultiStatement#executeBatchMulti()}</li>
     * </ul>
     * </p>
     *
     * @see #ComPreparedTask(MySQLTaskAdjutant, BatchWrapper, FluxSink)
     * @see PreparedStatement#executeBatchMulti()
     */
    static Flux<ReactorMultiResult> batchMultiResult(final BatchWrapper<? extends ParamValue> wrapper
            , final MySQLTaskAdjutant adjutant) {
        return Flux.create(sink -> {
            try {
                ComPreparedTask task = new ComPreparedTask(adjutant, wrapper, sink);
                task.submit(sink::error);
            } catch (Throwable e) {
                sink.error(MySQLExceptions.wrap(e));
            }
        });
    }


    /**
     * <p>
     * This method is underlying api of below methods:
     * <ul>
     *     <li>{@link DatabaseSession#prepare(String)}</li>
     *     <li>{@link DatabaseSession#prepare(String, int)}</li>
     * </ul>
     * </p>
     *
     * @see DatabaseSession#prepare(String)
     * @see DatabaseSession#prepare(String, int)
     * @see #ComPreparedTask(MySQLTaskAdjutant, MonoSink, StmtWrapper)
     */
    static Mono<PreparedStatement> prepare(final StmtWrapper wrapper, final MySQLTaskAdjutant adjutant) {
        return Mono.create(sink -> {
            try {
                ComPreparedTask task = new ComPreparedTask(adjutant, sink, wrapper);
                task.submit(sink::error);
            } catch (Throwable e) {
                sink.error(MySQLExceptions.wrap(e));
            }
        });
    }


    private static final Logger LOG = LoggerFactory.getLogger(ComPreparedTask.class);

    private final DownstreamSink downstreamSink;

    private int statementId;

    private Phase phase = Phase.PREPARED;

    private MySQLColumnMeta[] parameterMetas = MySQLColumnMeta.EMPTY;

    private int parameterMetaIndex = -1;

    private MySQLColumnMeta[] prepareColumnMetas = MySQLColumnMeta.EMPTY;

    private int columnMetaIndex = -1;

    private List<JdbdException> errorList;


    /**
     * @see #update(ParamWrapper, MySQLTaskAdjutant)
     */
    private ComPreparedTask(final ParamWrapper wrapper, final MonoSink<ResultStates> sink
            , final MySQLTaskAdjutant adjutant) throws SQLException {
        super(adjutant);

        this.packetPublisher = createPrepareCommand(wrapper);
        this.downstreamSink = new UpdateDownstreamSink(wrapper, sink);
    }

    /**
     * <p>
     * create a prepare statement task for query.
     * </p>
     *
     * @see #query(ParamWrapper, MySQLTaskAdjutant)
     */
    private ComPreparedTask(final ParamWrapper wrapper, final FluxSink<ResultRow> sink
            , final MySQLTaskAdjutant adjutant) throws SQLException {
        super(adjutant);
        this.packetPublisher = createPrepareCommand(wrapper);
        this.downstreamSink = new QueryDownstreamSink(wrapper, sink);
    }

    /**
     * @see #batchUpdate(BatchWrapper, MySQLTaskAdjutant)
     */
    private ComPreparedTask(final FluxSink<ResultStates> sink, final BatchWrapper<? extends ParamValue> wrapper
            , final MySQLTaskAdjutant adjutant) throws SQLException {
        super(adjutant);
        this.packetPublisher = createPrepareCommand(wrapper);
        this.downstreamSink = new BatchUpdateSink<>(wrapper, sink);
    }

    /**
     * @see #prepare(StmtWrapper, MySQLTaskAdjutant)
     */
    private ComPreparedTask(final MySQLTaskAdjutant adjutant, MonoSink<PreparedStatement> sink, StmtWrapper wrapper)
            throws SQLException {
        super(adjutant);
        this.packetPublisher = createPrepareCommand(wrapper);
        this.downstreamSink = new DownstreamAdapter(sink);
    }

    /**
     * @see #multiResults(ParamWrapper, MySQLTaskAdjutant)
     */
    private ComPreparedTask(final ParamWrapper wrapper, MultiResultSink sink, final MySQLTaskAdjutant adjutant)
            throws SQLException {
        super(adjutant);
        this.packetPublisher = createPrepareCommand(wrapper);
        this.downstreamSink = new MultiResultDownstreamSink(wrapper, sink);
    }


    /**
     * @see #batchMultiResult(BatchWrapper, MySQLTaskAdjutant)
     */
    private ComPreparedTask(final MySQLTaskAdjutant adjutant, final BatchWrapper<? extends ParamValue> wrapper
            , final FluxSink<ReactorMultiResult> sink) throws SQLException {
        super(adjutant);
        this.packetPublisher = createPrepareCommand(wrapper);
        this.downstreamSink = new BatchMultiResultDownstreamSink<>(wrapper, sink);
    }


    @Override
    public final int obtainStatementId() {
        return this.statementId;
    }

    @Override
    public final MySQLColumnMeta[] obtainParameterMetas() {
        return Objects.requireNonNull(this.parameterMetas, "this.parameterMetas");
    }

    @Override
    public final ClientProtocolAdjutant obtainAdjutant() {
        return this.adjutant;
    }


    @Override
    public final boolean supportFetch() {
        final DownstreamSink downstreamSink = this.downstreamSink;
        return downstreamSink instanceof FetchAbleDownstreamSink
                && ((FetchAbleDownstreamSink) downstreamSink).getFetchSize() > 0;
    }

    /**
     * @see PrepareStmtTask#executeUpdate(ParamWrapper)
     */
    @Override
    public final Mono<ResultStates> executeUpdate(final ParamWrapper wrapper) {
        return Mono.create(sink -> {
            if (this.adjutant.inEventLoop()) {
                doPreparedUpdate(wrapper, sink);
            } else {
                this.adjutant.execute(() -> doPreparedUpdate(wrapper, sink));
            }
        });
    }

    /**
     * @see PrepareStmtTask#executeQuery(ParamWrapper)
     */
    @Override
    public final Flux<ResultRow> executeQuery(ParamWrapper wrapper) {
        return Flux.create(sink -> {
            if (this.adjutant.inEventLoop()) {
                doPreparedQuery(wrapper, sink);
            } else {
                this.adjutant.execute(() -> doPreparedQuery(wrapper, sink));
            }
        });
    }

    /**
     * @see PrepareStmtTask#executeBatch(BatchWrapper)
     */
    @Override
    public final Flux<ResultStates> executeBatch(final BatchWrapper<? extends ParamValue> wrapper) {
        return Flux.create(sink -> {
            if (this.adjutant.inEventLoop()) {
                doPreparedBatchUpdate(wrapper, sink);
            } else {
                this.adjutant.execute(() -> doPreparedBatchUpdate(wrapper, sink));
            }
        });
    }

    /**
     * @see PrepareStmtTask#execute(ParamWrapper)
     */
    @Override
    public final ReactorMultiResult execute(ParamWrapper wrapper) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getWarnings() {
        final DownstreamSink downstreamSink = this.downstreamSink;
        if (!(downstreamSink instanceof DownstreamAdapter)) {
            throw new IllegalStateException(
                    String.format("%s isn't %s", downstreamSink, DownstreamAdapter.class.getSimpleName()));
        }
        return ((DownstreamAdapter) downstreamSink).warnings;
    }

    /*################################## blow protected  method ##################################*/

    /**
     * @see <a href="https://dev.mysql.com/doc/dev/mysql-server/latest/page_protocol_com_stmt_prepare.html">Protocol::COM_STMT_PREPARE</a>
     */
    @Override
    protected Publisher<ByteBuf> start() {

        final Publisher<ByteBuf> publisher;
        publisher = Objects.requireNonNull(this.packetPublisher, "this.packetPublisher");
        this.packetPublisher = null;
        this.phase = Phase.READ_PREPARE_RESPONSE;
        return publisher;
    }


    @Override
    protected boolean decode(final ByteBuf cumulateBuffer, final Consumer<Object> serverStatusConsumer) {
        if (!PacketUtils.hasOnePacket(cumulateBuffer)) {
            return false;
        }
        boolean taskEnd = false, continueDecode = true;
        while (continueDecode) {
            switch (this.phase) {
                case READ_PREPARE_RESPONSE: {
                    if (readPrepareResponse(cumulateBuffer)) {
                        taskEnd = true;
                        continueDecode = false;
                    } else {
                        this.phase = Phase.READ_PREPARE_PARAM_META;
                        continueDecode = PacketUtils.hasOnePacket(cumulateBuffer);
                    }
                }
                break;
                case READ_PREPARE_PARAM_META: {
                    if (readPrepareParameterMeta(cumulateBuffer, serverStatusConsumer)) {
                        if (hasReturnColumns()) {
                            this.phase = Phase.READ_PREPARE_COLUMN_META;
                            continueDecode = PacketUtils.hasOnePacket(cumulateBuffer);
                        } else {
                            taskEnd = handleReadPrepareComplete();
                            continueDecode = false;
                        }
                    } else {
                        continueDecode = false;
                    }
                }
                break;
                case READ_PREPARE_COLUMN_META: {
                    if (readPrepareColumnMeta(cumulateBuffer, serverStatusConsumer)) {
                        taskEnd = handleReadPrepareComplete();
                    }
                    continueDecode = false;
                }
                break;
                case READ_EXECUTE_RESPONSE: {
                    // maybe modify this.phase
                    taskEnd = readExecuteResponse(cumulateBuffer, serverStatusConsumer);
                    continueDecode = false;
                }
                break;
                case READ_RESULT_SET: {
                    taskEnd = readResultSet(cumulateBuffer, serverStatusConsumer);
                    continueDecode = false;
                }
                break;
                case READ_RESET_RESPONSE: {
                    if (readResetResponse(cumulateBuffer, serverStatusConsumer)) {
                        taskEnd = true;
                        continueDecode = false;
                    } else {
                        ((BatchUpdateSink<?>) this.downstreamSink).resetSuccess();
                        this.phase = Phase.EXECUTE;
                        taskEnd = executeStatement(); // execute command
                        if (!taskEnd) {
                            this.phase = Phase.READ_EXECUTE_RESPONSE;
                        }
                    }
                }
                break;
                case READ_FETCH_RESPONSE: {
                    if (readFetchResponse(cumulateBuffer)) {
                        taskEnd = true;
                        continueDecode = false;
                    } else {
                        this.phase = Phase.READ_RESULT_SET;
                        continueDecode = PacketUtils.hasOnePacket(cumulateBuffer);
                    }
                }
                break;
                default:
                    throw new IllegalStateException(String.format("this.phase[%s] error.", this.phase));
            }
        }
        if (taskEnd) {
            if (this.phase != Phase.CLOSE_STMT) {
                this.phase = Phase.CLOSE_STMT;
                this.packetPublisher = Mono.just(createCloseStatementPacket());
            }
            if (hasError()) {
                this.downstreamSink.error(createException());
            } else {
                this.downstreamSink.complete();
            }
        }
        return taskEnd;
    }


    @Override
    protected Action onError(Throwable e) {
        final Action action;
        switch (this.phase) {
            case PREPARED:
                action = Action.TASK_END;
                break;
            case CLOSE_STMT: {
                throw new IllegalStateException("CLOSE_STM command send error.");
            }
            default: {
                addError(MySQLExceptions.wrap(e));
                this.downstreamSink.error(createException());
                this.packetPublisher = Mono.just(createCloseStatementPacket());
                action = Action.MORE_SEND_AND_END;
            }

        }
        return action;
    }

    @Override
    protected void onChannelClose() {
        if (this.phase != Phase.CLOSE_STMT) {
            this.downstreamSink.error(new SessionCloseException("Database session have closed."));
        }
    }

    /*################################## blow private method ##################################*/

    private Publisher<ByteBuf> createPrepareCommand(StmtWrapper wrapper) throws SQLException, JdbdSQLException {
        assertPhase(Phase.PREPARED);
        String sql = wrapper.getSql();
        return PacketUtils.createSimpleCommand(PacketUtils.COM_STMT_PREPARE, sql
                , this.adjutant, this::addAndGetSequenceId);
    }


    private boolean hasError() {
        return !MySQLCollections.isEmpty(this.errorList);
    }

    private void addError(JdbdException e) {
        List<JdbdException> errorList = this.errorList;
        if (errorList == null) {
            errorList = new ArrayList<>();
            this.errorList = errorList;
        }
        errorList.add(e);
    }


    /**
     * @return true: prepare error,task end.
     * @see #decode(ByteBuf, Consumer)
     * @see <a href="https://dev.mysql.com/doc/dev/mysql-server/latest/page_protocol_com_stmt_prepare.html#sect_protocol_com_stmt_prepare_response">COM_STMT_PREPARE Response</a>
     */
    private boolean readPrepareResponse(final ByteBuf cumulateBuffer) {
        assertPhase(Phase.READ_PREPARE_RESPONSE);

        final int payloadLength = PacketUtils.readInt3(cumulateBuffer);
        updateSequenceId(PacketUtils.readInt1AsInt(cumulateBuffer));
        final int headFlag = PacketUtils.getInt1AsInt(cumulateBuffer, cumulateBuffer.readerIndex()); //1. status/error header
        final boolean taskEnd;
        switch (headFlag) {
            case ErrorPacket.ERROR_HEADER: {
                ErrorPacket error = ErrorPacket.readPacket(cumulateBuffer.readSlice(payloadLength)
                        , this.negotiatedCapability, this.adjutant.obtainCharsetError());
                addError(MySQLExceptions.createErrorPacketException(error));
                taskEnd = true;
            }
            break;
            case 0: {
                final int payloadStartIndex = cumulateBuffer.readerIndex();
                cumulateBuffer.skipBytes(1);//skip status
                this.statementId = PacketUtils.readInt4(cumulateBuffer);//2. statement_id
                resetColumnMeta(PacketUtils.readInt2AsInt(cumulateBuffer));//3. num_columns
                resetParameterMetas(PacketUtils.readInt2AsInt(cumulateBuffer));//4. num_params
                cumulateBuffer.skipBytes(1); //5. skip filler
                final int warnings = PacketUtils.readInt2AsInt(cumulateBuffer);//6. warning_count
                if (this.downstreamSink instanceof DownstreamAdapter) {
                    ((DownstreamAdapter) this.downstreamSink).warnings = warnings;
                }
                if ((this.negotiatedCapability & ClientProtocol.CLIENT_OPTIONAL_RESULTSET_METADATA) != 0) {
                    throw new IllegalStateException("Not support CLIENT_OPTIONAL_RESULTSET_METADATA"); //7. metadata_follows
                }
                cumulateBuffer.readerIndex(payloadStartIndex + payloadLength); // to next packet,avoid tail filler.
                taskEnd = false;
            }
            break;
            default: {
                throw MySQLExceptions.createFatalIoException(
                        "Server send COM_STMT_PREPARE Response error. header[%s]", headFlag);
            }
        }
        return taskEnd;
    }

    /**
     * <p>
     * this method will update {@link #phase}.
     * </p>
     *
     * @return true : task end.
     * @see #decode(ByteBuf, Consumer)
     */
    private boolean handleReadPrepareComplete() {
        final DownstreamSink downstreamSink = this.downstreamSink;
        final boolean taskEnd;
        if (downstreamSink instanceof DownstreamAdapter) {
            taskEnd = false;
            this.phase = Phase.WAIT_PARAMS;
            ((DownstreamAdapter) downstreamSink).emitStatement();
        } else {
            this.phase = Phase.EXECUTE;
            if (executeStatement()) {
                taskEnd = true;
            } else {
                taskEnd = false;
                this.phase = Phase.READ_EXECUTE_RESPONSE;
            }
        }
        return taskEnd;
    }


    /**
     * @return true:read parameter meta end.
     * @see #readPrepareResponse(ByteBuf)
     * @see #decode(ByteBuf, Consumer)
     * @see #resetParameterMetas(int)
     */
    private boolean readPrepareParameterMeta(final ByteBuf cumulateBuffer
            , final Consumer<Object> serverStatusConsumer) {
        assertPhase(Phase.READ_PREPARE_PARAM_META);
        int parameterMetaIndex = this.parameterMetaIndex;
        final MySQLColumnMeta[] metaArray = Objects.requireNonNull(this.parameterMetas, "this.parameterMetas");
        if (parameterMetaIndex < metaArray.length) {
            parameterMetaIndex = BinaryResultSetReader.readColumnMeta(cumulateBuffer, metaArray
                    , parameterMetaIndex, this::updateSequenceId, this.adjutant);
            this.parameterMetaIndex = parameterMetaIndex;
        }
        return parameterMetaIndex == metaArray.length && tryReadEof(cumulateBuffer, serverStatusConsumer);
    }


    /**
     * @return true:read column meta end.
     * @see #readPrepareColumnMeta(ByteBuf, Consumer)
     * @see #decode(ByteBuf, Consumer)
     * @see #resetColumnMeta(int)
     */
    private boolean readPrepareColumnMeta(ByteBuf cumulateBuffer, Consumer<Object> serverStatusConsumer) {
        assertPhase(Phase.READ_PREPARE_COLUMN_META);

        final MySQLColumnMeta[] metaArray = Objects.requireNonNull(this.prepareColumnMetas, "this.prepareColumnMetas");
        int columnMetaIndex = this.columnMetaIndex;
        if (columnMetaIndex < metaArray.length) {
            columnMetaIndex = BinaryResultSetReader.readColumnMeta(cumulateBuffer, metaArray
                    , columnMetaIndex, this::updateSequenceId, this.adjutant);
            this.columnMetaIndex = columnMetaIndex;
        }
        return columnMetaIndex == metaArray.length && tryReadEof(cumulateBuffer, serverStatusConsumer);
    }

    /**
     * @return false : need more cumulate
     * @see #readPrepareParameterMeta(ByteBuf, Consumer)
     * @see #readPrepareColumnMeta(ByteBuf, Consumer)
     */
    private boolean tryReadEof(ByteBuf cumulateBuffer, Consumer<Object> serverStatusConsumer) {
        boolean end = true;
        if ((this.negotiatedCapability & ClientProtocol.CLIENT_DEPRECATE_EOF) == 0) {
            if (PacketUtils.hasOnePacket(cumulateBuffer)) {
                int payloadLength = PacketUtils.readInt3(cumulateBuffer);
                updateSequenceId(PacketUtils.readInt1AsInt(cumulateBuffer));
                EofPacket eof;
                eof = EofPacket.read(cumulateBuffer.readSlice(payloadLength), this.negotiatedCapability);
                serverStatusConsumer.accept(eof.getStatusFags());
            } else {
                end = false;
            }
        }
        return end;
    }


    /**
     * @see #decode(ByteBuf, Consumer)
     * @see #onError(Throwable)
     * @see <a href="https://dev.mysql.com/doc/dev/mysql-server/latest/page_protocol_com_stmt_close.html">Protocol::COM_STMT_CLOSE</a>
     */
    private ByteBuf createCloseStatementPacket() {
        ByteBuf packet = this.adjutant.allocator().buffer(9);

        PacketUtils.writeInt3(packet, 5);
        packet.writeByte(0);// use 0 sequence_id

        packet.writeByte(PacketUtils.COM_STMT_CLOSE);
        PacketUtils.writeInt4(packet, this.statementId);
        return packet;
    }


    /**
     * <p>
     * modify {@link #phase}
     * </p>
     *
     * @return true: task end.
     * @see #decode(ByteBuf, Consumer)
     * @see #executeStatement()
     */
    private boolean readExecuteResponse(final ByteBuf cumulateBuffer, final Consumer<Object> serverStatusConsumer) {
        assertPhase(Phase.READ_EXECUTE_RESPONSE);

        final int header = PacketUtils.getInt1AsInt(cumulateBuffer, cumulateBuffer.readerIndex() + PacketUtils.HEADER_SIZE);
        final boolean taskEnd;
        switch (header) {
            case ErrorPacket.ERROR_HEADER: {
                final int payloadLength = PacketUtils.readInt3(cumulateBuffer);
                updateSequenceId(PacketUtils.readInt1AsInt(cumulateBuffer));

                ErrorPacket error = ErrorPacket.readPacket(cumulateBuffer.readSlice(payloadLength)
                        , this.negotiatedCapability, this.adjutant.obtainCharsetError());
                addError(MySQLExceptions.createErrorPacketException(error));
                taskEnd = true;
            }
            break;
            case OkPacket.OK_HEADER: {
                final int payloadLength = PacketUtils.readInt3(cumulateBuffer);
                updateSequenceId(PacketUtils.readInt1AsInt(cumulateBuffer));

                OkPacket ok = OkPacket.read(cumulateBuffer.readSlice(payloadLength), this.negotiatedCapability);
                serverStatusConsumer.accept(ok.getStatusFags());
                // emit update result
                taskEnd = this.downstreamSink.nextUpdate(MySQLResultStates.from(ok));
            }
            break;
            default: {
                this.phase = Phase.READ_RESULT_SET;
                taskEnd = readResultSet(cumulateBuffer, serverStatusConsumer);
            }
        }
        return taskEnd;
    }

    /**
     * <p>
     * modify {@link #phase}
     * </p>
     *
     * @return true: task end.
     * @see #readExecuteResponse(ByteBuf, Consumer)
     * @see #decode(ByteBuf, Consumer)
     */
    private boolean readResultSet(final ByteBuf cumulateBuffer, final Consumer<Object> serverStatusConsumer) {
        assertPhase(Phase.READ_RESULT_SET);
        return this.downstreamSink.readResultSet(cumulateBuffer, serverStatusConsumer);
    }

    /**
     * <p>
     * modify {@link #phase}
     * </p>
     *
     * @return true: task end.
     * @see #decode(ByteBuf, Consumer)
     */
    private boolean readFetchResponse(final ByteBuf cumulateBuffer) {
        final int flag = PacketUtils.getInt1AsInt(cumulateBuffer, cumulateBuffer.readerIndex() + PacketUtils.HEADER_SIZE);
        boolean taskEnd = false;
        if (flag == ErrorPacket.ERROR_HEADER) {
            final int payloadLength = PacketUtils.readInt3(cumulateBuffer);
            updateSequenceId(PacketUtils.readInt1AsInt(cumulateBuffer));
            ErrorPacket error = ErrorPacket.readPacket(cumulateBuffer.readSlice(payloadLength)
                    , this.negotiatedCapability, this.adjutant.obtainCharsetError());
            addError(MySQLExceptions.createErrorPacketException(error));
            taskEnd = true;
        }
        return taskEnd;
    }

    /**
     * @return true : reset occur error,task end.
     * @see #decode(ByteBuf, Consumer)
     * @see <a href="https://dev.mysql.com/doc/dev/mysql-server/latest/page_protocol_com_stmt_reset.html">Protocol::COM_STMT_RESET</a>
     */
    private boolean readResetResponse(final ByteBuf cumulateBuffer, final Consumer<Object> serverStatusConsumer) {
        final int payloadLength = PacketUtils.readInt3(cumulateBuffer);
        updateSequenceId(PacketUtils.readInt1AsInt(cumulateBuffer));

        final int flag = PacketUtils.getInt1AsInt(cumulateBuffer, cumulateBuffer.readerIndex());
        final boolean taskEnd;
        switch (flag) {
            case ErrorPacket.ERROR_HEADER: {
                ErrorPacket error = ErrorPacket.readPacket(cumulateBuffer.readSlice(payloadLength)
                        , this.negotiatedCapability, this.adjutant.obtainCharsetError());
                addError(MySQLExceptions.createErrorPacketException(error));
                taskEnd = true;
            }
            break;
            case OkPacket.OK_HEADER: {
                OkPacket ok = OkPacket.read(cumulateBuffer.readSlice(payloadLength), this.negotiatedCapability);
                serverStatusConsumer.accept(MySQLResultStates.from(ok));
                taskEnd = false;
            }
            break;
            default:
                throw MySQLExceptions.createFatalIoException("COM_STMT_RESET response error,flag[%s].", flag);
        }
        return taskEnd;
    }

    /**
     * @see #decode(ByteBuf, Consumer)
     * @see UpdateDownstreamSink#executeCommand()
     * @see BatchUpdateSink#executeCommand()
     */
    boolean hasReturnColumns() {
        MySQLColumnMeta[] columnMetas = this.prepareColumnMetas;
        return columnMetas != null && columnMetas.length > 0;
    }


    /**
     * @return true : task end:<ul>
     * <li>bind parameter error</li>
     * <li>batch update end</li>
     * </ul>
     * @see #decode(ByteBuf, Consumer)
     * @see <a href="https://dev.mysql.com/doc/dev/mysql-server/latest/page_protocol_com_stmt_execute.html">Protocol::COM_STMT_EXECUTE</a>
     */
    private boolean executeStatement() {
        assertPhase(Phase.EXECUTE);
        return this.downstreamSink.executeCommand();
    }

    /**
     * @return true : send occur error,task end.
     * @see DownstreamSink#executeCommand()
     */
    private boolean sendExecuteCommand(ExecuteCommandWriter writer, int stmtIndex
            , List<? extends ParamValue> parameterGroup) {
        assertPhase(Phase.EXECUTE);
        updateSequenceId(-1); // reset sequenceId

        boolean taskEnd = false;
        try {
            this.packetPublisher = writer.writeCommand(stmtIndex, parameterGroup);
        } catch (Throwable e) {
            addError(MySQLExceptions.wrap(e));
            taskEnd = true;
        }
        return taskEnd;
    }

    /**
     * @see BatchUpdateSink#nextUpdate(ResultStates)
     */
    private void sendResetCommand() {
        this.phase = Phase.RESET_STMT;
        this.packetPublisher = Mono.just(createResetPacket());
        this.phase = Phase.READ_RESET_RESPONSE;
    }

    /**
     * @see QueryDownstreamSink#readResultSet(ByteBuf, Consumer)
     */
    private void sendFetchCommand() {
        if (!this.supportFetch()) {
            throw new IllegalStateException(String.format("%s not support fetch command.", this));
        }
        this.phase = Phase.FETCH_STMT;
        this.packetPublisher = Mono.just(createFetchPacket());
        this.phase = Phase.READ_FETCH_RESPONSE;
    }


    /**
     * @see #readResultSet(ByteBuf, Consumer)
     * @see <a href="https://dev.mysql.com/doc/dev/mysql-server/latest/page_protocol_com_stmt_fetch.html">Protocol::COM_STMT_FETCH</a>
     */
    private ByteBuf createFetchPacket() {
        assertPhase(Phase.FETCH_STMT);
        final DownstreamSink downstreamSink = this.downstreamSink;
        if (!(downstreamSink instanceof FetchAbleDownstreamSink)) {
            throw new IllegalStateException(String.format("%s isn't fetch able downstream.", downstreamSink));
        }

        ByteBuf packet = this.adjutant.allocator().buffer(13);
        PacketUtils.writeInt3(packet, 9);
        packet.writeByte(addAndGetSequenceId());

        packet.writeByte(PacketUtils.COM_STMT_FETCH);
        PacketUtils.writeInt4(packet, this.statementId);
        PacketUtils.writeInt4(packet, ((FetchAbleDownstreamSink) downstreamSink).getFetchSize());
        return packet;
    }

    /**
     * @see #readExecuteResponse(ByteBuf, Consumer)
     * @see <a href="https://dev.mysql.com/doc/dev/mysql-server/latest/page_protocol_com_stmt_reset.html">Protocol::COM_STMT_RESET</a>
     */
    private ByteBuf createResetPacket() {
        assertPhase(Phase.RESET_STMT);

        final DownstreamSink downStreamSink = this.downstreamSink;
        if (!(downStreamSink instanceof BatchUpdateSink)) {
            throw new IllegalStateException(String.format(
                    "this.downstreamSink[%s] isn't %s,reject COM_STMT_RESET command."
                    , this.downstreamSink, BatchUpdateSink.class.getSimpleName()));
        }
        if (!((BatchUpdateSink<?>) downStreamSink).hasMoreGroup()) {
            throw new IllegalStateException("Batch update have ended");
        }
        ByteBuf packet = this.adjutant.allocator().buffer(9);

        PacketUtils.writeInt3(packet, 5);
        packet.writeByte(0);// use 0 sequence id

        packet.writeByte(PacketUtils.COM_STMT_RESET);
        PacketUtils.writeInt4(packet, this.statementId);
        return packet;
    }


    /**
     * @see #readPrepareColumnMeta(ByteBuf, Consumer)
     * @see #readExecuteResponse(ByteBuf, Consumer)
     * @see #readPrepareResponse(ByteBuf)
     */
    private void resetColumnMeta(final int columnCount) {
        if (columnCount == 0) {
            this.prepareColumnMetas = MySQLColumnMeta.EMPTY;
        } else {
            this.prepareColumnMetas = new MySQLColumnMeta[columnCount];
        }
        this.columnMetaIndex = 0;
    }

    /**
     * @see #readPrepareParameterMeta(ByteBuf, Consumer) (ByteBuf)
     * @see #readPrepareResponse(ByteBuf)
     */
    private void resetParameterMetas(final int parameterCount) {
        if (parameterCount == 0) {
            this.parameterMetas = MySQLColumnMeta.EMPTY;
        } else {
            this.parameterMetas = new MySQLColumnMeta[parameterCount];
        }
        this.parameterMetaIndex = 0;
    }


    /**
     * @see #decode(ByteBuf, Consumer)
     */
    private JdbdException createException() {
        final List<JdbdException> errorList = this.errorList;
        if (errorList == null || errorList.isEmpty()) {
            throw new IllegalStateException("No error.");
        }
        JdbdException e;
        if (errorList.size() == 1) {
            e = errorList.get(0);
        } else {
            e = new JdbdCompositeException(errorList
                    , "MultiResults read occur multi error,the first error[%s]", errorList.get(0).getMessage());
        }
        return e;
    }

    /**
     * @see #executeUpdate(ParamWrapper)
     */
    private void doPreparedUpdate(final ParamWrapper wrapper, final MonoSink<ResultStates> sink) {
        doPreparedExecute(sink::error, () -> new UpdateDownstreamSink(wrapper, sink));
    }

    /**
     * @see #executeQuery(ParamWrapper)
     */
    private void doPreparedQuery(final ParamWrapper wrapper, final FluxSink<ResultRow> sink) {
        doPreparedExecute(sink::error, () -> new QueryDownstreamSink(wrapper, sink));
    }

    /**
     * @see #executeBatch(BatchWrapper)
     */
    private void doPreparedBatchUpdate(BatchWrapper<? extends ParamValue> wrapper, FluxSink<ResultStates> sink) {
        doPreparedExecute(sink::error, () -> new BatchUpdateSink<>(wrapper, sink));
    }


    /**
     * @see #doPreparedUpdate(ParamWrapper, MonoSink)
     * @see #doPreparedQuery(ParamWrapper, FluxSink)
     * @see #doPreparedBatchUpdate(BatchWrapper, FluxSink)
     */
    private void doPreparedExecute(final Consumer<Throwable> errorConsumer, final Supplier<DownstreamSink> supplier) {
        final DownstreamSink downstreamSink = this.downstreamSink;

        if (this.phase == Phase.CLOSE_STMT) {
            errorConsumer.accept(new IllegalStateException
                    (String.format("%s closed.", PreparedStatement.class.getSimpleName())));

        } else if (!(downstreamSink instanceof DownstreamAdapter)) {
            errorConsumer.accept(new IllegalStateException(
                    String.format("%s isn't %s.", downstreamSink, DownstreamAdapter.class.getSimpleName())));
        } else {
            final DownstreamAdapter downstreamAdapter = (DownstreamAdapter) downstreamSink;
            try {

                if (downstreamAdapter.downstreamSink != null) {
                    throw new IllegalStateException(String.format("%s downstreamSink duplicate.", this));
                }
                final DownstreamSink actualDownstreamSink = supplier.get();
                if (actualDownstreamSink instanceof DownstreamAdapter) {
                    throw new IllegalArgumentException(String.format("downstreamSink type[%s] error."
                            , downstreamSink.getClass().getSimpleName()));
                }
                if (ComPreparedTask.this.phase != Phase.WAIT_PARAMS) {
                    throw new IllegalStateException(String.format("%s is executing ,reject execute again."
                            , ComPreparedTask.class.getSimpleName()));
                }

                downstreamAdapter.downstreamSink = actualDownstreamSink;
                this.phase = Phase.EXECUTE;
                if (executeStatement()) {
                    this.phase = Phase.CLOSE_STMT;
                    this.packetPublisher = Mono.just(createCloseStatementPacket());
                } else {
                    this.phase = Phase.READ_EXECUTE_RESPONSE;
                }
            } catch (Throwable e) {
                this.phase = Phase.CLOSE_STMT;
                this.packetPublisher = Mono.just(createCloseStatementPacket());
                if (e instanceof IllegalStateException) {
                    errorConsumer.accept(e);
                } else {
                    errorConsumer.accept(MySQLExceptions.wrap(e));
                }
            }

            this.sendPacketSignal(this.phase == Phase.CLOSE_STMT)
                    .doOnError(errorConsumer)
                    .subscribe();

        }


    }


    private void assertPhase(Phase expectedPhase) {
        if (this.phase != expectedPhase) {
            throw new IllegalStateException(String.format("this.phase isn't %s.", expectedPhase));
        }
    }

    /*################################## blow private static method ##################################*/



    /*################################## blow private static inner class ##################################*/


    private interface DownstreamSink {

        /**
         * @return true execute task occur error,task end.
         */
        boolean executeCommand();

        /**
         * @return true : task end.
         */
        boolean nextUpdate(ResultStates states);

        /**
         * @return true : task end.
         */
        boolean readResultSet(final ByteBuf cumulateBuffer, final Consumer<Object> serverStatusConsumer);

        void error(JdbdException e);

        void complete();
    }

    private interface BatchDownstreamSink extends DownstreamSink {

        boolean hasMoreGroup();

        void resetSuccess();

    }

    private interface FetchAbleDownstreamSink extends DownstreamSink {

        int getFetchSize();
    }


    private class QueryDownstreamSink implements FetchAbleDownstreamSink, ResultRowSink {

        private final ResultSetReader resultSetReader;

        private final List<? extends ParamValue> parameterGroup;

        private final FluxSink<ResultRow> sink;

        private final Consumer<ResultStates> statesConsumer;

        private final int fetchSize;

        private ResultStates resultStates;

        /**
         * @see ComPreparedTask#ComPreparedTask(ParamWrapper, FluxSink, MySQLTaskAdjutant)
         */
        private QueryDownstreamSink(ParamWrapper wrapper, FluxSink<ResultRow> sink) {
            if (ComPreparedTask.this.properties.getOrDefault(PropertyKey.useCursorFetch, Boolean.class)
                    && Capabilities.supportPsMultiResult(ComPreparedTask.this.negotiatedCapability)) {
                // we only create cursor-backed result sets if
                // a) The query is a SELECT
                // b) The server supports it
                // c) We know it is forward-only (note this doesn't preclude updatable result sets)
                // d) The user has set a fetch size
                this.fetchSize = wrapper.getFetchSize();
            } else {
                this.fetchSize = -1;
            }

            this.resultSetReader = ResultSetReaderBuilder
                    .builder()

                    .rowSink(this)
                    .adjutant(ComPreparedTask.this.adjutant)
                    .fetchResult(this.fetchSize > 0)
                    .sequenceIdUpdater(ComPreparedTask.this::updateSequenceId)

                    .errorConsumer(ComPreparedTask.this::addError)
                    .resettable(this.fetchSize > 0)
                    .build(BinaryResultSetReader.class);

            this.parameterGroup = wrapper.getParamGroup();
            this.sink = sink;
            this.statesConsumer = wrapper.getStatesConsumer();
        }

        @Override
        public boolean executeCommand() {
            if (this.resultStates != null) {
                // here bug.
                throw new IllegalStateException(String.format("%s have ended.", this));
            }
            MySQLColumnMeta[] columnMetaArray = Objects.requireNonNull(
                    ComPreparedTask.this.prepareColumnMetas, "ComPreparedTask.this.prepareColumnMetas");
            boolean taskEnd;
            if (columnMetaArray.length == 0) {
                ComPreparedTask.this.addError(new ErrorSubscribeException(ResultType.QUERY, ResultType.UPDATE));
                taskEnd = true;
            } else {
                PrepareExecuteCommandWriter writer = new PrepareExecuteCommandWriter(ComPreparedTask.this);
                taskEnd = ComPreparedTask.this.sendExecuteCommand(writer, -1, this.parameterGroup);
            }
            return taskEnd;
        }

        @Override
        public boolean nextUpdate(ResultStates states) {
            throw new IllegalStateException(
                    String.format("%s can't read ResultStates ,check executeCommand() method.", this));
        }

        @Override
        public boolean readResultSet(final ByteBuf cumulateBuffer, final Consumer<Object> serverStatusConsumer) {
            final ResultStates lastResultStates = this.resultStates;
            final boolean taskEnd;
            if (this.resultSetReader.read(cumulateBuffer, serverStatusConsumer)) {
                final ResultStates resultStates = this.resultStates;
                if (resultStates == null || resultStates == lastResultStates) {
                    throw new IllegalStateException(String.format("%s not invoke %s.accept(ResultStates) method."
                            , this.resultSetReader, ResultRowSink.class.getName()));
                }
                if (resultStates.hasMoreResults() || this.isCancelled()) {
                    taskEnd = false;
                } else if (resultStates.hasMoreFetch()) {
                    taskEnd = false;
                    ComPreparedTask.this.sendFetchCommand();
                } else {
                    taskEnd = true;
                }
            } else {
                taskEnd = false;
            }
            return taskEnd;
        }

        @Override
        public int getFetchSize() {
            return this.fetchSize;
        }

        @Override
        public void error(JdbdException e) {
            this.sink.error(e);
        }

        @Override
        public void complete() {
            ResultStates resultStates = Objects.requireNonNull(this.resultStates, "this.resultStates");
            try {
                this.statesConsumer.accept(resultStates);
                this.sink.complete();
            } catch (Throwable e) {
                this.sink.error(new ResultStateConsumerException(e, "%s consumer occur error."
                        , ResultStates.class.getName()));
            }
        }

        /**
         * @see ResultRowSink#next(ResultRow)
         */
        @Override
        public void next(ResultRow resultRow) {
            this.sink.next(resultRow);
        }

        /**
         * @see ResultRowSink#isCancelled()
         */
        @Override
        public boolean isCancelled() {
            return ComPreparedTask.this.hasError() || this.sink.isCancelled();
        }

        /**
         * @see ResultRowSink#accept(ResultStates)
         */
        @Override
        public void accept(final ResultStates resultStates) {
            if (this.resultStates != null) {
                throw new IllegalStateException(String.format("%s this.resultStates duplicate.", this));
            }
            this.resultStates = resultStates;
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }

    }

    private final class UpdateDownstreamSink implements DownstreamSink {

        private final List<? extends ParamValue> parameterGroup;

        private final MonoSink<ResultStates> sink;

        private ResultStates resultStates;

        /**
         * @see #ComPreparedTask(ParamWrapper, MonoSink, MySQLTaskAdjutant)
         */
        private UpdateDownstreamSink(ParamWrapper wrapper, MonoSink<ResultStates> sink) {
            this.parameterGroup = wrapper.getParamGroup();
            this.sink = sink;
        }

        @Override
        public boolean executeCommand() {
            if (this.resultStates != null) {
                // here bug.
                throw new IllegalStateException(String.format("%s have ended.", this));
            }
            boolean taskEnd;
            if (hasReturnColumns()) {
                ComPreparedTask.this.addError(new ErrorSubscribeException(ResultType.UPDATE, ResultType.QUERY));
                taskEnd = true;
            } else {
                PrepareExecuteCommandWriter writer = new PrepareExecuteCommandWriter(ComPreparedTask.this);
                taskEnd = ComPreparedTask.this.sendExecuteCommand(writer, -1, this.parameterGroup);
            }
            return taskEnd;
        }

        @Override
        public boolean nextUpdate(ResultStates states) {
            if (this.resultStates != null) {
                throw new IllegalStateException(String.format("%s ResultStates duplicate.", this));
            }
            this.resultStates = states;
            return true;
        }

        @Override
        public boolean readResultSet(ByteBuf cumulateBuffer, Consumer<Object> serverStatusConsumer) {
            throw new IllegalStateException(
                    String.format("%s can't read result set ,check executeCommand() method.", this));
        }

        @Override
        public void error(JdbdException e) {
            this.sink.error(e);
        }

        public void success(final ResultStates resultStates) {
            this.resultStates = resultStates;
        }

        @Override
        public void complete() {
            this.sink.success(Objects.requireNonNull(this.resultStates, "this.resultStates"));
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }


    }

    private final class BatchUpdateSink<T extends ParamValue> implements BatchDownstreamSink {

        private final List<List<T>> groupList;

        private int index = 0;

        private final FluxSink<ResultStates> sink;

        private final ExecuteCommandWriter commandWriter;

        private boolean lastHasLongData;

        /**
         * @see #ComPreparedTask(FluxSink, BatchWrapper, MySQLTaskAdjutant)
         */
        private BatchUpdateSink(BatchWrapper<T> wrapper, FluxSink<ResultStates> sink) {
            this.groupList = wrapper.getParamGroupList();
            this.sink = sink;
            this.commandWriter = new PrepareExecuteCommandWriter(ComPreparedTask.this);
        }

        @Override
        public boolean executeCommand() {
            final int currentIndex = this.index;
            if (currentIndex >= this.groupList.size()) {
                // here bug.
                throw new IllegalStateException(String.format("%s have ended.", this));
            }

            if (currentIndex == 0 && hasReturnColumns()) {
                ComPreparedTask.this.addError(new ErrorSubscribeException(
                        ResultType.BATCH_UPDATE, ResultType.QUERY));
                return true;
            }
            if (this.lastHasLongData) {
                throw new IllegalStateException(
                        String.format("%s last group has long data,reject execute command.", this));
            }
            final int groupIndex = this.index++;
            final List<T> group = this.groupList.get(groupIndex);
            this.lastHasLongData = BindUtils.hasLongData(group);
            return ComPreparedTask.this.sendExecuteCommand(this.commandWriter, groupIndex, group);
        }

        @Override
        public boolean nextUpdate(ResultStates states) {
            this.sink.next(states);
            boolean taskEnd;
            if (this.hasMoreGroup()) {
                taskEnd = false;
                if (this.lastHasLongData) {
                    ComPreparedTask.this.sendResetCommand();
                }
            } else {
                taskEnd = true;
            }
            return taskEnd;
        }

        @Override
        public void resetSuccess() {
            if (!this.lastHasLongData) {
                throw new IllegalStateException(
                        String.format("%s lastHasLongData is false ,reject update status.", this));
            }
            this.lastHasLongData = false;
        }

        @Override
        public boolean readResultSet(ByteBuf cumulateBuffer, Consumer<Object> serverStatusConsumer) {
            throw new IllegalStateException(
                    String.format("%s can't read result set ,check executeCommand() method.", this));
        }

        @Override
        public void error(JdbdException e) {
            this.sink.error(e);
        }

        /**
         * @return true ,has more,reset statement.
         */
        public boolean next(final ResultStates resultStates) {
            this.sink.next(resultStates);
            return hasMoreGroup();
        }

        @Override
        public boolean hasMoreGroup() {
            return this.index < this.groupList.size();
        }

        @Override
        public void complete() {
            if (this.index == this.groupList.size()) {
                this.sink.complete();
            } else {
                String message = String.format(
                        "%s execute error,current index[%s] group size[%s]", this, this.index, this.groupList.size());
                throw new IllegalStateException(message);
            }

        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }


    }// BatchUpdateSink class


    private final class MultiResultDownstreamSink implements DownstreamSink {

        private final MultiResultSink sink;

        /**
         * @see ComPreparedTask#ComPreparedTask(ParamWrapper, MultiResultSink, MySQLTaskAdjutant)
         */
        private MultiResultDownstreamSink(ParamWrapper wrapper, MultiResultSink sink) {
            this.sink = sink;
        }

        @Override
        public boolean executeCommand() {
            return false;
        }

        @Override
        public boolean nextUpdate(ResultStates states) {
            return false;
        }

        @Override
        public boolean readResultSet(ByteBuf cumulateBuffer, Consumer<Object> serverStatusConsumer) {
            return false;
        }

        @Override
        public void error(JdbdException e) {

        }

        @Override
        public void complete() {

        }
    }


    private final class BatchMultiResultDownstreamSink<T extends ParamValue> implements DownstreamSink {

        private final FluxSink<ReactorMultiResult> sink;

        private final List<List<T>> groupList;

        public BatchMultiResultDownstreamSink(BatchWrapper<T> wrapper, FluxSink<ReactorMultiResult> sink) {
            this.sink = sink;
            this.groupList = wrapper.getParamGroupList();
        }

        @Override
        public boolean executeCommand() {
            return false;
        }

        @Override
        public boolean nextUpdate(ResultStates states) {
            return false;
        }

        @Override
        public boolean readResultSet(ByteBuf cumulateBuffer, Consumer<Object> serverStatusConsumer) {
            return false;
        }

        @Override
        public void error(JdbdException e) {

        }

        @Override
        public void complete() {

        }
    }


    private final class DownstreamAdapter implements FetchAbleDownstreamSink, BatchDownstreamSink {

        private final MonoSink<PreparedStatement> sink;

        //non-volatile,because update once and don't update again after emit PreparedStatement.
        private int warnings = -1;

        /**
         * @see #doPreparedExecute(Consumer, Supplier)
         */
        private DownstreamSink downstreamSink;

        /**
         * @see #ComPreparedTask(MySQLTaskAdjutant, MonoSink, StmtWrapper)
         */
        private DownstreamAdapter(MonoSink<PreparedStatement> sink) {
            this.sink = sink;
        }

        private void emitStatement() {
            if (this.warnings < 0) {
                throw new IllegalStateException("warnings not set.");
            }
            this.sink.success(ServerPreparedStatement.create(ComPreparedTask.this));
        }


        @Override
        public boolean executeCommand() {
            return Objects.requireNonNull(this.downstreamSink, "this.downstreamSink")
                    .executeCommand();
        }

        @Override
        public boolean nextUpdate(ResultStates states) {
            return Objects.requireNonNull(this.downstreamSink, "this.downstreamSink")
                    .nextUpdate(states);
        }

        @Override
        public boolean readResultSet(ByteBuf cumulateBuffer, Consumer<Object> serverStatusConsumer) {
            return Objects.requireNonNull(this.downstreamSink, "this.downstreamSink")
                    .readResultSet(cumulateBuffer, serverStatusConsumer);
        }

        @Override
        public void error(JdbdException e) {
            if (this.downstreamSink == null) {
                this.sink.error(e);
            } else {
                this.downstreamSink.error(e);
            }
        }

        @Override
        public void complete() {
            Objects.requireNonNull(this.downstreamSink, "this.downstreamSink")
                    .complete();
        }

        @Override
        public boolean hasMoreGroup() {
            final DownstreamSink downstreamSink = Objects.requireNonNull(this.downstreamSink, "this.downstreamSink");
            if (!(downstreamSink instanceof BatchDownstreamSink)) {
                throw new IllegalStateException(
                        String.format("%s isn't %s instance.", this, BatchDownstreamSink.class.getSimpleName()));
            }
            return ((BatchDownstreamSink) downstreamSink).hasMoreGroup();
        }

        @Override
        public void resetSuccess() {
            final DownstreamSink downstreamSink = Objects.requireNonNull(this.downstreamSink, "this.downstreamSink");
            if (!(downstreamSink instanceof BatchDownstreamSink)) {
                throw new IllegalStateException(
                        String.format("%s isn't %s instance.", this, BatchDownstreamSink.class.getSimpleName()));
            }
            ((BatchDownstreamSink) downstreamSink).resetSuccess();
        }

        @Override
        public int getFetchSize() {
            final DownstreamSink downstreamSink = Objects.requireNonNull(this.downstreamSink, "this.downstreamSink");
            final int fetchSize;
            if (downstreamSink instanceof FetchAbleDownstreamSink) {
                fetchSize = ((FetchAbleDownstreamSink) downstreamSink).getFetchSize();
            } else {
                fetchSize = 0;
            }
            return fetchSize;
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }


    }// DownstreamAdapter class


    enum Phase {
        PREPARED,
        READ_PREPARE_RESPONSE,
        READ_PREPARE_PARAM_META,
        READ_PREPARE_COLUMN_META,

        WAIT_PARAMS,

        EXECUTE,
        READ_EXECUTE_RESPONSE,
        READ_RESULT_SET,

        RESET_STMT,
        READ_RESET_RESPONSE,

        FETCH_STMT,
        READ_FETCH_RESPONSE,


        CLOSE_STMT
    }


}
