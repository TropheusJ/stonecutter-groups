package io.github.tropheusj.stonecutter_groups.mixin;

import io.github.tropheusj.stonecutter_groups.*;
import net.minecraft.world.inventory.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

@Mixin(StonecutterMenu.class)
public abstract class StonecutterMenuMixin extends AbstractContainerMenu implements StonecutterMenuExtensions {
	protected StonecutterMenuMixin(@Nullable MenuType<?> type, int syncId) {
		super(type, syncId);
	}

	@Override
	public int stonecutter_groups$selectedStack() {
		return 0;
	}
}
