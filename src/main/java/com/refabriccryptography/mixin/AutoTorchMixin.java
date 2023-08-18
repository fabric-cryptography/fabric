package com.refabriccryptography.mixin;

import com.refabriccryptography.PythonProxy;
import com.refabriccryptography.block.MayInteract;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityPose;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.light.ChunkLightingView;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.registry.Registries;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.sqrt;

@Mixin(ClientPlayerEntity.class)
public class AutoTorchMixin {
	@Inject(at = @At("RETURN"), method = "sendMovementPackets ()V")
	private void sendMovementPackets(CallbackInfo info) {
		if (PythonProxy.globalMap == null) {
			return;
		}

		int autotorch = Integer.parseInt(PythonProxy.globalMap.getOrDefault("autotorch", "0"));
		if (autotorch == 0) {
			return;
		}

		MinecraftClient client = MinecraftClient.getInstance();
		if(client == null){
			return;
		}

		if(client.interactionManager != null && client.interactionManager.isBreakingBlock()){
			return;
		}

		ClientWorld world = client.world;
		if(world == null){
			return;
		}

		LightingProvider lightingProvider = client.world.getLightingProvider();
		if(lightingProvider.hasUpdates()){
			return;
		}

		ChunkLightingView view = lightingProvider.get(LightType.BLOCK);

		ClientPlayerEntity player = client.player;
		if(player == null){
			return;
		}

		double coolDownNow = Instant.now().getEpochSecond() * 1000 + Instant.now().getNano() / 1000.0 / 1000;
		double coolDownLast = Double.parseDouble(PythonProxy.globalMap.getOrDefault("autoplace_cooldown", "0.0"));

		if(coolDownNow - coolDownLast <= 250) {
			return;
		}

		PythonProxy.globalMap.put("autoplace_cooldown", String.valueOf(coolDownNow));

		boolean offHandReady = false;

		ItemStack offHandItemStack = player.getOffHandStack();
		Item offHandItem = offHandItemStack.getItem();

		if(offHandItemStack.getCount() != 0 && Registries.ITEM.getId(offHandItem).toString().equals("minecraft:torch")){
			offHandReady = true;
		}

		boolean mainHandReady = false;

		ItemStack mainHandItemStack = player.getMainHandStack();
		Item mainHandItem = mainHandItemStack.getItem();

		if(mainHandItemStack.getCount() != 0 && Registries.ITEM.getId(mainHandItem).toString().equals("minecraft:torch")){
			mainHandReady = true;
		}

		if(!offHandReady && !mainHandReady){
			return;
		}

		BlockPos playerBlockPos = player.getBlockPos();
		Vec3d playerEyePosStanding = new Vec3d(player.getX(), player.getY() + player.getEyeHeight(EntityPose.STANDING), player.getZ());
		Vec3d playerEyePosCrouching = new Vec3d(player.getX(), player.getY() + player.getEyeHeight(EntityPose.CROUCHING), player.getZ());

		List<BlockPos> list = new LinkedList<>();
		for(int i = -6; i <= 6; i++){
			int j_bound = (int) sqrt(36 - i * i);
			for(int j = -j_bound; j <= j_bound; j++){
				int k_bound = (int) sqrt(36 - i * i - j * j);
				for(int k = -k_bound; k <= k_bound; k++) {
					BlockPos blockPos = playerBlockPos.add(new Vec3i(i, j, k));

					if(playerEyePosStanding.distanceTo(new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ())) >= 5.9){
						continue;
					}

					if(playerEyePosCrouching.distanceTo(new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ())) >= 5.9){
						continue;
					}

					if(player.getPos().distanceTo(new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5)) >= 7.9){
						continue;
					}

					BlockState blockState = world.getBlockState(blockPos);

					if (!blockState.isOpaque()){
						continue;
					}

					if(!Block.sideCoversSmallSquare(world, blockPos, Direction.UP)){
						continue;
					}

					BlockState blockState_up = world.getBlockState(blockPos.add(0, 1, 0));

					if(!blockState_up.isAir()){
						continue;
					}

					BlockState blockState_up_up = world.getBlockState(blockPos.add(0, 2, 0));

					if(blockState_up_up.isOpaque()){
						continue;
					}

					list.add(blockPos);
				}
			}
		}

		Collections.shuffle(list);
		if(client.world == null){
			return;
		}

		boolean found = false;
		BlockPos foundBlockPos = null;
		BlockState foundBlockState = null;

		if(world.getRegistryKey() == World.OVERWORLD || world.getRegistryKey() == World.END) {
			for (BlockPos blockPos : list) {
				if (view.getLightLevel(blockPos.add(0, 1, 0)) == 0) {
					found = true;
					foundBlockPos = blockPos;
					foundBlockState = world.getBlockState(foundBlockPos);

					PythonProxy.LOGGER.info(String.format("Found block: %d %d %d", blockPos.getX(), blockPos.getY(), blockPos.getZ()));
					break;
				}
			}
		} else if(world.getRegistryKey() == World.NETHER) {
			for (BlockPos blockPos : list) {
				if (view.getLightLevel(blockPos.add(0, 1, 0)) < 8) {
					found = true;
					foundBlockPos = blockPos;
					foundBlockState = world.getBlockState(foundBlockPos);

					PythonProxy.LOGGER.info(String.format("Found block: %d %d %d", blockPos.getX(), blockPos.getY(), blockPos.getZ()));
					break;
				}
			}
		} else {
			player.sendMessage(Text.of("Auto torch is only supported in the overworld, the nether, and the end"));
			return;
		}

		boolean blockMayInteract = false;
		if(found && MayInteract.blockMayInteract(foundBlockState.getBlock())){
			blockMayInteract = true;
		}

		if(found) {
			if(client.interactionManager != null) {
				PythonProxy.globalMap.put("autoplace_cancel_interaction", "True");

				if(blockMayInteract) {
					player.networkHandler.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
				}

				Hand hand;
				if(offHandReady){
					hand = Hand.OFF_HAND;
				}else{
					hand = Hand.MAIN_HAND;
				}

				client.interactionManager.interactBlock(player, hand, new BlockHitResult(new Vec3d(foundBlockPos.getX() + 0.5, foundBlockPos.getY() + 1, foundBlockPos.getZ() + 0.5), Direction.UP, foundBlockPos, false));

				if(blockMayInteract) {
					player.networkHandler.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
				}

				PythonProxy.globalMap.put("autoplace_cancel_interaction", "False");
				player.sendMessage(Text.of("Torch placed"), true);
			}
		}
	}
}