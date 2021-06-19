package net.wb1016.fdnotif.discord;

import net.wb1016.fdnotif.minecraft.CompatText;
import net.wb1016.fdnotif.minecraft.Message;

import java.util.ArrayList;
import java.util.List;

public interface MessageHandler {

    List<net.wb1016.fdnotif.discord.handlers.MessageHandler> TEXT_HANDLERS = new ArrayList<>();

    MinecraftMessage handleText(Message text);

    static void registerHandler(net.wb1016.fdnotif.discord.handlers.MessageHandler messageHandler) {
        TEXT_HANDLERS.add(messageHandler);
    }

    default String adaptUsernameToDiscord(String username) {
        return adaptUsername(username);
    }

    static String adaptUsername(String username) {
        return username.replaceAll("§[b0931825467adcfeklmnor]", "")
                .replaceAll("([_`~*>])", "\\\\$1");
    }

    default String getArgAsString(Object arg) {
        return getAsString(arg);
    }

    static String getAsString(Object arg) {
        if (arg instanceof CompatText) {
            return ((CompatText) arg).getMessage();
        } else if (arg instanceof Message) {
            return ((Message) arg).getMessage();
        }
        return (String) arg;
    }
}
