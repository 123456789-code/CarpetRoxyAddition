package com.Wang125510.ROXY.mixin.reintroduceWaterShiftPlace;

import com.Wang125510.ROXY.Rules;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
public class BucketItemMixin {
	@Shadow
	private Fluid content;

	@Shadow
	protected void playEmptySound(LivingEntity livingEntity, LevelAccessor levelAccessor, BlockPos blockPos) {}

	@Inject(method = "emptyContents", at = @At("HEAD"), cancellable = true)
	private void reintroduceWaterShiftPlace(LivingEntity livingEntity, Level level, BlockPos blockPos, BlockHitResult blockHitResult, CallbackInfoReturnable<Boolean> cir) {
		if (!Rules.reintroduceWaterShiftPlace) return;
		if (livingEntity == null || !livingEntity.isShiftKeyDown()) return;
		if (this.content != Fluids.WATER) return;

		BlockState blockState = level.getBlockState(blockPos);
		if (blockState.getBlock() instanceof LiquidBlockContainer container
				&& container.canPlaceLiquid(livingEntity, level, blockPos, blockState, this.content)) {
			container.placeLiquid(level, blockPos, blockState, Fluids.WATER.defaultFluidState());
			this.playEmptySound(livingEntity, level, blockPos);
			cir.setReturnValue(true);
		}
	}
}
