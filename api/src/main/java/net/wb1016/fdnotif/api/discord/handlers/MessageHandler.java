package net.wb1016.fdnotif.api.discord.handlers;

import net.wb1016.fdnotif.api.config.Config;
import net.wb1016.fdnotif.api.discord.MinecraftMessage;
import net.wb1016.fdnotif.api.discord.MinecraftToDiscordFunction;
import net.wb1016.fdnotif.api.minecraft.Message;

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
