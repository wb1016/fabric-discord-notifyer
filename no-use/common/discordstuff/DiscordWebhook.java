package net.wb1016.fdnotif.discordstuff;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.receive.ReadonlyMessage;
import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.wb1016.fdnotif.fdnotif;
import net.wb1016.fdnotif.api.config.Config;
import net.wb1016.fdnotif.api.discord.MessageHandler;
import net.wb1016.fdnotif.api.discord.MessageSender;
import net.wb1016.fdnotif.api.discord.MinecraftMessage;
import net.wb1016.fdnotif.api.minecraft.Message;
import net.wb1016.fdnotif.api.minecraft.MinecraftServer;
import net.wb1016.fdnotif.api.minecraft.PlayerEntity;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DiscordWebhook implements MessageSender {

    protected final Config config;
    protected WebhookClient webhookClient;
    protected DiscordBot messageReader;

    public DiscordWebhook(String webhookURL, Config config, DiscordBot messageReader) {
        this.config = config;
        WebhookClientBuilder builder = new WebhookClientBuilder(webhookURL)
                .setAllowedMentions(AllowedMentions.none()
                        .withParseEveryone(config.mainConfig.webhook.mentions.everyone)
                        .withParseRoles(config.mainConfig.webhook.mentions.roles)
                        .withParseUsers(config.mainConfig.webhook.mentions.users)
                )
                // Fix found by Tom_The_Geek (Geek202), creator of TomsServerUtils.
                .setHttpClient(new OkHttpClient.Builder()
                    .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                    .build());
        this.webhookClient = builder.build();
        this.messageReader = messageReader;
    }

    public @NotNull CompletableFuture<ReadonlyMessage> sendMessage(UUID author, String message) {
        WebhookMessageBuilder builder = new WebhookMessageBuilder();

        if (author != null) {
            try {
                if (fdnotif.getMessageReceiver() != null && fdnotif.getMessageReceiver().getServer() != null) {
                    MinecraftServer minecraftServer = fdnotif.getMessageReceiver().getServer();
                    builder.setUsername(minecraftServer.getUsernameFromUUID(author));
                } else {
                    builder.setUsername("Could not get player username");
                }
            } catch (NullPointerException e) {
                builder.setUsername("Could not get player username");
            }
            builder.setAvatarUrl("https://crafatar.com/avatars/" + author.toString() + "?&overlay");
        }

        builder.setContent(message);

        return this.webhookClient.send(builder.build());
    }

    @Override
    public void serverStarting() {
        if (this.config.mainConfig.minecraftToDiscord.chatChannels.serverStartingMessage) this.sendMessage(null, this.config.messageConfig.minecraftToDiscord.serverStarting);
    }

    @Override
    public void serverStarted() {
        if (this.config.mainConfig.minecraftToDiscord.chatChannels.serverStartMessage) this.sendMessage(null, this.config.messageConfig.minecraftToDiscord.serverStarted);
    }

    @Override
    public void serverStopping() {
        if (this.config.mainConfig.minecraftToDiscord.chatChannels.serverStoppingMessage) this.sendMessage(null, this.config.messageConfig.minecraftToDiscord.serverStopping);
        if (this.messageReader != null) this.messageReader.serverStopping();
    }

    @Override
    public void serverStopped() {
        if (this.config.mainConfig.minecraftToDiscord.chatChannels.serverStopMessage) this.sendMessage(null, this.config.messageConfig.minecraftToDiscord.serverStopped)
                .thenRun(new Runnable() {
                    @Override
                    public void run() {
                        DiscordWebhook.this.webhookClient.close();
                    }
                });
        else this.webhookClient.close();
        if (this.messageReader != null) this.messageReader.serverStopped();
    }

    @Override
    public void sendMessage(Message message) {
        if (this.messageReader != null && this.messageReader.minecraftToDiscordHandler != null
                && this.webhookClient != null && this.config != null) {
            MinecraftMessage minecraftMessage = this.messageReader.minecraftToDiscordHandler.handleText(message);
            if (minecraftMessage != null) {
                MinecraftMessage.MessageSendability common = minecraftMessage.getCommon();
                MinecraftMessage.MessageSendability chat = minecraftMessage.getChat();

                if (common != null && common.canSendChat()) {
                    UUID authorUUID = null;
                    if (message.hasAuthorUUID()) authorUUID = message.getAuthorUUID();
                    else if (minecraftMessage.doSearchForAuthor()) {
                        if (message.getKey() != null && message.getKey().equals("chat.type.team.text")) {
                            authorUUID = getPlayerUUIDFromText(message.getArgs()[1]);
                        } else {
                            authorUUID = getPlayerUUIDFromText(message.getArgs()[0]);
                        }
                    }

                    this.sendMessage(authorUUID, common.getMessage());
                } else if (chat != null && chat.canSend()) {
                    UUID authorUUID = null;
                    if (message.hasAuthorUUID()) authorUUID = message.getAuthorUUID();
                    else if (minecraftMessage.doSearchForAuthor()) {
                        if (message.getKey() != null && message.getKey().equals("chat.type.team.text")) {
                            authorUUID = getPlayerUUIDFromText(message.getArgs()[1]);
                        } else {
                            authorUUID = getPlayerUUIDFromText(message.getArgs()[0]);
                        }
                    }

                    this.sendMessage(authorUUID, chat.getMessage());
                }
            }
        }
    }

    private UUID getPlayerUUIDFromText(Object arg) {
        String playerName = "";
        if (arg instanceof Message) {
            Message message = (Message) arg;
            if (message.getSibblings().isEmpty()) {
                playerName = message.getMessage();
            } else if (message.getSibblings().size() == 3) {
                playerName = message.getSibblings().get(1).getMessage();
            }
        }

        if (playerName.isEmpty()) {
            playerName = MessageHandler.getAsString(arg);
        }
        try {
            if (!playerName.isEmpty() && fdnotif.getMessageReceiver() != null && fdnotif.getMessageReceiver().getServer() != null) {
                MinecraftServer minecraftServer = fdnotif.getMessageReceiver().getServer();
                PlayerEntity playerEntity = minecraftServer.getPlayerFromUsername(playerName);
                return playerEntity.getUUID();
            }
        } catch (NullPointerException ignored) {
            ignored.printStackTrace();
        }

        return null;
    }
}
