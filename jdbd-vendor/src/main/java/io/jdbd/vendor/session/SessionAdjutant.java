package io.jdbd.vendor.session;

import io.jdbd.DatabaseSession;
import io.jdbd.SessionFactory;
import io.jdbd.vendor.conf.IPropertyKey;
import io.jdbd.vendor.conf.JdbcUrl;
import io.netty.channel.EventLoopGroup;

/**
 * <p>
 * This interface help {@link DatabaseSession} obtain session context that store in {@link SessionFactory}.
 * </p>
 *
 * @param <K> {@link io.jdbd.vendor.conf.IPropertyKey} type.
 * @see SessionFactory
 */
public interface SessionAdjutant<K extends IPropertyKey> {

    JdbcUrl<K> obtainUrl();

    EventLoopGroup obtainEventLoopGroup();


}
