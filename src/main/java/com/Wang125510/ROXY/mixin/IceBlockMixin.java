package com.Wang125510.ROXY.mixin;

import com.Wang125510.ROXY.Rules;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.level.block.IceBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(IceBlock.class)
public class IceBlockMixin {
	@ModifyExpressionValue(method = "playerDestroy",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;hasTag(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/tags/TagKey;)Z"))
	private boolean forceGlobalSilkTouch(boolean original) {
		if (Rules.alwaysSilkTouch) { return true; }
		return original;
	}
}
