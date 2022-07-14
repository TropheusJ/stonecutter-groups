package io.github.tropheusj.stonecutter_groups.fabric;

import io.github.tropheusj.serialization_hooks.ingredient.IngredientDeserializer;
import io.github.tropheusj.stonecutter_groups.StonecutterGroups;
import io.github.tropheusj.stonecutter_groups.fabric.FabricStonecutterGroupIngredient.Deserializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.core.Registry;
import net.minecraft.server.packs.PackType;

public class StonecutterGroupsFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		StonecutterGroups.init();
		Registry.register(IngredientDeserializer.REGISTRY, Deserializer.ID, Deserializer.INSTANCE);
	}
}
