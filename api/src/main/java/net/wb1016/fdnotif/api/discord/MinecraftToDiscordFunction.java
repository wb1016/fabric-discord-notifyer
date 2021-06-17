package net.wb1016.fdnotif.api.discord;

import net.wb1016.fdnotif.api.config.Config;
import net.wb1016.fdnotif.api.minecraft.CompatText;
import net.wb1016.fdnotif.api.minecraft.Message;

public interface MinecraftToDiscordFunction {

    MinecraftMessage handleText(Message text, Config config);
}
