package io.github.tropheusj.stonecutter_groups;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StonecutterGroups {
	public static final String ID = "stonecutter_groups";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static void init() {
	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(ID, path);
	}
}
