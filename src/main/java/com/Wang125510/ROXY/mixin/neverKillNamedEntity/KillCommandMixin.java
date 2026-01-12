package com.Wang125510.ROXY.mixin.neverKillNamedEntity;

import com.Wang125510.ROXY.Rules;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.KillCommand;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.stream.Collectors;

@Mixin(KillCommand.class)
public class KillCommandMixin {
	@ModifyVariable(method = "kill", at = @At("HEAD"), argsOnly = true)
	private static Collection<Entity> modifyTargets(Collection<Entity> collection, @Share("filtered_count") LocalIntRef filtered_count) {
		if (!Rules.neverKillNamedEntity) { return collection; }

		Collection<Entity> filtered = collection.stream().filter(entity -> !entity.hasCustomName()).collect(Collectors.toList());
		filtered_count.set(collection.size() - filtered.size());
		return filtered;
	}

	@Inject(method = "kill", at = @At("TAIL"))
	private static void kill(CommandSourceStack commandSourceStack, Collection<? extends Entity> collection, CallbackInfoReturnable<Integer> cir, @Share("filtered_count") LocalIntRef filtered_count) {
		if (Rules.neverKillNamedEntity) {
			commandSourceStack.sendSuccess(() -> Component.literal("已跳过" + filtered_count.get() + "个已命名实体"), true);
		}
	}
}
