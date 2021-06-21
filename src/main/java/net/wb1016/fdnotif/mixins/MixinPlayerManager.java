package net.wb1016.fdnotif.mixins;

import net.wb1016.fdnotif.events.PlayerJoinCallback;
import net.wb1016.fdnotif.events.PlayerLeaveCallback;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {

    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        PlayerJoinCallback.EVENT.invoker().onJoin(connection, player);
    }

    @Inject(method = "remove", at = @At("HEAD"))
    private void remove(ServerPlayerEntity player, CallbackInfo ci) {
        PlayerLeaveCallback.EVENT.invoker().onLeave(player);
    }

}
