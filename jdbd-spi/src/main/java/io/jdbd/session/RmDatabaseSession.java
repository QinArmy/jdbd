package io.jdbd.session;

import io.jdbd.JdbdException;
import io.jdbd.lang.Nullable;
import io.jdbd.util.NameMode;
import org.reactivestreams.Publisher;

import java.util.Optional;
import java.util.function.Function;

/**
 * <p>
 * This interface representing database session that support XA transaction.
 *<br/>
 * <p>
 * This interface is is similar to {@code javax.sql.javax.sql.XAConnection} and {@code javax.transaction.xa.XAResource}, except that this interface is reactive.
 *<br/>
 * <p>
 * The 'Rm' of the name of this interface means Resource Manager of XA transaction.
 *<br/>
 * <p>
 * The instance of this interface is created by {@link DatabaseSessionFactory#rmSession()}.
 *<br/>
 * <p>
 * This interface extends {@link DatabaseSession} for support XA interface based on
 * the X/Open CAE Specification (Distributed Transaction Processing: The XA Specification).
 * This document is published by The Open Group and available at
 * <a href="http://www.opengroup.org/public/pubs/catalog/c193.htm">The XA Specification</a>,
 * here ,you can download the pdf about The XA Specification.
 *<br/>
 * <p>
 * Application developer can create statement by following methods :
 *     <ul>
 *         <li>{@link #statement()} ,create static statement.</li>
 *         <li>{@link #prepareStatement(String)} , create server-prepare statement</li>
 *         <li>{@link #bindStatement(String)} , create the adaptor of client-prepared statement and server-prepared statement.</li>
 *         <li>{@link #bindStatement(String, boolean)}, create the adaptor of client-prepared statement and server-prepared statement.</li>
 *         <li>{@link #multiStatement()}, create multi-statement</li>
 *     </ul>
 *<br/>
 * <p>
 * Application developer can control XA transaction by following :
 *     <ul>
 *         <li>{@link #start(Xid, int)}</li>
 *         <li>{@link #start(Xid, int, TransactionOption)}</li>
 *         <li>{@link #end(Xid, int)}</li>
 *         <li>{@link #end(Xid, int, Function)}</li>
 *         <li>{@link #prepare(Xid)}</li>
 *         <li>{@link #prepare(Xid, Function)}</li>
 *         <li>{@link #commit(Xid, int)}</li>
 *         <li>{@link #commit(Xid, int, Function)}</li>
 *         <li>{@link #rollback(Xid)}</li>
 *         <li>{@link #rollback(Xid, Function)}</li>
 *         <li>{@link #forget(Xid)}</li>
 *         <li>{@link #forget(Xid, Function)}</li>
 *         <li>{@link #recover(int)}</li>
 *         <li>{@link #recover(int, Function)}</li>
 *         <li>{@link #inTransaction()}</li>
 *         <li>{@link #isSupportForget()}</li>
 *         <li>{@link #startSupportFlags()}</li>
 *         <li>{@link #endSupportFlags()}</li>
 *         <li>{@link #recoverSupportFlags()}</li>
 *         <li>{@link #transactionInfo()}</li>
 *         <li>{@link #setTransactionCharacteristics(TransactionOption)}</li>
 *         <li>{@link #setSavePoint()}</li>
 *         <li>{@link #setSavePoint(String)}</li>
 *         <li>{@link #setSavePoint(String, java.util.function.Function)}</li>
 *         <li>{@link #releaseSavePoint(SavePoint)}</li>
 *         <li>{@link #releaseSavePoint(SavePoint, java.util.function.Function)}</li>
 *         <li>{@link #rollbackToSavePoint(SavePoint)}</li>
 *         <li>{@link #rollbackToSavePoint(SavePoint, java.util.function.Function)}</li>
 *     </ul>
 *<br/>
 *
 * @see <a href="http://www.opengroup.org/public/pubs/catalog/c193.html">The XA Specification</a>
 */
public interface RmDatabaseSession extends DatabaseSession {

    /**
     * Use TM_NO_FLAGS to indicate no flags value is selected.
     */
    int TM_NO_FLAGS = 0;

    /**
     * Caller is joining existing transaction branch.
     */
    int TM_JOIN = 1 << 21;

    /**
     * Ends a recovery scan.
     */
    int TM_END_RSCAN = 1 << 23;

    /**
     * Starts a recovery scan.
     */
    int TM_START_RSCAN = 1 << 24;

    /**
     * Caller is suspending (not ending) its association with
     * a transaction branch.
     */
    int TM_SUSPEND = 1 << 25;

    /**
     * Disassociates caller from a transaction branch.
     */
    int TM_SUCCESS = 1 << 26;

    /**
     * Caller is resuming association with a suspended
     * transaction branch.
     */
    int TM_RESUME = 1 << 27;

    /**
     * Disassociates the caller and marks the transaction branch
     * rollback-only.
     */
    int TM_FAIL = 1 << 29;

    /**
     * Caller is using one-phase optimization.
     */
    int TM_ONE_PHASE = 1 << 30;

    /**
     * The transaction branch has been read-only and has been committed.
     */
    int XA_RDONLY = 3;

    /**
     * The transaction work has been prepared normally.
     */
    int XA_OK = 0;


    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // session is instance of {@link RmDatabaseSession}
     *             session.start(xid,RmDatabaseSession.TM_NO_FLAGS,TransactionOption.option(null,false)) ;
     *         </code>
     *     </pre>
     * <br/>
     *
     * @see #start(Xid, int, TransactionOption)
     */
    Publisher<RmDatabaseSession> start(Xid xid);

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // session is instance of {@link RmDatabaseSession}
     *             session.start(xid,flags,TransactionOption.option(null,false)) ;
     *         </code>
     *     </pre>
     *<br/>
     *
     * @see #start(Xid, int, TransactionOption)
     */
    Publisher<RmDatabaseSession> start(Xid xid, int flags);

    /**
     * <p>
     * Starts work on behalf of a transaction branch specified in
     * <code>xid</code>.
     * If {@link #TM_JOIN} is specified, the start applies to joining a transaction
     * previously seen by the resource manager. If {@link #TM_RESUME} is specified,
     * the start applies to resuming a suspended transaction specified in the
     * parameter <code>xid</code>.
     * If neither {@link #TM_JOIN} nor {@link #TM_RESUME} is specified and the transaction
     * specified by <code>xid</code> has previously been seen by the resource
     * manager, the resource manager throws the XAException exception with
     * {@link XaException#XAER_DUPID} error code.
     *<br/>
     * <p>
     * To be safe,{@link RmDatabaseSession} write gtrid and bqual as hex strings. steps :
     * <ul>
     *     <li>Get byte[] with {@link java.nio.charset.StandardCharsets#UTF_8}</li>
     *     <li>write gtrid or bqual as hex strings</li>
     * </ul>
     *<br/>
     *
     * @param flags bit set, support below flags:
     *              <ul>
     *                  <li>{@link #TM_NO_FLAGS}</li>
     *                  <li>{@link #TM_JOIN} Note that this flag cannot be used in conjunction with {@link #TM_RESUME}</li>
     *                  <li>{@link #TM_RESUME} Note that this flag cannot be used in conjunction with {@link #TM_JOIN}</li>
     *              </ul>
     * @return a Publisher that only emitting an element or error.The element is this instance.
     * @throws io.jdbd.JdbdException emit(not throw),when
     *                               <ul>
     *                                  <li>xid is null</li>
     *                                  <li>{@link Xid#getGtrid()} have no text</li>
     *                                  <li>{@link Xid#getBqual()} non-null and have no text</li>
     *                                  <li>database don't support appropriate flag</li>
     *                                  <li>transaction states error,see {@link XaStates}</li>
     *                                  <li>database server response error message , see {@link io.jdbd.result.ServerException}</li>
     *                               </ul>
     */
    Publisher<RmDatabaseSession> start(Xid xid, int flags, TransactionOption option);


    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // session is instance of {@link RmDatabaseSession}
     *             session.end(xid,RmDatabaseSession.TM_SUCCESS,option -> null) ;
     *         </code>
     *     </pre>
     *<br/>
     *
     * @see #end(Xid, int, Function)
     */
    Publisher<RmDatabaseSession> end(Xid xid);

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // session is instance of {@link RmDatabaseSession}
     *             session.end(xid,flags,option -> null) ;
     *         </code>
     *     </pre>
     *<br/>
     *
     * @see #end(Xid, int, Function)
     */
    Publisher<RmDatabaseSession> end(Xid xid, int flags);

    /**
     * <p>
     * Ends the work performed on behalf of a transaction branch.
     * The resource manager disassociates the XA resource from the
     * transaction branch specified and lets the transaction
     * complete.
     *<br/>
     * <p>
     * To be safe,{@link RmDatabaseSession} write gtrid and bqual as hex strings. steps :
     * <ul>
     *     <li>Get byte[] with {@link java.nio.charset.StandardCharsets#UTF_8}</li>
     *     <li>write gtrid or bqual as hex strings</li>
     * </ul>
     *<br/>
     *
     * @param flags      bit set, support one of following :
     *                   <ul>
     *                       <li>{@link #TM_SUCCESS} s specified, the portion of work has completed successfully.</li>
     *                       <li>{@link #TM_FAIL} is specified, the portion of work has failed.<br/>
     *                       The resource manager may mark the transaction as rollback-only
     *                       </li>
     *                       <li>{@link #TM_SUSPEND} , the transaction branch is temporarily suspended in an incomplete state.<br/>
     *                       The transaction context is in a suspended state and must be resumed via the <code>start</code><br/>
     *                       method with {@link #TM_RESUME} specified.<br/>
     *                       </li>
     *                   </ul>
     * @param optionFunc dialect option ,empty or option.
     * @throws XaException emit(not throw) when
     *                     <ul>
     *                          <li>xid is null</li>
     *                          <li>{@link Xid#getGtrid()} have no text</li>
     *                          <li>{@link Xid#getBqual()} non-null and have no text</li>
     *                          <li>driver don't support appropriate flags</li>
     *                          <li>driver don't support optionMap</li>
     *                          <li>transaction states error,see {@link XaStates}</li>
     *                          <li>server response error message,see {@link io.jdbd.result.ServerException}</li>
     *                     </ul>
     */
    Publisher<RmDatabaseSession> end(Xid xid, int flags, Function<Option<?>, ?> optionFunc);


    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // session is instance of {@link RmDatabaseSession}
     *             session.prepare(xid,option -> null) ;
     *         </code>
     *     </pre>
     *<br/>
     *
     * @see #prepare(Xid, Function)
     */
    Publisher<Integer> prepare(Xid xid);

    /**
     * <p>
     * To be safe,{@link RmDatabaseSession} write gtrid and bqual as hex strings. steps :
     * <ul>
     *     <li>Get byte[] with {@link java.nio.charset.StandardCharsets#UTF_8}</li>
     *     <li>write gtrid or bqual as hex strings</li>
     * </ul>
     *<br/>
     *
     * @param xid        non-null
     * @param optionFunc optionMap dialect option ,empty or option map.
     * @throws XaException emit(not throw) when
     *                     <ul>
     *                          <li>xid is null</li>
     *                          <li>{@link Xid#getGtrid()} have no text</li>
     *                          <li>{@link Xid#getBqual()} non-null and have no text</li>
     *                          <li>driver don't support optionMap</li>
     *                          <li>transaction states error,see {@link XaStates}</li>
     *                          <li>server response error message,see {@link io.jdbd.result.ServerException}</li>
     *                     </ul>
     */
    Publisher<Integer> prepare(Xid xid, Function<Option<?>, ?> optionFunc);


    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // session is instance of {@link RmDatabaseSession}
     *             session.commit(xid,RmDatabaseSession.TM_NO_FLAGS,option -> null) ;
     *         </code>
     *     </pre>
     *<br/>
     *
     * @see #commit(Xid, int, Function)
     */
    Publisher<RmDatabaseSession> commit(Xid xid);

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // session is instance of {@link RmDatabaseSession}
     *             session.commit(xid,flags,option -> null) ;
     *         </code>
     *     </pre>
     *<br/>
     *
     * @see #commit(Xid, int, Function)
     */
    Publisher<RmDatabaseSession> commit(Xid xid, int flags);


    /**
     * <p>
     * To be safe,{@link RmDatabaseSession} write gtrid and bqual as hex strings. steps :
     * <ul>
     *     <li>Get byte[] with {@link java.nio.charset.StandardCharsets#UTF_8}</li>
     *     <li>write gtrid or bqual as hex strings</li>
     * </ul>
     *<br/>
     *
     * @param xid        non-null
     * @param flags      one of following :
     *                   <ul>
     *                     <li>{@link #TM_NO_FLAGS}</li>
     *                     <li>{@link #TM_ONE_PHASE}</li>
     *                   </ul>
     * @param optionFunc optionMap dialect option ,empty or option map.
     * @throws XaException emit(not throw) when
     *                     <ul>
     *                          <li>xid is null</li>
     *                          <li>{@link Xid#getGtrid()} have no text</li>
     *                          <li>{@link Xid#getBqual()} non-null and have no text</li>
     *                          <li>driver don't support optionMap</li>
     *                          <li>transaction states error,see {@link XaStates}</li>
     *                          <li>server response error message,see {@link io.jdbd.result.ServerException}</li>
     *                     </ul>
     */
    Publisher<RmDatabaseSession> commit(Xid xid, int flags, Function<Option<?>, ?> optionFunc);


    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // session is instance of {@link RmDatabaseSession}
     *             session.rollback(xid,option -> null) ;
     *         </code>
     *     </pre>
     *<br/>
     *
     * @see #rollback(Xid, Function)
     */
    Publisher<RmDatabaseSession> rollback(Xid xid);

    /**
     * <p>
     * To be safe,{@link RmDatabaseSession} write gtrid and bqual as hex strings. steps :
     * <ul>
     *     <li>Get byte[] with {@link java.nio.charset.StandardCharsets#UTF_8}</li>
     *     <li>write gtrid or bqual as hex strings</li>
     * </ul>
     *<br/>
     *
     * @param xid        non-null
     * @param optionFunc optionMap dialect option ,empty or option map.
     * @throws XaException emit(not throw) when
     *                     <ul>
     *                          <li>xid is null</li>
     *                          <li>{@link Xid#getGtrid()} have no text</li>
     *                          <li>{@link Xid#getBqual()} non-null and have no text</li>
     *                          <li>driver don't support optionMap</li>
     *                          <li>transaction states error,see {@link XaStates}</li>
     *                          <li>server response error message,see {@link io.jdbd.result.ServerException}</li>
     *                     </ul>
     */
    Publisher<RmDatabaseSession> rollback(Xid xid, Function<Option<?>, ?> optionFunc);


    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // session is instance of {@link RmDatabaseSession}
     *             session.forget(xid,option -> null) ;
     *         </code>
     *     </pre>
     *<br/>
     *
     * @see #forget(Xid, Function)
     */
    Publisher<RmDatabaseSession> forget(Xid xid);

    /**
     * <p>
     * To be safe,{@link RmDatabaseSession} write gtrid and bqual as hex strings. steps :
     * <ul>
     *     <li>Get byte[] with {@link java.nio.charset.StandardCharsets#UTF_8}</li>
     *     <li>write gtrid or bqual as hex strings</li>
     * </ul>
     *<br/>
     *
     * @param xid        non-null
     * @param optionFunc optionMap dialect option ,empty or option map.
     * @throws XaException emit(not throw) when
     *                     <ul>
     *                          <li>driver don't support this method, see {@link #isSupportForget()}</li>
     *                          <li>xid is null</li>
     *                          <li>{@link Xid#getGtrid()} have no text</li>
     *                          <li>{@link Xid#getBqual()} non-null and have no text</li>
     *                          <li>driver don't support optionMap</li>
     *                          <li>transaction states error,see {@link XaStates}</li>
     *                          <li>server response error message,see {@link io.jdbd.result.ServerException}</li>
     *                     </ul>
     */
    Publisher<RmDatabaseSession> forget(Xid xid, Function<Option<?>, ?> optionFunc);

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // session is instance of {@link RmDatabaseSession}
     *             session.recover(RmDatabaseSession.TM_NO_FLAGS,option -> null) ;
     *         </code>
     *     </pre>
     *<br/>
     *
     * @see #recover(int, Function)
     */
    Publisher<Optional<Xid>> recover();

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // session is instance of {@link RmDatabaseSession}
     *             session.recover(flags,option -> null) ;
     *         </code>
     *     </pre>
     *<br/>
     *
     * @see #recover(int, Function)
     */
    Publisher<Optional<Xid>> recover(int flags);

    /**
     * <p>
     * To be safe,{@link RmDatabaseSession} <strong>possibly</strong> write gtrid and bqual as hex strings. steps :
     * <ul>
     *     <li>Get byte[] of trid ( or bqual) with {@link java.nio.charset.StandardCharsets#UTF_8}</li>
     *     <li>write gtrid ( or bqual) as hex strings</li>
     * </ul>
     * so the conversion process of this method is the reverse of above.
     *<br/>
     *
     * @param flags      one of following :
     *                   <ul>
     *                     <li>{@link #TM_NO_FLAGS}</li>
     *                     <li>{@link #TM_START_RSCAN}</li>
     *                     <li>{@link #TM_END_RSCAN}</li>
     *                   </ul>
     * @param optionFunc optionMap dialect option ,empty or option map.
     * @return return the xids whose xid format follow this driver, If xid format don't follow this driver, then it is represented by {@link Optional#empty()}.
     * @throws XaException emit(not throw) when
     *                     <ul>
     *                          <li>driver don't support appropriate flags</li>
     *                          <li>driver don't support optionMap</li>
     *                          <li>server response unknown xid format</li>
     *                          <li>server response error message,see {@link io.jdbd.result.ServerException}</li>
     *                     </ul>
     */
    Publisher<Optional<Xid>> recover(int flags, Function<Option<?>, ?> optionFunc);


    /**
     * @return true : support {@link #forget(Xid, Function)} method
     */
    boolean isSupportForget();

    /**
     * @return the sub set of {@link #start(Xid, int, TransactionOption)} support flags(bit set).
     */
    int startSupportFlags();

    /**
     * @return the sub set of {@link #end(Xid, int, Function)} support flags(bit set).
     */
    int endSupportFlags();

    /**
     * @return the sub set of {@link #recover(int, Function)} support flags(bit set).
     */
    int recoverSupportFlags();

    /**
     * {@inheritDoc}
     */
    @Override
    Publisher<RmDatabaseSession> setTransactionCharacteristics(TransactionOption option);


    /**
     * {@inheritDoc}
     */
    @Override
    Publisher<RmDatabaseSession> releaseSavePoint(SavePoint savepoint);

    /**
     * {@inheritDoc}
     */
    @Override
    Publisher<RmDatabaseSession> releaseSavePoint(SavePoint savepoint, Function<Option<?>, ?> optionFunc);


    /**
     * {@inheritDoc}
     */
    @Override
    Publisher<RmDatabaseSession> rollbackToSavePoint(SavePoint savepoint);


    /**
     * {@inheritDoc}
     */
    @Override
    Publisher<RmDatabaseSession> rollbackToSavePoint(SavePoint savepoint, Function<Option<?>, ?> optionFunc);

    /**
     * {@inheritDoc}
     */
    @Override
    RmDatabaseSession appendLiteral(@Nullable String text, StringBuilder builder) throws JdbdException;

    /**
     * {@inheritDoc}
     */
    @Override
    RmDatabaseSession appendIdentifier(String identifier, StringBuilder builder) throws JdbdException;


    /**
     * {@inheritDoc}
     */
    @Override
    RmDatabaseSession appendTableName(String tableName, NameMode mode, StringBuilder builder) throws JdbdException;

    /**
     * {@inheritDoc}
     */
    @Override
    RmDatabaseSession appendColumnName(String columnName, NameMode mode, StringBuilder builder) throws JdbdException;


}
