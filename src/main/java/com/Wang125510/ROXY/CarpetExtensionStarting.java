package com.Wang125510.ROXY;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.api.settings.SettingsManager;
import com.Wang125510.ROXY.command.BestHorseCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

import com.Wang125510.ROXY.RunPerTick.RunPerTick;
import com.Wang125510.ROXY.command.DeletePlayerCommand;

public class CarpetExtensionStarting implements CarpetExtension {
	private static final String MOD_ID = CarpetRoxyAddition.getModId();
	private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static SettingsManager settingsManager;
	private static final CarpetExtensionStarting INSTANCE = new CarpetExtensionStarting();

	public static CarpetExtensionStarting getInstance() { return INSTANCE; }

	@Override
	public String version() {
		return CarpetRoxyAddition.getModId();
	}

	public static void init() { CarpetServer.manageExtension(INSTANCE); }

	@Override
	public void onGameStarted() {
		settingsManager = new SettingsManager(CarpetExtensionStarting.getInstance().version(), MOD_ID, "Carpet Roxy Addition");
		CarpetServer.settingsManager.parseSettingsClass(Rules.class);
		LOGGER.info("Carpet Roxy Addition rules initialized");
	}

	@Override
	public void onTick(MinecraftServer server) {
		RunPerTick.runPerTick(server);
	}

	@Override
	public void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext) {
		DeletePlayerCommand.register(dispatcher);
		BestHorseCommand.register(dispatcher);
	}

	@Override
	public Map<String, String> canHasTranslations(String lang) {
		return ComponentTranslate.getTranslationFromResourcePath(lang);
	}
}