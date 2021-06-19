package net.wb1016.fdnotif.discord.handlers;

import net.wb1016.fdnotif.cfg.Config;
import net.wb1016.fdnotif.discord.MinecraftMessage;
import net.wb1016.fdnotif.discord.MinecraftToDiscordFunction;
import net.wb1016.fdnotif.minecraft.Message;

public abstract class MessageHandler {
    private final MinecraftToDiscordFunction minecraftToDiscordFunction;

    public MessageHandler(MinecraftToDiscordFunction minecraftToDiscordFunction) {
        this.minecraftToDiscordFunction = minecraftToDiscordFunction;
    }

    public MinecraftMessage handle(Message text, Config config) {
        return this.minecraftToDiscordFunction.handleText(text, config);
    }

    public abstract boolean match(Message message);
}
