package com.refabriccryptography.mixin;

import com.refabriccryptography.PythonProxy;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayNetworkHandler.class)
public class ChatMessageMixin {
    @Inject(at = @At("HEAD"), method = "sendChatCommand", cancellable = true)
    protected void chatListener(String command, CallbackInfo ci) {
        if (PythonProxy.globalMap != null) {
            if (command.startsWith("nightvision")) {
                int currentNV = Integer.parseInt(PythonProxy.globalMap.getOrDefault("night_vision", "0"));
                PythonProxy.globalMap.put("night_vision", String.valueOf(1 - currentNV));

                MinecraftClient client = MinecraftClient.getInstance();
                ClientPlayerEntity player = client.player;

                if (player != null) {
                    if (currentNV == 0) {
                        player.sendMessage(Text.of("Night vision is on."), false);
                    }else{
                        player.sendMessage(Text.of("Night vision is off."), false);
                    }
                }

                MinecraftClient.getInstance().worldRenderer.reload();

                ci.cancel();
                return;
            }

            if (command.startsWith("/mobglow")) {
                int currentMobGlowing = Integer.parseInt(PythonProxy.globalMap.getOrDefault("mobglow", "0"));
                PythonProxy.globalMap.put("mobglow", String.valueOf(1 - currentMobGlowing));

                MinecraftClient client = MinecraftClient.getInstance();
                ClientPlayerEntity player = client.player;

                if (player != null) {
                    if (currentMobGlowing == 0) {
                        player.sendMessage(Text.of("Mob glowing is on."), false);
                    }else{
                        player.sendMessage(Text.of("Mob glowing is off."), false);
                    }
                }

                ci.cancel();
                return;
            }

            if(command.startsWith("autotorch")) {
                int currentAutoTorch = Integer.parseInt(PythonProxy.globalMap.getOrDefault("autotorch", "0"));
                PythonProxy.globalMap.put("autotorch", String.valueOf(1 - currentAutoTorch));

                MinecraftClient client = MinecraftClient.getInstance();
                ClientPlayerEntity player = client.player;

                if (player != null) {
                    if (currentAutoTorch == 0) {
                        player.sendMessage(Text.of("Auto torch is on."), false);
                    }else{
                        player.sendMessage(Text.of("Auto torch is off."), false);
                    }
                }

                ci.cancel();
            }

        }
    }
}