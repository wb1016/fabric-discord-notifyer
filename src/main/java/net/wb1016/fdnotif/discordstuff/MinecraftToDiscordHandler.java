package net.wb1016.fdnotif.discordstuff;

import net.wb1016.fdnotif.cfg.Config;
import net.wb1016.fdnotif.cfg.MainConfig;
import net.wb1016.fdnotif.discord.MessageHandler;
import net.wb1016.fdnotif.discord.MinecraftMessage;
import net.wb1016.fdnotif.discord.handlers.StringHandler;
import net.wb1016.fdnotif.discord.handlers.TextHandler;
import net.wb1016.fdnotif.minecraft.Message;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public final class MinecraftToDiscordHandler implements MessageHandler {

    private final JDA api;
    private final DiscordBot discordBot;
    private final Config config;

    public MinecraftToDiscordHandler(DiscordBot discordBot) {
        this.api = discordBot.api;
        this.discordBot = discordBot;
        this.config = discordBot.config;

        // Advancement task
        MessageHandler.registerHandler(new TextHandler("chat.type.advancement.task", (text, config) -> {
            String message = text.getMessage().replaceAll("§[b0931825467adcfeklmnor]", "");
            if (this.config.messageConfig.minecraftToDiscord.advancementTask.useCustomMessage) {
                message = this.config.messageConfig.minecraftToDiscord.advancementTask.customMessage
                        .replace("%player", adaptUsernameToDiscord(getArgAsString(text.getArgs()[0])))
                        .replace("%advancement", getArgAsString(text.getArgs()[1]));
            }
            MinecraftMessage minecraftMessage = new MinecraftMessage(new MinecraftMessage.MessageSendability(message,
                    config.mainConfig.minecraftToDiscord.logChannels.advancementMessages
                    ));
            return minecraftMessage;
        }));

        // Advancement challenge
        MessageHandler.registerHandler(new TextHandler("chat.type.advancement.challenge", (text, config) -> {
            String message = text.getMessage().replaceAll("§[b0931825467adcfeklmnor]", "");
            if (this.config.messageConfig.minecraftToDiscord.advancementChallenge.useCustomMessage) {
                message = this.config.messageConfig.minecraftToDiscord.advancementChallenge.customMessage
                        .replace("%player", adaptUsernameToDiscord(getArgAsString(text.getArgs()[0])))
                        .replace("%advancement", getArgAsString(text.getArgs()[1]));
            }
            MinecraftMessage minecraftMessage = new MinecraftMessage(new MinecraftMessage.MessageSendability(message,
                    config.mainConfig.minecraftToDiscord.logChannels.challengeMessages
                    ));
            return minecraftMessage;
        }));

        // Advancement goal
        MessageHandler.registerHandler(new TextHandler("chat.type.advancement.goal", (text, config) -> {
            String message = text.getMessage().replaceAll("§[b0931825467adcfeklmnor]", "");
            if (this.config.messageConfig.minecraftToDiscord.advancementGoal.useCustomMessage) {
                message = this.config.messageConfig.minecraftToDiscord.advancementGoal.customMessage
                        .replace("%player", adaptUsernameToDiscord(getArgAsString(text.getArgs()[0])))
                        .replace("%advancement", getArgAsString(text.getArgs()[1]));
            }
            MinecraftMessage minecraftMessage = new MinecraftMessage(new MinecraftMessage.MessageSendability(message,
                    config.mainConfig.minecraftToDiscord.logChannels.goalMessages
                    ));
            return minecraftMessage;
        }));

        // Admin commands
        MessageHandler.registerHandler(new TextHandler("chat.type.admin", (text, config) -> {
            String message = text.getMessage().replaceAll("§[b0931825467adcfeklmnor]", "");
            if (this.config.messageConfig.minecraftToDiscord.adminMessage.useCustomMessage) {
                message = this.config.messageConfig.minecraftToDiscord.adminMessage.customMessage
                        .replace("%author", adaptUsernameToDiscord(getArgAsString(text.getArgs()[0])))
                        .replace("%message", getArgAsString(text.getArgs()[1]));
            }
            MinecraftMessage minecraftMessage = new MinecraftMessage(new MinecraftMessage.MessageSendability(message,
                    config.mainConfig.minecraftToDiscord.logChannels.adminMessages
                    ));
            return minecraftMessage;
        }));

        // Player join server with new username
        MessageHandler.registerHandler(new TextHandler("multiplayer.player.joined.renamed", (text, config) -> {
            String message = text.getMessage().replaceAll("§[b0931825467adcfeklmnor]", "");
            if (this.config.messageConfig.minecraftToDiscord.playerJoinedRenamed.useCustomMessage) {
                message = this.config.messageConfig.minecraftToDiscord.playerJoinedRenamed.customMessage
                        .replace("%new", adaptUsernameToDiscord(getArgAsString(text.getArgs()[0])))
                        .replace("%old", adaptUsernameToDiscord(getArgAsString(text.getArgs()[1])));
            }
            MinecraftMessage minecraftMessage = new MinecraftMessage(new MinecraftMessage.MessageSendability(message,
                    config.mainConfig.minecraftToDiscord.logChannels.joinAndLeaveMessages
                    ));
            return minecraftMessage;
        }));

        // Player join server
        MessageHandler.registerHandler(new TextHandler("multiplayer.player.joined", (text, config) -> {
            String message = text.getMessage().replaceAll("§[b0931825467adcfeklmnor]", "");
            if (this.config.messageConfig.minecraftToDiscord.playerJoined.useCustomMessage) {
                message = this.config.messageConfig.minecraftToDiscord.playerJoined.customMessage
                        .replace("%player", adaptUsernameToDiscord(getArgAsString(text.getArgs()[0])));
            }
            MinecraftMessage minecraftMessage = new MinecraftMessage(new MinecraftMessage.MessageSendability(message,
                    config.mainConfig.minecraftToDiscord.logChannels.joinAndLeaveMessages
            ));
            return minecraftMessage;
        }));

        // Player leave server
        MessageHandler.registerHandler(new TextHandler("multiplayer.player.left", (text, config) -> {
            String message = text.getMessage().replaceAll("§[b0931825467adcfeklmnor]", "");
            if (this.config.messageConfig.minecraftToDiscord.playerLeft.useCustomMessage) {
                message = this.config.messageConfig.minecraftToDiscord.playerLeft.customMessage
                        .replace("%player", adaptUsernameToDiscord(getArgAsString(text.getArgs()[0])));
            }
            MinecraftMessage minecraftMessage = new MinecraftMessage(new MinecraftMessage.MessageSendability(message,
                    config.mainConfig.minecraftToDiscord.logChannels.joinAndLeaveMessages
            ));
            return minecraftMessage;
        }));

        // Death messages
        MessageHandler.registerHandler(new TextHandler("death.", (text, config) -> {
            String message = text.getMessage().replaceAll("§[b0931825467adcfeklmnor]", "");
            MinecraftMessage minecraftMessage = new MinecraftMessage(new MinecraftMessage.MessageSendability(
                    this.config.messageConfig.minecraftToDiscord.deathMsgPrefix
                    + message
                    + this.config.messageConfig.minecraftToDiscord.deathMsgPostfix,
                    config.mainConfig.minecraftToDiscord.logChannels.deathMessages
            ));
            return minecraftMessage;
        }));

        MessageHandler.registerHandler(new CommandHandler("tellraw", (text, config) -> {

            String message = text.getMessage();
            String source = adaptUsernameToDiscord(text.getSource());

            String stringMessage = this.config.messageConfig.minecraftToDiscord.atATellRaw
                    .replace("%message", message)
                    .replace("%source", source);

            MinecraftMessage minecraftMessage = new MinecraftMessage(new MinecraftMessage.MessageSendability(stringMessage,
                    config.mainConfig.minecraftToDiscord.logChannels.atATellRaw
                    )).searchForAuthor();

            if (text.hasAuthorUUID()) minecraftMessage.setAuthor(text.getAuthorUUID());

            return minecraftMessage;
        }));
}

    @Override
    public MinecraftMessage handleText(Message text) {
        if (this.api == null || (!this.discordBot.hasLogChannels)) return null;
        Message.MessageObjectType objectType = text.getType();
        String message = text.getMessage();
        if (message.equals(this.discordBot.lastMessageD)) return null;
        for (net.wb1016.fdnotif.discord.handlers.MessageHandler messageHandler : TEXT_HANDLERS) {
            if (messageHandler.match(text)) {
                return messageHandler.handle(text, this.config);
            }
        }
        if (this.config.mainConfig.minecraftToDiscord.general.enableDebugLogs) {
            if (text.getTextType() == Message.TextType.TRANSLATABLE) {
                DiscordBot.LOGGER.error("[fdnotif] Unhandled text \"{}\":{}", text.getKey(), text.getMessage());
            } else {
                DiscordBot.LOGGER.error("[fdnotif] Unhandled text \"{}\"", text.getMessage());
            }
        }
        return null;
    }
}