package io.github.tropheusj.stonecutter_groups;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public record StonecutterGroupEntry(ItemStack stack, long units) {
	public boolean matches(ItemStack stack) {
		if (!stack.sameItem(this.stack))
			return false;
		CompoundTag tag = this.stack.getTag();
		if (tag == null)
			return true;
		return Objects.equals(tag, stack.getTag());
	}
}
