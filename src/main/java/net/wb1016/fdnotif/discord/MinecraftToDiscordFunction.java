package net.wb1016.fdnotif.discord;

import net.wb1016.fdnotif.cfg.Config;
import net.wb1016.fdnotif.minecraft.CompatText;
import net.wb1016.fdnotif.minecraft.Message;

public interface MinecraftToDiscordFunction {

    MinecraftMessage handleText(Message text, Config config);
}
