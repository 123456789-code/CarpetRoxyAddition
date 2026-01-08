package com.Wang125510.ROXY.mixin;

import com.Wang125510.ROXY.Rules;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.IceBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(IceBlock.class)
public class IceBlockMixin {
	@WrapOperation(method = "playerDestroy",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;hasTag(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/tags/TagKey;)Z"))
	private boolean forceGlobalSilkTouch(ItemStack itemStack, TagKey<Enchantment> tagKey, Operation<Boolean> original) {
		if (Rules.alwaysSilkTouch) {
			return true;
		}
		return original.call(itemStack, tagKey);
	}
}
