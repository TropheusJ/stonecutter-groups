package io.github.tropheusj.stonecutter_groups.forge;

import io.github.tropheusj.stonecutter_groups.StonecutterGroupRecipeSerializer.GroupEntry;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class UtilsImpl {
	public static Ingredient groupToIngredient(List<GroupEntry> entries, GroupEntry except) {
		return new ForgeStonecutterGroupIngredient(entries, except);
	}
}
