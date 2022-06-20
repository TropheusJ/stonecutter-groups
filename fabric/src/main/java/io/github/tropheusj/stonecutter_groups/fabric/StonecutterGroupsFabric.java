package io.github.tropheusj.stonecutter_groups.fabric;

import io.github.tropheusj.stonecutter_groups.StonecutterGroups;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

public class StonecutterGroupsFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		StonecutterGroups.init();
		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(FabricStonecutterGroupReloadListener.INSTANCE);
	}
}
