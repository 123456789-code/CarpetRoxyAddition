package com.Wang125510.ROXY.mixin.skipTime;

import com.Wang125510.ROXY.Rules;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.SleepStatus;
import org.spongepowered.asm.mixin.Mixin;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
	@Unique
	private boolean shouldSkipTime(ServerLevel level) {
		String rule = Rules.skipTime;

		if (rule.equals("night") || rule.equals("night&thunder")) {
			if (level.isDarkOutside()) {
				return true;
			}
		}
		if (rule.equals("thunder") || rule.equals("night&thunder")) {
			if (level.isThundering()) {
				return true;
			}
		}
		return false;
	}


	@WrapOperation(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/players/SleepStatus;areEnoughSleeping(I)Z"
			)
	)
	private boolean areEnoughSleeping(SleepStatus sleepStatus, int i, Operation<Boolean> original) {
		if (shouldSkipTime((ServerLevel) (Object) this)) {
			return true;
		}
		else {
			return original.call(sleepStatus, i);
		}
	}

	@WrapOperation(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/players/SleepStatus;areEnoughDeepSleeping(ILjava/util/List;)Z"
			)
	)
	private boolean areEnoughDeepSleeping(SleepStatus sleepStatus, int i, List<ServerPlayer> players, Operation<Boolean> original) {
		if (shouldSkipTime((ServerLevel) (Object) this)) {
			return true;
		}
		else {
			return original.call(sleepStatus, i, players);
		}
	}
}
