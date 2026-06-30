package com.Wang125510.ROXY.mixin.netherPortalLocFix;

import com.Wang125510.ROXY.Rules;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.PortalProcessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Entity.class)
public class EntityMixin {
	@WrapOperation(
			method = "setAsInsidePortal",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/PortalProcessor;isInsidePortalThisTick()Z")
	)
	private boolean isNotInsidePortalThisTick(PortalProcessor portalProcess, Operation<Boolean> original) {
		if (!Rules.netherPortalLocFix.equals("off")) {
			return false;
		}
		else {
			return original.call(portalProcess);
		}
	}

	@Inject(
			method = "checkInsideBlocks(Ljava/util/List;Lnet/minecraft/world/entity/InsideBlockEffectApplier$StepBasedCollector;)V",
			at = @At("HEAD"),
			cancellable = true
	)
	private void checkInsideBlocks(List<Entity.Movement> list, InsideBlockEffectApplier.StepBasedCollector collector, CallbackInfo cir) {
		if (Rules.netherPortalLocFix.equals("off") || Rules.netherPortalLocFix.equals("weak")) {
			return;
		}

		Entity self = (Entity)(Object)this;
		AABB aABB = self.getBoundingBox();
		BlockPos min = BlockPos.containing(aABB.minX + 1.0E-7, aABB.minY + 1.0E-7, aABB.minZ + 1.0E-7);
		BlockPos max = BlockPos.containing(aABB.maxX - 1.0E-7, aABB.maxY - 1.0E-7, aABB.maxZ - 1.0E-7);

		if (self.level().hasChunksAt(min, max)) {
			BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
			boolean hasPortal = false;
			for (int x = min.getX(); x <= max.getX(); x++) {
				for (int y = min.getY(); y <= max.getY(); y++) {
					for (int z = min.getZ(); z <= max.getZ(); z++) {
						if (!self.isAlive()) {
							return;
						}
						pos.set(x, y, z);
						if (self.level().getBlockState(pos).is(Blocks.NETHER_PORTAL)) {
							hasPortal = true;
						}
					}
				}
			}
			if (!hasPortal) {
				return;
			}
			for (int x = min.getX(); x <= max.getX(); x++) {
				for (int y = min.getY(); y <= max.getY(); y++) {
					for (int z = min.getZ(); z <= max.getZ(); z++) {
						if (!self.isAlive()) {
							cir.cancel();
							return;
						}
						pos.set(x, y, z);
						BlockState state = self.level().getBlockState(pos);
						try {
							// 1.21.1 的 entityInside 多两个参数，传递默认值不影响遍历顺序
							state.entityInside(self.level(), pos, self, collector, true);
							self.onInsideBlock(state);
						} catch (Throwable t) {
							CrashReport report = CrashReport.forThrowable(t, "Colliding entity with block");
							CrashReportCategory category = report.addCategory("Block being collided with");
							CrashReportCategory.populateBlockDetails(category, self.level(), pos, state);
							self.fillCrashReportCategory(category);
							throw new ReportedException(report);
						}
					}
				}
			}
		}

		cir.cancel();
	}
}
