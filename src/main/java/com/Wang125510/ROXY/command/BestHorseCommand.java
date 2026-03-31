package com.Wang125510.ROXY.command;

import com.Wang125510.ROXY.Rules;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.AbstractHorse;

import java.util.Objects;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class BestHorseCommand {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		LiteralArgumentBuilder<CommandSourceStack> command = literal("setbesthorse")
				.requires((player) -> Rules.setBestHorseCommand)
				.then(argument("horse", EntityArgument.entity())
						.executes(BestHorseCommand::setBestHorse)
				);
		dispatcher.register(command);
	}

	private static int setBestHorse(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		Entity entity= EntityArgument.getEntity(context, "horse");

		if (!(entity instanceof AbstractHorse horse)) {
			context.getSource().sendFailure(Component.literal("目标必须是一匹马！"));
			return 0;
		}

		Objects.requireNonNull(horse.getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(AbstractHorse.MAX_MOVEMENT_SPEED);
		Objects.requireNonNull(horse.getAttribute(Attributes.JUMP_STRENGTH)).setBaseValue(AbstractHorse.MAX_JUMP_STRENGTH);
		Objects.requireNonNull(horse.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(AbstractHorse.MAX_HEALTH);

		context.getSource().sendSuccess(() -> Component.literal("已将 " + horse.getName().getString() + " 设为满配马！"), true);
		return 1;
	}
}
