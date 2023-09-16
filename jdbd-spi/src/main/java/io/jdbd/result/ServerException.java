package io.jdbd.result;

import io.jdbd.JdbdException;
import io.jdbd.lang.Nullable;
import io.jdbd.session.Option;
import io.jdbd.session.OptionSpec;


/**
 * <p>
 * Emit(not throw), when server return error message.
 * <br/>
 *
 * @since 1.0
 */
public abstract class ServerException extends JdbdException implements OptionSpec {


    protected ServerException(String message, @Nullable String sqlState, int vendorCode) {
        super(message, sqlState, vendorCode);
    }

    /**
     * <p>
     * Get one field value of server error message.
     *<br/>
     *
     * @return null , if field not exists.
     * @see Option#MESSAGE
     * @see Option#SQL_STATE
     * @see Option#VENDOR_CODE
     */
    @Override
    @Nullable
    public abstract <T> T valueOf(Option<T> option);


    @Override
    public abstract String toString();


}
