package com.refabriccryptography.mixin;

import com.refabriccryptography.PythonProxy;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(PlayerMoveC2SPacket.PositionAndOnGround.class)
public class SafeFallMixinPositionAndOnGround {
    @Inject(at = @At("HEAD"), method = "write", cancellable = true)
    protected void write(PacketByteBuf packetByteBuf_1, CallbackInfo info) throws IOException {
        if (PythonProxy.globalMap == null) {
            return;
        }

        int safeFall = Integer.parseInt(PythonProxy.globalMap.getOrDefault("safefall", "1"));
        if (safeFall == 0) {
            return;
        }

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null){
            return;
        }

        double x = ((SafeFallMixinAccessor)this).getX();
        double y = ((SafeFallMixinAccessor)this).getY();
        double z = ((SafeFallMixinAccessor)this).getZ();

        packetByteBuf_1.writeDouble(x);
        packetByteBuf_1.writeDouble(y);
        packetByteBuf_1.writeDouble(z);
        packetByteBuf_1.writeByte(1);
        info.cancel();
    }
}