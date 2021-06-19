package net.wb1016.fdnotif.discord.handlers;

import net.wb1016.fdnotif.discord.MinecraftToDiscordFunction;
import net.wb1016.fdnotif.minecraft.Message;

public class StringHandler extends MessageHandler {

    public StringHandler(MinecraftToDiscordFunction minecraftToDiscordFunction) {
        super(minecraftToDiscordFunction);
    }

    @Override
    public boolean match(Message message) {
        return message.getType() == Message.MessageObjectType.STRING;
    }
}
