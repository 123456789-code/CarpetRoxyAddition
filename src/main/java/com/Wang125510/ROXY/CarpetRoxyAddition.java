package com.Wang125510.ROXY;

import com.Wang125510.ROXY.config.ConfigManager;
import com.Wang125510.ROXY.other.showRuleUpdate;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CarpetRoxyAddition implements ModInitializer {
	private static final String MOD_ID = "carpet_roxy_addition";
	private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		try {
			Class.forName("carpet.CarpetServer");
			LOGGER.info("Carpet detected, extension ready!");
		} catch (ClassNotFoundException e) {
			LOGGER.warn("Carpet not found. Some features may not work.");
		}

		CarpetExtensionStarting.init();

		ConfigManager.initialize();

		LOGGER.info("Carpet Roxy Addition initialized");

		showRuleUpdate.setup();
	}

	public static String getModId() {
		return MOD_ID;
	}
}