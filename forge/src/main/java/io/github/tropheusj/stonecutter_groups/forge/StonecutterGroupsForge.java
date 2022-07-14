package io.github.tropheusj.stonecutter_groups.forge;

import io.github.tropheusj.stonecutter_groups.StonecutterGroups;
import io.github.tropheusj.stonecutter_groups.forge.ForgeStonecutterGroupIngredient.Serializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.Mod;

@Mod(StonecutterGroups.ID)
public class StonecutterGroupsForge {
	public StonecutterGroupsForge() {
		StonecutterGroups.init();
		CraftingHelper.register(Serializer.ID, Serializer.INSTANCE);
	}
}
