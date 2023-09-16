package io.jdbd.vendor.task;


/**
 * Help class for add ssl
 */
public interface ChannelEncryptor {

    void addSsl(SslWrapper sslWrapper);

    void addGssContext(GssWrapper gssWrapper);


}
