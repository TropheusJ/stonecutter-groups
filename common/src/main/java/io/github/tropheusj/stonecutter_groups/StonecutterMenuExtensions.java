package io.github.tropheusj.stonecutter_groups;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface StonecutterMenuExtensions {
	List<ItemStack> stonecutter_groups$getGroupStacks();
	int stonecutter_groups$selectedGroupStack();
}
