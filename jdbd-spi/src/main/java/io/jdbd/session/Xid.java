package io.jdbd.session;

import io.jdbd.lang.Nullable;

/**
 * <p>
 * XID consist of following three :
 * <ul>
 *     <li>{@link #getGtrid()}</li>
 *     <li>{@link #getBqual()}</li>
 *     <li>{@link #getFormatId()}</li>
 * </ul>
 * <br/>
 * <p>
 * To be safe,{@link RmDatabaseSession} <strong>possibly</strong>  write gtrid and bqual as hex strings. steps :
 * <ul>
 *     <li>Get byte[] with {@link java.nio.charset.StandardCharsets#UTF_8}</li>
 *     <li>write gtrid or bqual as hex strings</li>
 * </ul>
 * the conversion process of {@link RmDatabaseSession#recover(int, java.util.function.Function)} is the reverse of above.
 * <br/>
 * <p>
 *     Application developer can get the instance of {@link Xid} by {@link #from(String, String, int)} method.
 * <br/>
 *
 * @see Option#XID
 * @see RmDatabaseSession
 */
public interface Xid extends OptionSpec {

    /**
     * <p>
     * The global transaction identifier string
     *<br/>
     * <p>
     *   <ul>
     *       <li>Global transaction identifier must have text</li>
     *   </ul>
     *<br/>
     *
     * @return a global transaction identifier.
     */
    String getGtrid();

    /**
     * Obtain the transaction branch identifier part of XID as an string.
     * <p>
     *   <ul>
     *       <li>If non-null,branch transaction identifier must have text</li>
     *   </ul>
     *<br/>
     *
     * @return a branch qualifier
     */
    @Nullable
    String getBqual();

    /**
     * Obtain the format identifier part of the XID.
     *
     * @return Format identifier. O means the OSI CCR format.
     */
    int getFormatId();

    /**
     * <p>
     * The implementation of {@link Xid} must correctly override this method with only following three :
     *     <ul>
     *         <li>{@link #getGtrid()}</li>
     *         <li>{@link #getBqual()}</li>
     *         <li>{@link #getFormatId()}</li>
     *     </ul>
     *<br/>
     * <p>
     *     Like following :
     *     <pre>
     *         <code><br/>
     *               &#64;Override
     *               public int hashCode() {
     *                   return Objects.hash(this.gtrid, this.bqual, this.formatId);
     *               }
     *         </code>
     *     </pre>
     *<br/>
     */
    @Override
    int hashCode();

    /**
     * <p>
     * The implementation of {@link Xid} must correctly override this method with only following three :
     *     <ul>
     *         <li>{@link #getGtrid()}</li>
     *         <li>{@link #getBqual()}</li>
     *         <li>{@link #getFormatId()}</li>
     *     </ul>
     *<br/>
     * <p>
     *     Like following :
     *     <pre>
     *         <code><br/>
     *           &#64;Override
     *           public boolean equals(final Object obj) {
     *               final boolean match;
     *               if (obj == this) {
     *                   match = true;
     *               } else if (obj instanceof Xid) {
     *                   final Xid o = (Xid) obj;
     *                   match = this.gtrid.equals(o.getGtrid())
     *                           &amp;&amp; Objects.equals(o.getBqual(), this.bqual)
     *                           &amp;&amp; o.getFormatId() == this.formatId;
     *               } else {
     *                   match = false;
     *               }
     *               return match;
     *           }
     *         </code>
     *     </pre>
     *<br/>
     */
    @Override
    boolean equals(Object obj);

    /**
     * override {@link Object#toString()}
     *
     * @return xid info, contain : <ol>
     * <li>class name</li>
     * <li>{@link #getGtrid()}</li>
     * <li>{@link #getBqual()}</li>
     * <li>{@link #getFormatId()}</li>
     * <li>dialect option if exists</li>
     * <li>{@link System#identityHashCode(Object)}</li>
     * </ol>
     */
    @Override
    String toString();

    /**
     * <p>
     * {@link RmDatabaseSession#recover(int, java.util.function.Function) } maybe add some dialect value.
     *<br/>
     *
     * @return null or dialect option value.
     * @see RmDatabaseSession#recover(int, java.util.function.Function)
     */
    @Nullable
    @Override
    <T> T valueOf(Option<T> option);

    /**
     * <p>
     * Create one {@link Xid} instance.
     *<br/>
     *
     * @param gtrid must have text
     * @param bqual null or must have text
     * @throws IllegalArgumentException throw when gtrid or bqual error.
     */
    static Xid from(String gtrid, @Nullable String bqual, int formatId) {
        return JdbdXid.from(gtrid, bqual, formatId);
    }

}
