package com.refabriccryptography.mixin;

import com.refabriccryptography.PythonProxy;
import com.refabriccryptography.mod.AutoAttackPredicate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Instant;
import java.util.List;
import java.util.function.Predicate;

@Mixin(ClientPlayerEntity.class)
public abstract class AutoAttackMixin {
    @Inject(at = @At("RETURN"), method = "sendMovementPackets ()V")
    private void sendMovementPackets(CallbackInfo info) {
        if (PythonProxy.globalMap == null) {
            return;
        }

        int autoattack = Integer.parseInt(PythonProxy.globalMap.getOrDefault("autoattack", "0"));
        if (autoattack == 0) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if(client == null){
            return;
        }

        if(client.interactionManager == null){
            return;
        }

        ClientWorld world = client.world;
        if(world == null){
            return;
        }

        ClientPlayerEntity player = client.player;
        if(player == null){
            return;
        }

        if(player.getMainHandStack() == null){
            return;
        }

        if(!Registries.ITEM.getId(player.getMainHandStack().getItem()).toString().contains("sword")){
            return;
        }

        double coolDownNow = Instant.now().getEpochSecond() * 1000 + Instant.now().getNano() / 1000.0 / 1000;
        double coolDownLast = Double.parseDouble(PythonProxy.globalMap.getOrDefault("autoattack_cooldown", "0.0"));

        if(coolDownNow - coolDownLast <= 625) {
            return;
        }

        PythonProxy.globalMap.put("autoattack_cooldown", String.valueOf(coolDownNow));

        BlockPos blockPos = player.getBlockPos();
        int x = blockPos.getX();
        int y = blockPos.getY();
        int z = blockPos.getZ();

        Vec3d box_1 = new Vec3d(x - 6, y - 6, z - 6);
        Vec3d box_2 = new Vec3d(x + 6, y + 6, z + 6);

        Box box = new Box(box_1, box_2);

        Predicate<Entity> predicate = p -> (AutoAttackPredicate.shouldAutoAttack(p)) && (p.distanceTo(player) <= 6);
        List<Entity> target = player.getEntityWorld().getOtherEntities(player, box, predicate);

        if(!target.isEmpty()){
            client.interactionManager.attackEntity(player, target.get(0));
            player.sendMessage(Text.of("Auto attacked"), true);
        }
    }
}