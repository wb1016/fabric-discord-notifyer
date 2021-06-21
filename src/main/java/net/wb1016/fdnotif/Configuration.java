package net.wb1016.fdnotif;

import net.fabricmc.loader.api.FabricLoader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class Configuration {
    public static final Path path = FabricLoader.getInstance().getConfigDir().resolve("fdnotif.json");
    public String botToken;
    public String channelId;
    public String botGameStatus;
    public String serverStarted;
    public String serverStopped;
    public String joinServer ;
    public String leftServer ;
    public String deathMessage;
    public String advancementTask ;
    public String advancementChallenge ;
    public String advancementGoal ;
    public boolean announcePlayers ;
    public boolean announceAdvancements ;
    public boolean announceDeaths ;

    public Configuration() {
        JsonObject json;
        try {
            json = new JsonParser().parse(new String(Files.readAllBytes(path))).getAsJsonObject();
            loadFromJson(json);
        } catch (IOException e) {
            // Create default
            try {
                Files.copy(Objects.requireNonNull(Configuration.class.getResourceAsStream("/data/fdnotif/files/default_config.json")), path);
                json = new JsonParser().parse(new String(Files.readAllBytes(path))).getAsJsonObject();
                loadFromJson(json);
                fdnotif.logger.error("Unable to load config file for fdnotif");
                fdnotif.logger.error("Please fill out the config file for fdnotif, found in config/fdnotif.json");
            } catch (IOException ioException) {
                ioException.printStackTrace();
                fdnotif.logger.error("Unable to create default config");
            }
        }
    }

    private void loadFromJson(JsonObject json) {
        botToken = json.get("botToken").getAsString();
        channelId = json.get("channelId").getAsString();
        serverStarted = json.get("serverStarted").getAsString();
        serverStopped = json.get("serverStopped").getAsString();
        joinServer = json.get("joinServer").getAsString();
        leftServer = json.get("leftServer").getAsString();
        deathMessage = json.get("deathMessage").getAsString();
        advancementTask = json.get("advancementTask").getAsString();
        advancementChallenge = json.get("advancementChallenge").getAsString();
        advancementGoal = json.get("advancementGoal").getAsString();
        announcePlayers = json.get("announcePlayers").getAsBoolean();
        announceAdvancements = json.get("announceAdvancements").getAsBoolean();
        announceDeaths = json.get("announceDeaths").getAsBoolean();
    }

    public void shutdown() {
        JsonObject o = new JsonObject();
        o.addProperty("botToken", this.botToken);
        o.addProperty("channelId ", this.channelId);
        o.addProperty("announcePlayers", this.announcePlayers);
        o.addProperty("announceAdvancements", this.announceAdvancements);
        o.addProperty("announceDeaths", this.announceDeaths);
        o.addProperty("joinServer", this.joinServer);
        o.addProperty("leftServer", this.leftServer);
        o.addProperty("deathMessage", this.deathMessage);
        o.addProperty("advancementTask", this.advancementTask);
        o.addProperty("advancementChallenge", this.advancementChallenge);
        o.addProperty("advancementGoal", this.advancementGoal);
        try {
            Files.write(path, new GsonBuilder().setPrettyPrinting().create().toJson(o).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            fdnotif.logger.error("Unable to save fdnotif config");
        }
    }}