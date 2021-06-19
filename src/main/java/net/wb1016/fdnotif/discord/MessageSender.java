package net.wb1016.fdnotif.discord;

import net.wb1016.fdnotif.minecraft.Message;

public interface MessageSender {

    void serverStarting();

    void serverStarted();

    void serverStopping();

    void serverStopped();

    void sendMessage(Message message);
}
