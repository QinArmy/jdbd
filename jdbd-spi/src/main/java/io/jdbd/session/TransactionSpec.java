package io.jdbd.session;


/**
 * <p>Package interface
 * <p>This interface is base interface of following :
 * <ul>
 *     <li>{@link TransactionOption}</li>
 *     <li>{@link TransactionInfo}</li>
 * </ul>
 */
interface TransactionSpec extends OptionSpec {


    /**
     * @return true : transaction is read-only.
     */
    boolean isReadOnly();


    /**
     * <p>
     * override {@link Object#toString()}
     * <p>
     * <br/>
     *
     * @return transaction info, contain
     * <ul>
     *     <li>implementation class name</li>
     *     <li>transaction info</li>
     *     <li>{@link System#identityHashCode(Object)}</li>
     * </ul>
     */
    @Override
    String toString();


}
