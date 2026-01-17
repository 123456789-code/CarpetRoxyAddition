package com.Wang125510.ROXY.mixin.tradeWithoutXp;

import com.Wang125510.ROXY.Rules;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Villager.class)
public class VillagerMixin {
	@WrapOperation(
			method = "rewardTradeXp",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z")
	)
	private boolean neverRewardTradeXp(Level instance, Entity entity, Operation<Boolean> original) {
		if (Rules.tradeWithoutXp) {
			return true;
		}
		return original.call(instance, entity);
	}
}
