package com.refabriccryptography.mixin;

import com.refabriccryptography.PythonProxy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MobGlowingMixin {
    @Final
    @Shadow
    private EntityType<?> type;

    @Inject(at = @At("HEAD"), method = "isGlowing ()Z", cancellable = true)
    protected void isGlowing(CallbackInfoReturnable<Boolean> info){
        if(type.getSpawnGroup() == SpawnGroup.MONSTER) {
            boolean isMobGlowing = PythonProxy.globalMap != null && PythonProxy.globalMap.getOrDefault("mobglow", "0").equals("1");

            if (isMobGlowing) {
                info.setReturnValue(true);
            }
        }
    }
}