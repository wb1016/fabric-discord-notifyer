package net.wb1016.fdnotif.config.manager;

import com.google.gson.*;
import net.wb1016.fdnotif.cfg.Config;
import net.wb1016.fdnotif.cfg.MainConfig;
import net.wb1016.fdnotif.cfg.MessageConfig;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ConfigHandler {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final int CONFIG_VERSION = 9;

    private static File OLD_CONFIG_FILE;

    private static File CONFIG_FOLDER;
    private static File NEW_MAIN_FILE;
    private static File MESSAGE_CONFIG;

    private static final Logger LOGGER = LogManager.getLogger("fdnotif-ConfigHandler");

    private static void initPaths() {
        File gameConfig = null;
        try {
            gameConfig = FabricLoader.getInstance().getConfigDir().toFile();
        } catch (NoSuchMethodError e) {
            gameConfig = FabricLoader.getInstance().getConfigDirectory();
        }

        File GAME_CONFIG_FOLDER = gameConfig;

        OLD_CONFIG_FILE = new File(GAME_CONFIG_FOLDER, "fdnotif.json");

        CONFIG_FOLDER = new File(GAME_CONFIG_FOLDER, "fdnotif");
        NEW_MAIN_FILE = new File(CONFIG_FOLDER, "fdnotif.json");
        MESSAGE_CONFIG = new File(CONFIG_FOLDER, "messages.json");
    }

    public static ConfigHolder getConfig() {
        initPaths();
        LOGGER.info("Looking for config file(s).");
        ConfigHolder configHolder = null;
        if (OLD_CONFIG_FILE.exists()) {
            LOGGER.info("Reading old config file.");
            try (InputStreamReader fileReader = new InputStreamReader(new FileInputStream(OLD_CONFIG_FILE), StandardCharsets.UTF_8)) {
                JsonObject jsonObject = gson.fromJson(fileReader, JsonObject.class);
                jsonObject.addProperty("version", -1);
                configHolder = parseConfig(jsonObject);
            } catch (IOException e) {
                e.printStackTrace();
            }
            OLD_CONFIG_FILE.delete();
        } else {
            if (NEW_MAIN_FILE.exists()) {
                LOGGER.info("Reading config file(s).");
                try (InputStreamReader mainFileReader = new InputStreamReader(new FileInputStream(NEW_MAIN_FILE), StandardCharsets.UTF_8)) {
                    JsonObject jsonObject = new JsonObject();
                    JsonObject mainObject = gson.fromJson(mainFileReader, JsonObject.class);
                    int version = mainObject.get("version").getAsInt();
                    if (version < 2) configHolder = parseConfig(mainObject);
                    else {
                        mainObject.remove("version");
                        jsonObject.addProperty("version", version);
                        jsonObject.add("main", mainObject);
                        if (MESSAGE_CONFIG.exists()) {
                            try (InputStreamReader messageFileReader = new InputStreamReader(new FileInputStream(MESSAGE_CONFIG), StandardCharsets.UTF_8)) {
                                jsonObject.add("messages", gson.fromJson(messageFileReader, JsonObject.class));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            JsonObject messageConfig = (JsonObject) gson.toJsonTree(new MessageConfig());
                            try (FileWriter fileWriter = new FileWriter(MESSAGE_CONFIG)) {
                                fileWriter.write(gson.toJson(messageConfig));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            jsonObject.add("messages", messageConfig);
                        }

                        configHolder = parseConfig(jsonObject);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Config config = new Config();

                if (MESSAGE_CONFIG.exists()) {
                    LOGGER.info("Reading config file.");
                    try (InputStreamReader messageFileReader = new InputStreamReader(new FileInputStream(MESSAGE_CONFIG), StandardCharsets.UTF_8)) {
                        config.messageConfig = gson.fromJson(messageFileReader, MessageConfig.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    LOGGER.info("Creating config files.");
                }

                configHolder = new ConfigHolder(config, null);
            }
        }
        if (configHolder != null) {
            LOGGER.info("Config files read.");
            saveConfig(configHolder);
        }
        return configHolder;
    }

    private static ConfigHolder parseConfig(JsonObject jsonObject) {
        Config config = new Config();

        JsonObject mainObject = jsonObject.getAsJsonObject("main");

        if (mainObject.has("activityUpdateInterval")) config.mainConfig.activityUpdateInterval = mainObject.get("activityUpdateInterval").getAsInt();

        List<String> logChannels = new ArrayList<>();
        for (JsonElement jsonElement : readList(
                mainObject.getAsJsonArray("logChannels"))) {
            if (jsonElement.isJsonPrimitive()) {
                JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();

                logChannels.add(parseChannelId(jsonPrimitive));
            }
        }
        config.mainConfig.logChannels = logChannels;

        JsonObject minecraftToDiscord = mainObject.getAsJsonObject("minecraftToDiscord");
        config.mainConfig.minecraftToDiscord = gson.fromJson(minecraftToDiscord.toString(), MainConfig.MinecraftToDiscord.class);

        JsonObject messageObject = jsonObject.getAsJsonObject("messages");
        config.messageConfig = gson.fromJson(messageObject.toString(), MessageConfig.class);


        String token = mainObject.get("token").getAsString();

        return new ConfigHolder(config, token);
    }

    private static String parseChannelId(JsonPrimitive jsonPrimitive) {
        String string = "0";

        if (jsonPrimitive.isNumber()) {
            Number number = jsonPrimitive.getAsNumber();
            string = String.valueOf(number.longValue());
        } else if (jsonPrimitive.isString()) {
            String numberString = jsonPrimitive.getAsString()
                    .replace("<", "")
                    .replace(">", "");

            string = String.valueOf(Long.valueOf(numberString));
        }

        return string;
    }

    private static List<JsonElement> readList(JsonArray jsonArray) {
        List<JsonElement> jsonObjectList = new ArrayList<>();

        Iterator<JsonElement> iterator = jsonArray.iterator();
        while (iterator.hasNext()) {
            jsonObjectList.add(iterator.next());
        }

        return jsonObjectList;
    }

    private static void saveMainConfig(JsonObject jsonObject) {
        if (!jsonObject.has("token")) jsonObject.addProperty("token", "");
        if (!jsonObject.has("version")) jsonObject.addProperty("version", CONFIG_VERSION);
        String token = jsonObject.get("token").getAsString();

        JsonObject jsonObject1 = new JsonObject();

        jsonObject1.addProperty("token", token);
        jsonObject.remove("token");

        for (Map.Entry<String, JsonElement> entry :jsonObject.entrySet()) {
            jsonObject1.add(entry.getKey(), entry.getValue());
        }

        if (!CONFIG_FOLDER.exists()) CONFIG_FOLDER.mkdirs();
        try (FileWriter fileWriter = new FileWriter(NEW_MAIN_FILE)) {
            fileWriter.write(gson.toJson(jsonObject1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveMessageConfig(JsonObject jsonObject) {
        if (!CONFIG_FOLDER.exists()) CONFIG_FOLDER.mkdirs();

        try (FileWriter fileWriter = new FileWriter(MESSAGE_CONFIG)) {
            fileWriter.write(gson.toJson(jsonObject));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveConfig(ConfigHolder configHolder) {
        LOGGER.info("Saving config files.");
        JsonObject mainObject = (JsonObject) gson.toJsonTree(configHolder.config.mainConfig);
        mainObject.addProperty("version", CONFIG_VERSION);
        if (configHolder.token != null) mainObject.addProperty("token", configHolder.token);

        saveMainConfig(mainObject);
        saveMessageConfig((JsonObject) gson.toJsonTree(configHolder.config.messageConfig));
    }

    public static class ConfigHolder {
        private Config config;
        private String token;

        protected ConfigHolder(Config config, String token) {
            this.config = config;
            this.token = token;
        }

        public Config getConfig() {
            return config;
        }

        public String getToken() {
            return token;
        }
    }
}
