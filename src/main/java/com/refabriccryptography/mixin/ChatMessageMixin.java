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

            if (command.startsWith("mobglow")) {
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
                return;
            }

            if(command.startsWith("levitation")) {
                if(command.startsWith("levitation 2")){
                    PythonProxy.globalMap.put("levitation", "2");
                }else if(command.startsWith("levitation 1")){
                    PythonProxy.globalMap.put("levitation", "1");
                }else if(command.startsWith("levitation 0")){
                    PythonProxy.globalMap.put("levitation", "0");
                }else {
                    int currentlevitation = Integer.parseInt(PythonProxy.globalMap.getOrDefault("levitation", "0"));
                    if(currentlevitation == 0){
                        PythonProxy.globalMap.put("levitation", "1");
                    }else{
                        PythonProxy.globalMap.put("levitation", "0");
                    }

                    MinecraftClient client = MinecraftClient.getInstance();
                    ClientPlayerEntity player = client.player;

                    if (player != null) {
                        if (currentlevitation == 0) {
                            player.sendMessage(Text.of("levitation is on."), false);
                        } else {
                            player.sendMessage(Text.of("levitation is off."), false);
                        }
                    }
                }

                ci.cancel();
                return;
            }

            if (command.startsWith("safefall")) {
                int currentSafeFall = Integer.parseInt(PythonProxy.globalMap.getOrDefault("safefall", "1"));
                PythonProxy.globalMap.put("safefall", String.valueOf(1 - currentSafeFall));

                MinecraftClient client = MinecraftClient.getInstance();
                ClientPlayerEntity player = client.player;

                if (player != null) {
                    if (currentSafeFall == 0) {
                        player.sendMessage(Text.of("Safe falling is on."), false);
                    }else{
                        player.sendMessage(Text.of("Safe falling is off."), false);
                    }
                }

                ci.cancel();
                return;
            }

            if(command.startsWith("fixy")) {
                double currentFixY = Double.parseDouble(PythonProxy.globalMap.getOrDefault("fixy", "0.0"));

                MinecraftClient client = MinecraftClient.getInstance();
                ClientPlayerEntity player = client.player;

                if (player != null) {
                    if (currentFixY == 0.0) {
                        PythonProxy.globalMap.put("fixy", String.valueOf(player.getPos().getY()));
                        player.sendMessage(Text.of("FixY is on."), false);
                    }else{
                        PythonProxy.globalMap.put("fixy", "0.0");
                        PythonProxy.globalMap.put("levitation_fixy", "0");
                        player.sendMessage(Text.of("FixY is off."), false);
                    }
                }

                ci.cancel();
                return;
            }

            if (command.startsWith("autoattack")) {
                int currentAutoAttack = Integer.parseInt(PythonProxy.globalMap.getOrDefault("autoattack", "0"));
                PythonProxy.globalMap.put("autoattack", String.valueOf(1 - currentAutoAttack));

                MinecraftClient client = MinecraftClient.getInstance();
                ClientPlayerEntity player = client.player;

                if (player != null) {
                    if (currentAutoAttack == 0) {
                        player.sendMessage(Text.of("Auto attack is on."), false);
                    }else{
                        player.sendMessage(Text.of("Auto attack is off."), false);
                    }
                }

                ci.cancel();
            }
        }
    }
}