package io.github.tropheusj.stonecutter_groups;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record StonecutterGroup(List<StonecutterGroupEntry> entries) {
	@Nullable
	public StonecutterGroupEntry matching(ItemStack stack) {
		for (StonecutterGroupEntry entry : entries) {
			if (entry.matches(stack)) {
				return entry;
			}
		}
		return null;
	}
}
