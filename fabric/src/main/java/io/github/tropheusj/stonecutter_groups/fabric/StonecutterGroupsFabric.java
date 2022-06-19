package io.github.tropheusj.stonecutter_groups.fabric;

import io.github.tropheusj.stonecutter_groups.StonecutterGroups;
import net.fabricmc.api.ModInitializer;

public class StonecutterGroupsFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		StonecutterGroups.init();
	}
}
