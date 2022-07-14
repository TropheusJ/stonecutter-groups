package io.github.tropheusj.stonecutter_groups;

import io.github.tropheusj.stonecutter_groups.StonecutterGroupRecipeSerializer.GroupEntry;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Despite the name, this class is only actually used as an intermediary storage for a group of StonecutterRecipes.
 */
public class StonecutterGroupRecipe implements Recipe<Container> {
	// used to generate unique IDs
	public static final AtomicInteger COUNTER = new AtomicInteger();

	public final ResourceLocation id;
	public final List<StonecutterRecipe> recipes;

	public StonecutterGroupRecipe(ResourceLocation id, List<GroupEntry> entries) {
		this.id = id;
		this.recipes = new ArrayList<>();
		for (GroupEntry entry : entries) {
			ResourceLocation recipeId = StonecutterGroups.id(id.getPath() + "/" + COUNTER.getAndIncrement());
			String group = id.getNamespace() + "/" + id.getPath();
			ItemStack output = entry.stack();
			Ingredient input = Utils.groupToIngredient(entries, entry);
			StonecutterRecipe recipe = new StonecutterRecipe(recipeId, group, input, output);
			recipes.add(recipe);
		}
	}

	@Override
	public boolean matches(Container inventory, Level world) {
		return false;
	}

	@Override
	public ItemStack assemble(Container inventory) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return false;
	}

	@Override
	public ItemStack getResultItem() {
		return ItemStack.EMPTY;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(Container inventory) {
		return NonNullList.create();
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return NonNullList.create();
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public String getGroup() {
		return Recipe.super.getGroup();
	}

	@Override
	public ItemStack getToastSymbol() {
		return ItemStack.EMPTY;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return StonecutterGroupRecipeSerializer.INSTANCE;
	}

	@Override
	public RecipeType<?> getType() {
		return StonecutterGroupRecipeType.INSTANCE;
	}

	@Override
	public boolean isIncomplete() {
		return false;
	}
}
