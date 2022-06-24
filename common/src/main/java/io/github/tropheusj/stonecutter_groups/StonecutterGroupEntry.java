package io.github.tropheusj.stonecutter_groups;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public record StonecutterGroupEntry(ItemStack stack, long units, StonecutterGroup group) {
	public boolean matches(ItemStack stack) {
		if (!stack.sameItem(this.stack))
			return false;
		CompoundTag tag = this.stack.getTag();
		if (tag == null)
			return true;
		return Objects.equals(tag, stack.getTag());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		StonecutterGroupEntry that = (StonecutterGroupEntry) o;
		return units == that.units && Objects.equals(stack, that.stack);
	}

	@Override
	public int hashCode() {
		return Objects.hash(stack, units);
	}
}
