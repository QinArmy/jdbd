package io.jdbd.session;

import java.nio.charset.Charset;
import java.util.function.Function;

/**
 * <p>
 * This interface is designed for following :
 *     <ul>
 *         <li>{@link io.jdbd.statement.Statement#setImportPublisher(Function)}</li>
 *         <li>{@link io.jdbd.statement.Statement#setExportSubscriber(Function)}</li>
 *     </ul>
 * <br/>
 * <p>
 *      Currently only one method,maybe add new method in the future.
 * <br/>
 *
 * @since 1.0
 */
public interface ChunkOption extends OptionSpec {

    /**
     * <p>
     * If chunk is binary , the {@link Charset} should be {@link java.nio.charset.StandardCharsets#ISO_8859_1}
     * <br/>
     *
     * @return non-null
     */
    Charset charset();


}
