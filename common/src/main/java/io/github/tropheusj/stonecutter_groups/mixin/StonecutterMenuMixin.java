package io.github.tropheusj.stonecutter_groups.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.datafixers.util.Pair;
import io.github.tropheusj.stonecutter_groups.*;
import net.minecraft.core.Registry;
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
	abstract void setupResultSlot();
	@Shadow
	public abstract int getNumRecipes();

	@Shadow protected abstract boolean isValidRecipeIndex(int id);

	@Shadow @Final private DataSlot selectedRecipeIndex;

	protected StonecutterMenuMixin(@Nullable MenuType<?> menuType, int i) {
		super(menuType, i);
	}

	@Unique
	private final DataSlot stonecutter_groups$selectedStack = DataSlot.standalone();

	@Unique
	private final List<ItemStack> stonecutter_groups$groupStacks = new ArrayList<>();

	@ModifyExpressionValue(method = "hasInputItem", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z", remap = false))
	private boolean stonecutter_groups$accountForGroupCount(boolean empty) {
		return empty && stonecutter_groups$groupStacks.isEmpty();
	}

	@Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At("TAIL"))
	private void stonecutter_groups$addSelectedDataSlot(int i, Inventory inventory, ContainerLevelAccess containerLevelAccess, CallbackInfo ci) {
		addDataSlot(stonecutter_groups$selectedStack);
	}

	@Inject(method = "setupRecipeList", at = @At("HEAD"))
	private void stonecutter_groups$clearOldGroups(Container input, ItemStack newInput, CallbackInfo ci) {
		stonecutter_groups$selectedStack.set(-1);
		stonecutter_groups$groupStacks.clear();
	}

	@Inject(method = "setupRecipeList", at = @At(value = "INVOKE_ASSIGN", shift = Shift.AFTER, target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipesFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/List;"))
	private void stonecutter_groups$setupNewGroups(Container input, ItemStack newInput, CallbackInfo ci) {
//		StonecutterGroupRegistry.ALL.forEach((id, group) -> {
//			if (group.matches(newInput)) {
//				group.entries().forEach(entry -> {
//					if (!entry.matches(newInput))
//						stonecutter_groups$groupStacks.add(entry.stack());
//				});
//			}
//		});
		stonecutter_groups$groupStacks.addAll(Registry.ITEM.stream().map(Item::getDefaultInstance).toList());
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
			// TODO this breaks shift clicking since it unsets the selected recipe, will need to check if current recipe can no longer apply
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
		if (!stonecutter_groups$groupStacks.isEmpty()) {
			int selected = stonecutter_groups$selectedStack.get();
			if (stonecutter_groups$validGroupStackIndex(selected)) {
				this.resultSlot.set(stonecutter_groups$groupStacks.get(selected));
			}
		}
	}

	@Unique
	private boolean stonecutter_groups$validGroupStackIndex(int index) {
		return index >= 0 && index < stonecutter_groups$groupStacks.size();
	}

	@Override
	public List<ItemStack> stonecutter_groups$getGroupStacks() {
		return stonecutter_groups$groupStacks;
	}

	@Override
	public int stonecutter_groups$selectedGroupStack() {
		return stonecutter_groups$selectedStack.get();
	}
}
