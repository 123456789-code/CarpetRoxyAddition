package com.Wang125510.ROXY.mixin.alwaysSilkTouch;

import com.Wang125510.ROXY.Rules;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEnchantments.class)
public class ItemEnchantmentsMixin {
	@Inject(method = "getLevel", at = @At("HEAD"), cancellable = true)
	private void forceGlobalSilkTouch(Holder<Enchantment> holder, CallbackInfoReturnable<Integer> cir) {
		if (Rules.alwaysSilkTouch && holder.is(Enchantments.SILK_TOUCH)) {
			cir.setReturnValue(1);
		}
	}
}
