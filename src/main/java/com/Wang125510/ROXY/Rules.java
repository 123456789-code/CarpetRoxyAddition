package com.Wang125510.ROXY;

import carpet.api.settings.Rule;

public class Rules {
	public static final String BUGFIX = "bugfix";
	public static final String ROXY = "ROXY";
	public static final String SURVIVAL = "survival";
	public static final String CREATIVE = "creative";
	public static final String EXPERIMENTAL = "experimental";
	public static final String OPTIMIZATION = "optimization";
	public static final String FEATURE = "feature";
	public static final String COMMAND = "command";
	public static final String TNT = "tnt";
	public static final String DISPENSER = "dispenser";
	public static final String SCARPET = "scarpet";
	public static final String CLIENT = "client";

	@Rule(
			categories = {ROXY, SURVIVAL},
			options = {"true", "false"}
	)
	public static boolean instantMining = false;

	@Rule(
			categories = {ROXY, SURVIVAL},
			options = {"true", "false"}
	)
	public static boolean alwaysSilkTouch = false;

	@Rule(
			categories = {ROXY, SURVIVAL},
			options = {"true", "false"}
	)
	public static boolean highlightItemEntity = false;

	@Rule(
			categories = {ROXY, SURVIVAL},
			options = {"NULL", "Player", "Player&TNT", "Anything"}
	)
	public static String createNetherPortalWhitelist = "Anything";
}
