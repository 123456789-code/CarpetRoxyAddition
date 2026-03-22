package com.Wang125510.ROXY.command;

import carpet.CarpetSettings;
import carpet.utils.CommandHelper;
import com.Wang125510.ROXY.Rules;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class DeletePlayerCommand{
	// 两步验证请求存储，key = 请求者的 UUID，value = 待删除玩家名 + 过期时间
	private static final Map<UUID, PendingDelete> pendingRequests = new ConcurrentHashMap<>();
	private static final long EXPIRE_TIME_MS = 30_000; // 30秒过期

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		LiteralArgumentBuilder<CommandSourceStack> command = literal("player")
			.requires((player) -> CommandHelper.canUseCommand(player, CarpetSettings.commandPlayer) && Rules.deletePlayerCommand)
			.then(argument("player", StringArgumentType.word())
				.then(literal("delete").executes(DeletePlayerCommand::execute)));
		dispatcher.register(command);
	}

	public static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		CommandSourceStack source = context.getSource();
		String targetName = context.getArgument("player", String.class);

		// 获取请求者 UUID（控制台用固定 UUID）
		UUID requesterId = source.getPlayer() != null ? source.getPlayer().getUUID() : UUID.nameUUIDFromBytes("console".getBytes());

		// 第一步：检查是否有待确认的请求
		if (!hasPendingRequest(requesterId)) {
			// 没有请求 → 创建请求
			addRequest(requesterId, targetName);
			source.sendSuccess(() -> Component.literal("§e[两步验证] 正在准备删除玩家 " + targetName + "，请再次输入相同命令以确认（30秒内有效）。"), false);
			return 1;
		}

		// 第二步：已有请求，执行确认
		PendingDelete pending = getAndRemoveRequest(requesterId);
		if (pending == null) {
			source.sendFailure(Component.literal("§c没有待确认的删除请求或请求已过期，请重新执行 /player <name> delete。"));
			return 0;
		}

		// 检查确认的玩家名是否与请求一致
		if (!pending.targetName.equals(targetName)) {
			source.sendFailure(Component.literal("§c确认的玩家名与请求不一致，请重新执行 /player " + pending.targetName + " delete"));
			return 0;
		}

		// 执行真正的删除流程
		return performDelete(source.getServer(), targetName, source);
	}

	private static int performDelete(MinecraftServer server, String targetName, CommandSourceStack source) {
		UUID targetUuid = server.getPlayerList().getPlayerByName(targetName).getUUID();
		if (targetUuid == null) {
			source.sendFailure(Component.literal("§c无法找到玩家 " + targetName + " 的 UUID，请检查玩家是否存在。"));
			return 0;
		}

		ServerPlayer player = server.getPlayerList().getPlayerByName(targetName);
		player.kill(player.level());

		server.execute(() -> {
			boolean success = deletePlayerData(server, targetUuid);
			if (success) {
				source.sendSuccess(() -> Component.literal("§a玩家 " + targetName + " 的数据已删除。"), true);
			} else {
				source.sendFailure(Component.literal("§c删除玩家数据失败。"));
			}
		});
		return 1;
	}

	private static void addRequest(UUID requesterId, String targetName) {
		pendingRequests.put(requesterId, new PendingDelete(targetName, System.currentTimeMillis() + EXPIRE_TIME_MS));
	}

	private static PendingDelete getAndRemoveRequest(UUID requesterId) {
		PendingDelete request = pendingRequests.remove(requesterId);
		if (request != null && request.expireTime > System.currentTimeMillis()) {
			return request;
		}
		return null;
	}

	private static boolean hasPendingRequest(UUID requesterId) {
		PendingDelete request = pendingRequests.get(requesterId);
		return request != null && request.expireTime > System.currentTimeMillis();
	}

	private static class PendingDelete {
		final String targetName;
		final long expireTime;
		PendingDelete(String targetName, long expireTime) {
			this.targetName = targetName;
			this.expireTime = expireTime;
		}
	}

	private static boolean deletePlayerData(MinecraftServer server, UUID uuid) {
		String fileName = uuid.toString();
		boolean allSuccess = true;
		Path root = server.getWorldPath(LevelResource.ROOT).getParent();

		allSuccess &= deleteFileSafely(root.resolve("playerdata").resolve(fileName + ".dat"));
		allSuccess &= deleteFileSafely(root.resolve("playerdata").resolve(fileName + ".dat_old"));
		allSuccess &= deleteFileSafely(root.resolve("advancements").resolve(fileName + ".json"));
		allSuccess &= deleteFileSafely(root.resolve("stats").resolve(fileName + ".json"));

		return allSuccess;
	}

	private static boolean deleteFileSafely(Path path) {
		try {
			Files.deleteIfExists(path);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}