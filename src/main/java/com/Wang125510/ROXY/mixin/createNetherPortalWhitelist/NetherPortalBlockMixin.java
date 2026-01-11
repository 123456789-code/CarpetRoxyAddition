package com.Wang125510.ROXY.mixin.createNetherPortalWhitelist;

import com.Wang125510.ROXY.Rules;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.BlockUtil;
import net.minecraft.world.level.portal.PortalForcer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(NetherPortalBlock.class)
public abstract class NetherPortalBlockMixin {
	@WrapOperation(
			method = "getExitPortal",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/portal/PortalForcer;createPortal(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction$Axis;)Ljava/util/Optional;"
			)
	)
	private Optional<BlockUtil.FoundRectangle> itemEntityCreateNetherPortalDisabled(
			PortalForcer forcer, BlockPos pos, Direction.Axis axis, Operation<Optional<BlockUtil.FoundRectangle>> original,
			ServerLevel world, Entity entity
	) {
		if (Rules.createNetherPortalWhitelist == "Player") {
			if (!(entity instanceof Player)) {
				return Optional.empty();
			}
		} else if (Rules.createNetherPortalWhitelist == "Player&TNT") {
			if (!(entity instanceof Player) && !(entity instanceof PrimedTnt)) {
				return Optional.empty();
			}
		} else if (Rules.createNetherPortalWhitelist == "NULL") {
			return Optional.empty();
		} else if (Rules.createNetherPortalWhitelist == "Anything") {
			return original.call(forcer, pos, axis);
		}

		return original.call(forcer, pos, axis);
	}
}
