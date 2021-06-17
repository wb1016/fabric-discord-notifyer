package net.wb1016.fdnotif.api.discord.handlers;

import net.wb1016.fdnotif.api.discord.MinecraftToDiscordFunction;
import net.wb1016.fdnotif.api.minecraft.Message;

public class TextHandler extends MessageHandler {
    private final String key;
    public TextHandler(String key, MinecraftToDiscordFunction minecraftToDiscordFunction) {
        super(minecraftToDiscordFunction);
        this.key = key;
    }

    public boolean match(Message text) {
        if (text.getTextType() == Message.TextType.TRANSLATABLE) {
            return text.getKey().startsWith(this.key);
        }
        return false;
    }
}
