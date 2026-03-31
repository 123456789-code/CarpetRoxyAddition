package com.Wang125510.ROXY.other.showRuleUpdate;

import carpet.CarpetServer;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.SettingsManager;
import com.Wang125510.ROXY.CarpetRoxyAddition;
import com.Wang125510.ROXY.Rules;
import com.Wang125510.ROXY.config.ConfigManager;
import com.Wang125510.ROXY.config.Configs;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class showRuleUpdate {
	private static final String MOD_ID = CarpetRoxyAddition.getModId();
	private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static List<String> addedRules = new ArrayList<>();

	public static void setup() {
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			if (Rules.showRuleUpdate) {
				List<String> oldRules = stringToList(Configs.ruleCache);
				List<String> newRules = getRules();
				addedRules = new ArrayList<>(newRules);
				addedRules.removeAll(oldRules);

				Configs.ruleCache = listToString(newRules);
				ConfigManager.saveConfig();
			}
		});

		// 玩家加入时发送新增规则信息
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			if (!Rules.showRuleUpdate) return;

			ServerPlayer player = handler.player;
			if (addedRules == null || addedRules.isEmpty()) {
				player.displayClientMessage(Component.literal("§a[ROXY] 此次服务器启动时没有新规则添加"), false);
			}
			else{
				StringBuilder messageBuilder = new StringBuilder("§a[ROXY] 服务器新增了以下地毯规则：\n");
				for (String rule : addedRules) {
					messageBuilder.append("§e- ").append(rule).append("\n");
				}
				messageBuilder.deleteCharAt(messageBuilder.length() - 1);
				player.displayClientMessage(Component.literal(messageBuilder.toString()), false);
			}
		});
	}

	private static List<String> getRules() {
		List<String> ruleNames = new ArrayList<>();

		// 方法1：通过 CarpetServer.settingsManager 获取（推荐）
		try {
			SettingsManager settingsManager = CarpetServer.settingsManager;
			if (settingsManager != null) {
				Collection<CarpetRule<?>> rules = settingsManager.getCarpetRules();
				if (rules != null && !rules.isEmpty()) {
					for (CarpetRule<?> rule : rules) {
						ruleNames.add(rule.name());
					}
					LOGGER.info("通过 SettingsManager 获取到 {} 条规则", ruleNames.size());
					return ruleNames;
				} else {
					LOGGER.warn("SettingsManager.getRules() 返回空，尝试回退反射方法");
				}
			} else {
				LOGGER.warn("CarpetServer.settingsManager 为 null，尝试回退反射方法");
			}
		} catch (Exception e) {
			LOGGER.error("通过 SettingsManager 获取规则失败", e);
		}

		// 方法2：反射扫描 CarpetSettings 类的 @Rule 字段（备用）
		try {
			Class<?> carpetSettingsClass = Class.forName("carpet.CarpetSettings");
			Field[] fields = carpetSettingsClass.getDeclaredFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(carpet.api.settings.Rule.class)) {
					ruleNames.add(field.getName());
				}
			}
			LOGGER.info("通过反射扫描 @Rule 字段获取到 {} 条规则", ruleNames.size());
		} catch (ClassNotFoundException e) {
			LOGGER.error("找不到 CarpetSettings 类，请确保 Carpet 模组已正确加载", e);
		} catch (Exception e) {
			LOGGER.error("反射扫描规则失败", e);
		}


		return ruleNames;
	}

	// 将规则列表转换为空格分隔的字符串
	private static String listToString(List<String> list) {
		StringBuilder str = new StringBuilder();
		for (String s : list) {
			str.append(s).append(" ");
		}
		return str.toString();
	}

	// 将缓存字符串还原为规则列表
	private static List<String> stringToList(String str) {
		List<String> ruleNames = new ArrayList<>();
		if (str == null || str.trim().isEmpty()) {
			return ruleNames;
		}
		String[] rules = str.split(" ");
		for (String rule : rules) {
			if (!rule.isEmpty()) {
				ruleNames.add(rule.trim());
			}
		}
		return ruleNames;
	}
}