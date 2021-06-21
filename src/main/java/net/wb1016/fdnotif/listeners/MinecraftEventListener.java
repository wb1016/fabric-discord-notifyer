package net.wb1016.fdnotif.listeners;

import net.wb1016.fdnotif.fdnotif;
import net.wb1016.fdnotif.events.*;

import net.dv8tion.jda.api.utils.MarkdownSanitizer;

public class MinecraftEventListener {
    public void init() {
            PlayerAdvancementCallback.EVENT.register((playerEntity, advancement) -> {
            if(fdnotif.config.announceAdvancements && advancement.getDisplay() != null && advancement.getDisplay().shouldAnnounceToChat() && playerEntity.getAdvancementTracker().getProgress(advancement).isDone()  && !fdnotif.stop) {
                switch (advancement.getDisplay().getFrame()) {
                    case GOAL -> fdnotif.textChannel.sendMessage(fdnotif.config.advancementGoal.replace("%playername%", MarkdownSanitizer.escape(playerEntity.getEntityName())).replace("%advancement%", MarkdownSanitizer.escape(advancement.getDisplay().getTitle().getString()))).queue();
                    case TASK -> fdnotif.textChannel.sendMessage(fdnotif.config.advancementTask.replace("%playername%", MarkdownSanitizer.escape(playerEntity.getEntityName())).replace("%advancement%", MarkdownSanitizer.escape(advancement.getDisplay().getTitle().getString()))).queue();
                    case CHALLENGE -> fdnotif.textChannel.sendMessage(fdnotif.config.advancementChallenge.replace("%playername%", MarkdownSanitizer.escape(playerEntity.getEntityName())).replace("%advancement%", MarkdownSanitizer.escape(advancement.getDisplay().getTitle().getString()))).queue();
                }
            }
        });

        PlayerDeathCallback.EVENT.register((playerEntity, damageSource) -> {
            if(fdnotif.config.announceDeaths && !fdnotif.stop){
                fdnotif.textChannel.sendMessage(fdnotif.config.deathMessage.replace("%deathmessage%",MarkdownSanitizer.escape(damageSource.getDeathMessage(playerEntity).getString())).replace("%playername%", MarkdownSanitizer.escape(playerEntity.getEntityName()))).queue();
            }
        });

        PlayerJoinCallback.EVENT.register((connection, playerEntity) -> {
            if(fdnotif.config.announcePlayers && !fdnotif.stop){
                fdnotif.textChannel.sendMessage(fdnotif.config.joinServer.replace("%playername%", MarkdownSanitizer.escape(playerEntity.getEntityName()))).queue();
            }
        });

        PlayerLeaveCallback.EVENT.register((playerEntity) -> {
            if(fdnotif.config.announcePlayers && !fdnotif.stop){
                fdnotif.textChannel.sendMessage(fdnotif.config.leftServer.replace("%playername%", MarkdownSanitizer.escape(playerEntity.getEntityName()))).queue();
            }
        });
    }
}
