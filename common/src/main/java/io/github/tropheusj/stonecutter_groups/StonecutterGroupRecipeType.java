package io.github.tropheusj.stonecutter_groups;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;

public enum StonecutterGroupRecipeType implements RecipeType<StonecutterGroupRecipe> {
	INSTANCE;

	public static final ResourceLocation ID = StonecutterGroups.id("stonecutter_group");

	@Override
	public String toString() {
		return ID.toString();
	}
}
