package com.Wang125510.ROXY.mixin.weatherNeverCreateSnow;

import com.Wang125510.ROXY.Rules;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
	@WrapOperation(
			method = "tickPrecipitation",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"
			)
	)
	private boolean dontSetBlockState(ServerLevel instance, BlockPos blockPos, BlockState blockState, Operation<Boolean> original) {
		if (Rules.weatherNeverCreateSnow && blockState.is(Blocks.SNOW)) {
			return true;
		}
		return original.call(instance, blockPos, blockState);
	}
}