package com.refabriccryptography.mixin;

import com.refabriccryptography.PythonProxy;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerInteractBlockMixin {
    @Inject(at = @At("HEAD"), method = "shouldCancelInteraction ()Z", cancellable = true)
    protected void shouldCancelInteraction(CallbackInfoReturnable<Boolean> info) {
        if (PythonProxy.globalMap == null) {
            return;
        }

        boolean autoplace_cancel_interaction = Boolean.parseBoolean(PythonProxy.globalMap.getOrDefault("autoplace_cooldown", "False"));

        if (autoplace_cancel_interaction) {
            info.setReturnValue(true);
        }
    }
}