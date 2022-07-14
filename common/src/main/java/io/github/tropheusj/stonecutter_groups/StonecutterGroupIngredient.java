package io.github.tropheusj.stonecutter_groups;

import com.google.gson.JsonObject;
import io.github.tropheusj.stonecutter_groups.StonecutterGroupRecipeSerializer.GroupEntry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public abstract class StonecutterGroupIngredient extends Ingredient {
	public StonecutterGroupIngredient(List<GroupEntry> entries, GroupEntry except) {
		super(entries.stream().filter(entry -> entry != except).map(GroupEntry::stack).map(ItemValue::new));
	}

	@Override
	public boolean test(@Nullable ItemStack arg) {
		if (arg == null) {
			return false;
		}
		ItemStack[] stacks = getItems();
		if (stacks.length == 0) {
			return arg.isEmpty();
		}
		for (ItemStack stack : stacks) {
			if (stack.sameItem(arg)) {
				// allow if there's no nbt requirement or if nbt matches
				if (!stack.hasTag() || Objects.equals(stack.getTag(), arg.getTag())) {
					return true;
				}
			}
		}
		return false;
	}
}
