package com.Wang125510.ROXY.mixin.netherPortalLocFix;

import com.Wang125510.ROXY.Rules;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PortalProcessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(PortalProcessor.class)
public abstract class PortalProcessorMixin {
	@WrapOperation(method = "getPortalDestination",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/Portal;getPortalDestination(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/portal/TeleportTransition;"
			)
	)
	private TeleportTransition getPortalDestination(Portal portal, ServerLevel serverLevel, Entity entity, BlockPos blockPos, Operation<TeleportTransition> original) {
		if (Rules.netherPortalLocFix.equals("radical")) {
			BlockPos lastContacted = this.computeLastContactedPortal(serverLevel, entity);
			if (lastContacted != null) {
				return original.call(portal, serverLevel, entity, lastContacted);
			}
		}
		return original.call(portal, serverLevel, entity, blockPos);
	}

	@Unique
	private BlockPos computeLastContactedPortal(ServerLevel level, Entity entity) {
		AABB bb = entity.getBoundingBox();
		int minX = Mth.floor(bb.minX);
		int maxX = Mth.floor(bb.maxX - 1.0E-7);
		int minY = Mth.floor(bb.minY);
		int maxY = Mth.floor(bb.maxY - 1.0E-7);
		int minZ = Mth.floor(bb.minZ);
		int maxZ = Mth.floor(bb.maxZ - 1.0E-7);

		BlockPos last = null;

		// 三重循环按 x → y → z 升序（与 1.20.4 的 checkInsideBlocks 一致）
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					BlockPos pos = new BlockPos(x, y, z);
					BlockState state = level.getBlockState(pos);
					if (state.getBlock() == Blocks.NETHER_PORTAL) {
						last = pos; // 每次覆盖，最后保存的就是最后一个
					}
				}
			}
		}
		return last;
	}
}