package com.Wang125510.ROXY.mixin.neverKillNamedEntity;

import com.Wang125510.ROXY.Rules;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.KillCommand;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(KillCommand.class)
public class KillCommandMixin {
	@Inject(method = "kill", at = @At("HEAD"), cancellable = true)
	private static void kill(CommandSourceStack commandSourceStack, Collection<? extends Entity> collection, CallbackInfoReturnable<Integer> cir) {
		if (!Rules.neverKillNamedEntity) { return; }

		int count = 0;
		for (Entity entity : collection) {
			if (!entity.hasCustomName()) {
				entity.kill(commandSourceStack.getLevel());
				count++;
			}
		}

		if (count == 1) {
			commandSourceStack.sendSuccess(() -> Component.translatable("commands.kill.success.single", ((Entity)collection.iterator().next()).getDisplayName()), true);
		} else {
			commandSourceStack.sendSuccess(() -> Component.translatable("commands.kill.success.multiple", collection.size()), true);
		}
		int finalCount = count;
		commandSourceStack.sendSuccess(() -> Component.literal("已跳过 " + (collection.size() - finalCount) + " 个已命名实体"), true);

		cir.setReturnValue(count);
		cir.cancel();
	}
}
