package io.github.tropheusj.stonecutter_groups.mixin;

import io.github.tropheusj.stonecutter_groups.StonecutterGroup;
import io.github.tropheusj.stonecutter_groups.StonecutterGroupEntry;
import io.github.tropheusj.stonecutter_groups.StonecutterGroupRegistry;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(StonecutterMenu.class)
public abstract class StonecutterMenuMixin {
	@Shadow
	@Final
	Slot inputSlot;

	@Shadow
	private ItemStack input;

//	@Unique
//	private final List<StonecutterGroup> stonecutter_groups$groups = new ArrayList<>();
	@Unique
	private Map<ItemStack, StonecutterGroupEntry> stonecutter_groups$stacksToDisplay = new HashMap<>();

	@Inject(method = "setupRecipeList", at = @At("HEAD"))
	private void stonecutter_groups$clearOldGroups(Container input, ItemStack newInput, CallbackInfo ci) {
//		stonecutter_groups$groups.clear();
		stonecutter_groups$stacksToDisplay.clear();
	}

	@Inject(method = "setupRecipeList", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipesFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/List;"))
	private void stonecutter_groups$setupNewGroups(Container input, ItemStack newInput, CallbackInfo ci) {
		StonecutterGroupRegistry.ALL.forEach((id, group) -> {
			for (StonecutterGroupEntry entry : group.entries()) {
				if (entry.matches(newInput)) {
//					stonecutter_groups$groups.add(group);
					stonecutter_groups$stacksToDisplay.put(entry.stack(), entry);
				}
			}
		});
	}

	@ModifyArg(method = "slotsChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
	private Item stonecutter_groups$setupOnCountChange(Item item) {
		ItemStack newStack = inputSlot.getItem();
		ItemStack oldStack = input;
		if (newStack.getCount() != oldStack.getCount()) {
			// we want to setup if counts change since that can allow different recipes
			// to force a setup we need the item to be different
			return !newStack.is(Items.AIR) ? Items.AIR : Items.STICK;
		}
		return item; // let the normal check happen
	}

	/**
	 * @author Tropheus Jay
	 * @reason account for stonecutter groups
	 */
	@Overwrite
	public boolean isValidRecipeIndex(int id) {
		return id >= 0 && id < stonecutter_groups$stacksToDisplay.size();
	}
}
