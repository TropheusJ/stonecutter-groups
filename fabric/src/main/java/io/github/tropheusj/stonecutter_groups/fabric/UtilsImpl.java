package io.github.tropheusj.stonecutter_groups.fabric;

import io.github.tropheusj.stonecutter_groups.StonecutterGroupRecipeSerializer.GroupEntry;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class UtilsImpl {
	public static Ingredient groupToIngredient(List<GroupEntry> entries, GroupEntry except) {
		return new FabricStonecutterGroupIngredient(entries, except);
	}
}
