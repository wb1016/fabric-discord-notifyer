package net.wb1016.fdnotif;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;

@Config(name = fdnotif.MOD_ID)
public class Configuration implements ConfigData {
		@ConfigEntry.Category(value = "Discord")
		public String botToken = "";

		@ConfigEntry.Category(value = "Discord")
		public String botGameStatus = "";

	@ConfigEntry.Category(value = "Discord")
	public String channelId = "";

	@ConfigEntry.Category(value = "Discord")
	public boolean announcePlayers = true;

	@ConfigEntry.Category(value = "Discord")
	public boolean announceAdvancements = true;

	@ConfigEntry.Category(value = "Discord")
	public boolean announceDeaths = true;

	public Texts texts = new Texts();

	public static class Texts {

		@ConfigEntry.Category(value = "Texts")
		public String serverStarted = "**Server started!**";

		@ConfigEntry.Category(value = "Texts")
		public String serverStopped = "**Server stopped!**";


		public String joinServer = "**%playername% joined the game**";

		@ConfigEntry.Category(value = "Texts")
		public String leftServer = "**%playername% left the game**";

		@ConfigEntry.Category(value = "Texts")
		public String deathMessage = "**%deathmessage%**";

		@ConfigEntry.Category(value = "Texts")
		public String advancementTask = "%playername% has made the advancement **[%advancement%]**";

		@ConfigEntry.Category(value = "Texts")
		public String advancementChallenge = "%playername% has completed the challenge **[%advancement%]**";

		@ConfigEntry.Category(value = "Texts")
		public String advancementGoal = "%playername% has reached the goal **[%advancement%]**";
	}
}
