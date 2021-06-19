package net.wb1016.fdnotif;

import net.wb1016.fdnotif.discord.MessageSender;
import net.wb1016.fdnotif.config.manager.ConfigHandler;
import net.wb1016.fdnotif.discordstuff.DiscordBot;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class fdnotif implements ModInitializer {

	private static DiscordBot messageReceiver;
	public static Logger LOGGER = LogManager.getLogger("fdnotif");
	private static boolean loaded = false;

	@Override
	public void onInitializeServer() {
		initialize();
	}

	private static void initialize() {
		ConfigHandler.ConfigHolder configHolder = ConfigHandler.getConfig();
		messageReceiver = new DiscordBot(configHolder.getToken(), configHolder.getConfig());
		messageSender = messageReceiver;
		loaded = true;
	}

	public static void regenConfig() {
		ConfigHandler.ConfigHolder configHolder = ConfigHandler.getConfig();
		messageReceiver = new DiscordBot(configHolder.getToken(), configHolder.getConfig());
		messageSender = messageReceiver;
	}

	public static MessageSender getMessageSender() {
		if (!loaded) initialize();
		return messageSender;
	}

	public static DiscordBot getMessageReceiver() {
		if (!loaded) initialize();
		return messageReceiver;
	}
}
