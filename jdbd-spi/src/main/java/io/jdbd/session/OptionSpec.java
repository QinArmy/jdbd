package io.jdbd.session;

import io.jdbd.lang.Nullable;

import java.util.Objects;

/**
 * <p>
 * This interface is base interface of following :
 *     <ul>
 *         <li>{@link DatabaseSessionFactory}</li>
 *         <li>{@link DatabaseMetaSpec}</li>
 *         <li>{@link io.jdbd.statement.Statement}</li>
 *         <li>{@link io.jdbd.result.ResultStates}</li>
 *         <li>{@link io.jdbd.VersionSpec}</li>
 *         <li>{@link io.jdbd.result.ServerException}</li>
 *         <li>{@link io.jdbd.result.RefCursor}</li>
 *         <li>{@link TransactionOption}</li>
 *         <li>{@link io.jdbd.result.Warning}</li>
 *         <li>{@link ChunkOption}</li>
 *     </ul>
 *     ,it provider more dialectal driver.
 *<br/>
 *
 * @see Option
 * @see io.jdbd.statement.Statement#setOption(Option, Object)
 * @since 1.0
 */
public interface OptionSpec {

    /**
     * <p>
     * This method can provider more dialectal driver.
     * <br/>
     * <p>
     * The implementation of this method must provide java doc(html list) for explaining supporting {@link Option} list.
     * <br/>
     *
     * @return null or the value of option.
     */
    @Nullable
    <T> T valueOf(Option<T> option);

    default <T> T nonNullOf(Option<T> option) {
        final T value;
        value = valueOf(option);
        Objects.requireNonNull(value);
        return value;
    }


}
