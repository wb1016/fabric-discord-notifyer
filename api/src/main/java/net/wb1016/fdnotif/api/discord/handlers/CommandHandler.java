package net.wb1016.fdnotif.api.discord.handlers;

import net.wb1016.fdnotif.api.discord.MinecraftToDiscordFunction;
import net.wb1016.fdnotif.api.minecraft.Message;

public class CommandHandler extends MessageHandler {
    private final String commandName;

    public CommandHandler(String commandName, MinecraftToDiscordFunction minecraftToDiscordFunction) {
        super(minecraftToDiscordFunction);
        this.commandName = commandName;
    }

    @Override
    public boolean match(Message message) {
        if (message.getTextType() == Message.TextType.COMMAND) {
            return this.commandName.equals(message.getCommandName());
        }
        return false;
    }
}
