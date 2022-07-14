package io.github.tropheusj.stonecutter_groups;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StonecutterGroups {
	public static final String ID = "stonecutter_groups";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static void init() {
		Registry.register(Registry.RECIPE_TYPE, StonecutterGroupRecipeType.ID, StonecutterGroupRecipeType.INSTANCE);
		Registry.register(Registry.RECIPE_SERIALIZER, StonecutterGroupRecipeSerializer.ID, StonecutterGroupRecipeSerializer.INSTANCE);
	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(ID, path);
	}
}
