package com.Wang125510.ROXY;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import org.apache.commons.io.IOUtils;

public class ComponentTranslate {
	private static final Gson GSON = new Gson();

	public static Map<String, String> getTranslationFromResourcePath(String lang) {
		InputStream langFile = ComponentTranslate.class
				.getClassLoader()
				.getResourceAsStream("assets/carpet_roxy_addition/lang/%s.json".formatted(lang));

		if (langFile == null) {
			return Collections.emptyMap();
		}

		try {
			String jsonData = IOUtils.toString(langFile, StandardCharsets.UTF_8);
			return GSON.fromJson(jsonData, new TypeToken<Map<String, String>>() {}.getType());
		} catch (IOException e) {
			return Collections.emptyMap();
		}
	}
}
