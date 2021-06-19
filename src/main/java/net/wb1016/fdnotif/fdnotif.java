package net.wb1016.fdnotif;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.wb1016.fdnotif.cfg.ConfigHandler;
import net.wb1016.fdnotif.discord.MessageSender;
import net.wb1016.fdnotif.discordstuff.DiscordBot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class fdnotif implements DedicatedServerModInitializer {

	private static DiscordBot messageReceiver;
	private static MessageSender messageSender;
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
