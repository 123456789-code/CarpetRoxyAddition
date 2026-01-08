package com.Wang125510.ROXY;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CarpetRoxyAddition implements ModInitializer {
	public static final String MOD_ID = "carpet_roxy_addition";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		try {
			Class.forName("carpet.CarpetServer");
			LOGGER.info("Carpet detected, extension ready!");
		} catch (ClassNotFoundException e) {
			LOGGER.warn("Carpet not found. Some features may not work.");
		}

		CarpetExtensionStarting.init();

		LOGGER.info("Carpet Roxy Addition initialized");
	}

	public static String getModId() {
		return MOD_ID;
	}
}