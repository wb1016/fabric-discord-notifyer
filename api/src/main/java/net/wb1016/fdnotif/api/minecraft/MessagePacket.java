package net.wb1016.fdnotif.api.minecraft;

import java.util.UUID;

public interface MessagePacket {

    Message getMessage();

    MessageType getMessageType();

    UUID getUUID();

    enum MessageType {
        CHAT,
        SYSTEM,
        INFO;
    }
}
