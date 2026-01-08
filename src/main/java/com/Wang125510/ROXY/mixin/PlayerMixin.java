package com.Wang125510.ROXY.mixin;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.Wang125510.ROXY.Rules;

@Mixin(Player.class)
public class PlayerMixin {
	@Inject(
			method = "getDestroySpeed",
			at = @At("RETURN"),
			cancellable = true
	)
	private void instantMining(BlockState block, CallbackInfoReturnable<Float> cir) {
		if (Rules.instantMining) {
			cir.setReturnValue(Float.MAX_VALUE / 10.0f); // 使用稍小的值避免溢出
		}
	}
}