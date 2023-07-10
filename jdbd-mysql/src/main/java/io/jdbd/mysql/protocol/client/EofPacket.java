package io.jdbd.mysql.protocol.client;

import io.netty.buffer.ByteBuf;

public final class EofPacket extends TerminatorPacket {

    public static final int EOF_HEADER = 0xFE;

    public static EofPacket read(ByteBuf payloadBuffer, final int capabilities) {
        if (Packets.readInt1AsInt(payloadBuffer) != EOF_HEADER) {
            throw new IllegalArgumentException("packetBuf isn't error packet.");
        } else if ((capabilities & Capabilities.CLIENT_PROTOCOL_41) == 0) {
            throw new IllegalArgumentException("only supported CLIENT_PROTOCOL_41.");
        }
        final int statusFags, warnings;
        statusFags = Packets.readInt2AsInt(payloadBuffer);
        warnings = Packets.readInt2AsInt(payloadBuffer);
        return new EofPacket(statusFags, warnings);
    }


    private EofPacket(int statusFags, int warnings) {
        super(warnings, statusFags);
    }


}
