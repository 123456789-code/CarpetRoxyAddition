package com.Wang125510.ROXY;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.api.settings.SettingsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

public class CarpetExtensionStarting implements CarpetExtension {
	public static final String MOD_ID = CarpetRoxyAddition.getModId();
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
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
	public Map<String, String> canHasTranslations(String lang) {
		return ComponentTranslate.getTranslationFromResourcePath(lang);
	}
}