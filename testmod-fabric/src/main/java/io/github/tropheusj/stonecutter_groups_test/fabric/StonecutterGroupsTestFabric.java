package io.github.tropheusj.stonecutter_groups_test.fabric;

import io.github.tropheusj.stonecutter_groups_test.StonecutterGroupsTest;
import net.fabricmc.api.ModInitializer;

public class StonecutterGroupsTestFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		StonecutterGroupsTest.init();
	}
}
