package io.github.tropheusj.stonecutter_groups;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public record StonecutterGroup(List<StonecutterGroupEntry> entries) {
	public boolean matches(ItemStack stack) {
		for (StonecutterGroupEntry entry : entries) {
			if (entry.matches(stack)) {
				return true;
			}
		}
		return false;
	}
}
