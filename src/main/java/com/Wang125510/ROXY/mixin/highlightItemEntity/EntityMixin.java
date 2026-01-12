package com.Wang125510.ROXY.mixin.highlightItemEntity;

import com.Wang125510.ROXY.Rules;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
	@Inject(method = "hasGlowingTag", at = @At("HEAD"), cancellable = true)
	private void alwaysHasGlowingTag(CallbackInfoReturnable<Boolean> cir) {
		Entity self = (Entity)(Object)this;

		if (Rules.highlightItemEntity && (self instanceof ItemEntity)) {
			cir.setReturnValue(true);
			cir.cancel();
		}
	}
}
