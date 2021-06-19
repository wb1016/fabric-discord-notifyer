package net.wb1016.fdnotif.discordstuff;

import net.wb1016.fdnotif.fdnotif;
import net.wb1016.fdnotif.cfg.Config;
import net.wb1016.fdnotif.cfg.MainConfig;
import net.wb1016.fdnotif.discord.MessageSender;
import net.wb1016.fdnotif.discord.MinecraftMessage;
import net.wb1016.fdnotif.minecraft.MinecraftServer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class DiscordBot implements MessageSender {
    public static final Logger LOGGER = LogManager.getLogger();
    protected MinecraftToDiscordHandler minecraftToDiscordHandler = null;

    protected Config config;
    public boolean hasLogChannels;
    protected MessageReceivedEvent messageCreateEvent;
    protected boolean hasReceivedMessage;
    public String lastMessageD;
    protected static List<String> lastMessageMs = new ArrayList<>();
    protected JDA api = null;
    protected long startTime;
    protected boolean stopping = false;
    // This is when we scheduled the next channel topic update. Should happen every five minutes.
    private long nextChannelTopicUpdateTimeMilliseconds = Long.MIN_VALUE;

    private boolean firstTick = true;
    private boolean updatedActivity = false;

    protected MinecraftServer server;

    public DiscordBot(String token, Config config) {
        this.lastMessageD = "null";

        if (token == null) {
            fdnotif.regenConfig();
            return;
        }

        if (token.isEmpty()) {
            LOGGER.error("[fdnotif] Please add a bot token to the config file!");
            return;
        }
        this.startTime = System.currentTimeMillis();

        if (config.mainConfig.logChannels.isEmpty()) {
            this.hasLogChannels = false;
        } else {
            this.hasLogChannels = true;
        }

        this.config = config;
        try {
            this.api = JDABuilder.createDefault(token).setActivity(Activity.playing("Minecraft")).build();
        } catch (LoginException error) {
            error.printStackTrace();
        }
        this.minecraftToDiscordHandler = new MinecraftToDiscordHandler(this);
    }

    @Override
    public void serverStarting() {
        if (this.api == null) return;
        if (this.config.mainConfig.minecraftToDiscord.logChannels.serverStartingMessage) sendToLogChannels(config.messageConfig.minecraftToDiscord.serverStarting);
    }

    @Override
    public void serverStarted() {
        if (this.api == null) return;
        this.startTime = System.currentTimeMillis();
        if (this.config.mainConfig.minecraftToDiscord.logChannels.serverStartMessage) sendToLogChannels(config.messageConfig.minecraftToDiscord.serverStarted);
    }

    @Override
    public void serverStopping() {
        if (this.api == null) return;
        this.stopping = true;
        if (this.config.mainConfig.minecraftToDiscord.logChannels.serverStoppingMessage) sendToLogChannels(config.messageConfig.minecraftToDiscord.serverStopping);
    }

    @Override
    public void serverStopped() {
        if (this.api == null) return;
        if (this.config.mainConfig.minecraftToDiscord.logChannels.serverStopMessage) {
            ArrayList<CompletableFuture<Message>> requests = new ArrayList<>();
            if(this.config.mainConfig.minecraftToDiscord.logChannels.serverStopMessage && this.hasLogChannels) requests.addAll(sendToLogChannels(config.messageConfig.minecraftToDiscord.serverStopped, requests));

            for (CompletableFuture<Message> request : requests){
                while (!request.isDone()) {
                    if (this.config.mainConfig.minecraftToDiscord.general.enableDebugLogs) LOGGER.info("Request is not done yet!");
                }
            }
        }
        this.api.shutdownNow();
    }

    public void serverTick(MinecraftServer server) {
        if (this.api == null) return;
        this.server = server;
        if (this.server == null) return;
        int playerNumber = server.getPlayerCount();
        int maxPlayer = server.getMaxPlayerCount();
        int totalUptimeSeconds = (int) (System.currentTimeMillis() - this.startTime) / 1000;
        final int uptimeD = totalUptimeSeconds / 86400;
        final int uptimeH = (totalUptimeSeconds % 86400) / 3600;
        final int uptimeM = (totalUptimeSeconds % 3600) / 60;
        final int uptimeS = totalUptimeSeconds % 60;
        String ip = server.getIp();
        if (this.updatedActivity) this.updatedActivity = ((int)(System.currentTimeMillis()/1000) % this.config.mainConfig.activityUpdateInterval) == 0;
        if ((((int)(System.currentTimeMillis()/1000) % this.config.mainConfig.activityUpdateInterval) == 0 && !this.updatedActivity) || this.firstTick) {
            String[] possibleActivities = this.config.messageConfig.discord.botActivities;
            if (possibleActivities.length > 0) {
                int rand = new Random().nextInt(possibleActivities.length);
                String selected = possibleActivities[rand];
                selected = selected
                        .replace("%playercount", String.valueOf(playerNumber))
                        .replace("%maxplayercount", String.valueOf(maxPlayer))
                        .replace("%uptime_D", String.valueOf(uptimeD))
                        .replace("%uptime_H", String.valueOf(uptimeH))
                        .replace("%uptime_M", String.valueOf(uptimeM))
                        .replace("%uptime_S", String.valueOf(uptimeS))
                        .replace("%ip", ip);
                this.api.getPresence().setActivity(Activity.playing(selected));
                this.updatedActivity = true;
            }
        }

        if ((this.hasLogChannels) && (this.config.mainConfig.minecraftToDiscord.logChannels.customChannelDescription) && (System.currentTimeMillis() > this.nextChannelTopicUpdateTimeMilliseconds)) {
            this.nextChannelTopicUpdateTimeMilliseconds = System.currentTimeMillis() + 5 * 60 * 1000; // Five minutes from now.

            final String topic = this.config.messageConfig.minecraftToDiscord.channelDescription
                    .replace("%playercount", String.valueOf(playerNumber))
                    .replace("%maxplayercount", String.valueOf(maxPlayer))
                    .replace("%uptime", uptimeD + "d " + uptimeH + "h " + uptimeM + "m " + uptimeS + "s")
                    .replace("%motd", server.getMotd())
                    .replace("%uptime_d", String.valueOf(uptimeD))
                    .replace("%uptime_h", String.valueOf(uptimeH))
                    .replace("%uptime_m", String.valueOf(uptimeM))
                    .replace("%uptime_s", String.valueOf(uptimeS));

            if (this.config.mainConfig.minecraftToDiscord.logChannels.customChannelDescription){
                for (String id : this.config.mainConfig.logChannels) {
                    this.updateChannelTopic(id, topic);
                }
            }
        }

        if (this.firstTick) this.firstTick = false;
    }

    private void updateChannelTopic(String channelId, String topic) {
        TextChannel channel = this.api.getTextChannelById(channelId);
        if (channel != null) {
            try {
                channel.getManager().setTopic(topic).queue();
            }
            catch (InsufficientPermissionException e) {
                LOGGER.error(String.format("Failed to set the channel topic for channel %s. Check that the bot has the <Manage Channels> permission, or else disable custom channel descriptions.", channelId), e);
            }
        }
    }

    public List<CompletableFuture<Message>> sendToLogChannels(String message) {
        return this.sendToLogChannels(message, new ArrayList<>());
    }

    public List<CompletableFuture<Message>> sendToLogChannels(String message, List<CompletableFuture<Message>> list) {
        if (this.hasLogChannels && this.api != null) {
            for (String id : this.config.mainConfig.logChannels) {
                try {
                    this.api = this.api.awaitReady();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                TextChannel channel = this.api.getTextChannelById(id);
                list.add(channel.sendMessage(message).submit());
                lastMessageMs.add(message);
            }
        }
        return list;
    }

    public MinecraftServer getServer() {
        return server;
    }
}
