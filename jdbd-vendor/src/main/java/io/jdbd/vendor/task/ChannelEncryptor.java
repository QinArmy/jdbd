package io.jdbd.vendor.task;


/**
 * Help class for add ssl
 */
public interface ChannelEncryptor {

    /**
     * add ssl
     *
     * @param sslWrapper non-null
     */
    void addSsl(SslWrapper sslWrapper);

    /**
     * add gss context
     *
     * @param gssWrapper non-null
     */
    void addGssContext(GssWrapper gssWrapper);


}
