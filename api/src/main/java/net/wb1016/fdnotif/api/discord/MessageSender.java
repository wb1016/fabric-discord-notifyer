package net.wb1016.fdnotif.api.discord;

import net.wb1016.fdnotif.api.minecraft.Message;

public interface MessageSender {

    void serverStarting();

    void serverStarted();

    void serverStopping();

    void serverStopped();

    void sendMessage(Message message);
}
