package com.Wang125510.ROXY.config;

import com.Wang125510.ROXY.CarpetRoxyAddition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
	private static final String MOD_ID = CarpetRoxyAddition.getModId();
	private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path CONFIG_PATH = FabricLoader.getInstance()
			.getConfigDir()
			.resolve(MOD_ID + ".json");


	public static void initialize() {
		loadConfig();
	}

	public static void loadConfig() {
		if (!Files.exists(CONFIG_PATH)) {
			LOGGER.info("配置文件不存在，创建默认配置");
			saveConfig(); // 保存默认配置
			return;
		}

		try {
			String content = Files.readString(CONFIG_PATH);
			JsonObject jsonObject = GSON.fromJson(content, JsonObject.class);

			// 遍历 Rules 类的所有静态字段
			for (Field field : Configs.class.getDeclaredFields()) {
				if (!Modifier.isStatic(field.getModifiers())) {
					continue;
				}

				// 检查是否有 @Rule 注解
				Config ruleAnnotation = field.getAnnotation(Config.class);
				if (ruleAnnotation == null) {
					continue;
				}

				String ruleName = ruleAnnotation.name();
				JsonElement element = jsonObject.get(ruleName);

				if (element != null && !element.isJsonNull()) {
					try {
						Object value = GSON.fromJson(element, field.getType());
						field.set(null, value);
						LOGGER.debug("加载规则 {} = {}", ruleName, value);
					} catch (Exception e) {
						LOGGER.error("加载规则 {} 时出错，使用默认值", ruleName, e);
					}
				}
			}

			LOGGER.info("配置文件加载完成");
		} catch (IOException e) {
			LOGGER.error("读取配置文件失败", e);
		}
	}

	public static void saveConfig() {
		JsonObject jsonObject = new JsonObject();

		// 遍历 Rules 类的所有静态字段
		for (Field field : Configs.class.getDeclaredFields()) {
			if (!Modifier.isStatic(field.getModifiers())) {
				continue;
			}

			// 检查是否有 @Rule 注解
			Config ruleAnnotation = field.getAnnotation(Config.class);
			if (ruleAnnotation == null) {
				continue;
			}

			String ruleName = ruleAnnotation.name();
			try {
				Object value = field.get(null);
				JsonElement element = GSON.toJsonTree(value);
				jsonObject.add(ruleName, element);
				LOGGER.debug("保存规则 {} = {}", ruleName, value);
			} catch (IllegalAccessException e) {
				LOGGER.error("获取规则 {} 的值失败", ruleName, e);
			}
		}

		try {
			String content = GSON.toJson(jsonObject);
			Files.createDirectories(CONFIG_PATH.getParent());
			Files.writeString(CONFIG_PATH, content);
			LOGGER.info("配置文件保存成功");
		} catch (IOException e) {
			LOGGER.error("保存配置文件失败", e);
		}
	}
}