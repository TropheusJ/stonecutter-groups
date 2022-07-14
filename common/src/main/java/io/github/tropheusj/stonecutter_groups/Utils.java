package io.github.tropheusj.stonecutter_groups;

import dev.architectury.injectables.annotations.ExpectPlatform;
import io.github.tropheusj.stonecutter_groups.StonecutterGroupRecipeSerializer.GroupEntry;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class Utils {
	@ExpectPlatform
	public static Ingredient groupToIngredient(List<GroupEntry> entries, GroupEntry except) {
		throw new AssertionError("Architectury failed!");
	}
}
