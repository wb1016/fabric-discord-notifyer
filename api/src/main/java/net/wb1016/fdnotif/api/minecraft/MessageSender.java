package net.wb1016.fdnotif.api.minecraft;

import net.wb1016.fdnotif.api.minecraft.style.Style;

public interface MessageSender {

    void sendMessageToChat(MinecraftServer server, String message, Style style);
}
