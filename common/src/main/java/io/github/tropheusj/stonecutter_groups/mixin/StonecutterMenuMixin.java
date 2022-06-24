package io.github.tropheusj.stonecutter_groups.mixin;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.tropheusj.stonecutter_groups.*;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(StonecutterMenu.class)
public abstract class StonecutterMenuMixin extends AbstractContainerMenu implements StonecutterMenuExtensions {
	@Shadow
	@Final
	Slot inputSlot;
	@Shadow
	private ItemStack input;
	@Shadow
	@Final
	Slot resultSlot;
	@Shadow
	@Final
	private DataSlot selectedRecipeIndex;

	@Shadow
	abstract void setupResultSlot();
	@Shadow
	public abstract int getNumRecipes();
	@Shadow
	protected abstract boolean isValidRecipeIndex(int id);

	protected StonecutterMenuMixin(@Nullable MenuType<?> menuType, int i) {
		super(menuType, i);
	}

	@Unique
	private final DataSlot stonecutter_groups$selectedStack = DataSlot.standalone();
	@Unique // list of all entries from all groups the input item is in
	private final List<StonecutterGroupEntry> stonecutter_groups$entries = new ArrayList<>();
	@Unique // map of input entries to groups. ex. if stone is part of 2 groups, this holds each stone entry mapped to the group it comes from.
	private final BiMap<StonecutterGroupEntry, StonecutterGroup> stonecutter_groups$currentGroups = HashBiMap.create(1);

	@ModifyExpressionValue(method = "hasInputItem", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z", remap = false))
	private boolean stonecutter_groups$accountForGroupCount(boolean empty) {
		return empty && stonecutter_groups$entries.isEmpty();
	}

	@Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At("TAIL"))
	private void stonecutter_groups$addSelectedDataSlot(int i, Inventory inventory, ContainerLevelAccess containerLevelAccess, CallbackInfo ci) {
		addDataSlot(stonecutter_groups$selectedStack);
		stonecutter_groups$selectedStack.set(-1);
	}

	@Inject(method = "setupRecipeList", at = @At("HEAD"))
	private void stonecutter_groups$clearOldGroups(Container input, ItemStack newInput, CallbackInfo ci) {
		if (newInput.isEmpty()) {
			stonecutter_groups$entries.clear();
			stonecutter_groups$selectedStack.set(-1);
		}
		stonecutter_groups$currentGroups.clear();
	}

	@Inject(method = "setupRecipeList", at = @At(value = "INVOKE_ASSIGN", shift = Shift.AFTER, target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipesFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/List;"))
	private void stonecutter_groups$setupNewGroups(Container input, ItemStack newInput, CallbackInfo ci) {
		int selected = stonecutter_groups$selectedStack.get();
		StonecutterGroupEntry lastSelected = selected == -1 ? null : stonecutter_groups$entries.get(selected);
		stonecutter_groups$entries.clear();
		StonecutterGroupRegistry.ALL.forEach((id, group) -> {
			StonecutterGroupEntry inputEntry = group.matching(newInput);
			if (inputEntry != null) {
				stonecutter_groups$currentGroups.put(inputEntry, group);
				long unitsAvailable = inputEntry.units() * newInput.getCount();
				group.entries().forEach(entry -> {
					// don't include self
					if (entry != inputEntry && unitsAvailable >= entry.units()) {
						stonecutter_groups$entries.add(entry);
					}
				});
			}
		});
		if (lastSelected != null) {
			int newIndex = stonecutter_groups$entries.indexOf(lastSelected);
			if (newIndex != -1) {
				stonecutter_groups$selectedStack.set(newIndex);
				return;
			}
		}
		stonecutter_groups$selectedStack.set(-1);
	}

	@Inject(method = "clickMenuButton", at = @At("RETURN"))
	private void stonecutter_groups$handleGroupClicking(Player player, int id, CallbackInfoReturnable<Boolean> cir) {
		if (isValidRecipeIndex(id)) {
			stonecutter_groups$selectedStack.set(-1);
		}
		id -= getNumRecipes();
		if (stonecutter_groups$validGroupStackIndex(id)) {
			stonecutter_groups$selectedStack.set(id);
			selectedRecipeIndex.set(-1);
			setupResultSlot();
		}
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

	@Inject(
			method = "setupResultSlot",
			at = @At(
					value = "INVOKE",
					shift = Shift.AFTER,
					target = "Lnet/minecraft/world/inventory/Slot;set(Lnet/minecraft/world/item/ItemStack;)V",
					ordinal = 1
			)
	)
	private void stonecutter_groups$setupResult(CallbackInfo ci) {
		if (!stonecutter_groups$entries.isEmpty()) {
			int selected = stonecutter_groups$selectedStack.get();
			if (stonecutter_groups$validGroupStackIndex(selected)) {

				this.resultSlot.set(stonecutter_groups$entries.get(selected).stack().copy());
			}
		}
	}

	@ModifyExpressionValue(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Ljava/util/Optional;isPresent()Z", remap = false))
	private boolean stonecutter_groups$allowGroupShiftClicks(boolean recipePresent, Player player, int index) {
		if (recipePresent) {
			return true;
		}
		Slot slot = this.slots.get(index);
		ItemStack stack = slot.getItem();
		for (StonecutterGroup group : StonecutterGroupRegistry.ALL.values()) {
			if (group.matching(stack) != null) {
				return true;
			}
		}
		return false;
	}

	@Unique
	private boolean stonecutter_groups$validGroupStackIndex(int index) {
		return index >= 0 && index < stonecutter_groups$entries.size();
	}

	@Override
	public List<StonecutterGroupEntry> stonecutterGroups$GetEntries() {
		return stonecutter_groups$entries;
	}

	@Override
	public int stonecutter_groups$selectedStack() {
		return stonecutter_groups$selectedStack.get();
	}

	@Override
	public StonecutterGroupEntry stonecutter_groups$inputEntry(StonecutterGroup group) {
		return stonecutter_groups$currentGroups.inverse().get(group);
	}
}
