package net.wb1016.fdnotif;

import net.wb1016.fdnotif.listeners.MinecraftEventListener;
import net.wb1016.fdnotif.Configuration;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import kong.unirest.Unirest;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.security.auth.login.LoginException;
import java.util.Collections;

public class fdnotif implements DedicatedServerModInitializer {
    public static final String MOD_ID = "fdnotif";
    public static Logger logger = LogManager.getLogger(MOD_ID);
    public static JDA jda;
    public static TextChannel textChannel;
    public static Configuration config;

    public static boolean stop = false;
    @Override
    public void onInitializeServer() {
        AutoConfig.register(Configuration.class, JanksonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(Configuration.class).getConfig();
        try {
                fdnotif.jda = JDABuilder.createDefault(config.botToken).setHttpClient(new OkHttpClient.Builder()
                        .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                        .build())
                    .build();
            fdnotif.jda.awaitReady();
            fdnotif.textChannel = fdnotif.jda.getTextChannelById(config.channelId);
        } catch (LoginException ex) {
            jda = null;
            fdnotif.logger.error("Unable to login!", ex);
        } catch (InterruptedException ex) {
            jda = null;
            fdnotif.logger.error(ex);
        }
        if(jda != null) {
            if(!config.botGameStatus.isEmpty())
                jda.getPresence().setActivity(Activity.playing(config.botGameStatus));
            ServerLifecycleEvents.SERVER_STARTED.register((server) -> textChannel.sendMessage(fdnotif.config.texts.serverStarted).queue());
            ServerLifecycleEvents.SERVER_STOPPING.register((server) -> {
                stop = true;
                textChannel.sendMessage(fdnotif.config.texts.serverStopped).queue();
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Unirest.shutDown();
                fdnotif.jda.shutdown();
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            new MinecraftEventListener().init();
        }
    }
}
