package io.github.tropheusj.stonecutter_groups.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.tropheusj.stonecutter_groups.StonecutterGroupEntry;
import io.github.tropheusj.stonecutter_groups.StonecutterMenuExtensions;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(StonecutterScreen.class)
public abstract class StonecutterScreenMixin extends AbstractContainerScreen<StonecutterMenu> {
	@Shadow
	private int startIndex;

	@Shadow
	private boolean displayRecipes;

	public StonecutterScreenMixin(StonecutterMenu abstractContainerMenu, Inventory inventory, Component component) {
		super(abstractContainerMenu, inventory, component);
	}

	@ModifyExpressionValue(method = { "getOffscreenRows", "isScrollBarActive" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/StonecutterMenu;getNumRecipes()I"))
	private int stonecutter_groups$accountForGroupCount(int recipes) {
		return recipes + ((StonecutterMenuExtensions) menu).stonecutterGroups$GetEntries().size();
	}

	/**
	 * @author Tropheus Jay
	 * @reason pickup with groups where recipes end, avoid an IOOB
	 */
	@Overwrite
	public void renderTooltip(PoseStack matrices, int x, int y) {
		super.renderTooltip(matrices, x, y);
		if (this.displayRecipes) {
			int i = this.leftPos + 52;
			int j = this.topPos + 14;
			int k = this.startIndex + 12;
			List<StonecutterRecipe> list = this.menu.getRecipes();
			List<StonecutterGroupEntry> entries = ((StonecutterMenuExtensions) menu).stonecutterGroups$GetEntries();
			int numRecipes = menu.getNumRecipes(); // moved to variable    // add group entry count to maximum
			for(int l = this.startIndex; l < k && l < (numRecipes + entries.size()); ++l) {
				int m = l - this.startIndex;
				int n = i + m % 4 * 16;
				int o = j + m / 4 * 18 + 2;
				if (x >= n && x < n + 16 && y >= o && y < o + 18) {
					// added check to avoid IOOB and defer to groups instead
					if (l < numRecipes) {
						ItemStack stack = list.get(l).getResultItem();
						// change render call to add tooltip
						this.renderTooltip(matrices, stonecutter_groups$appendToRecipeTooltip(stack, getTooltipFromItem(stack)), stack.getTooltipImage(), x, y);
					} else {
						StonecutterGroupEntry entry = entries.get(l - numRecipes);
						ItemStack stack = entry.stack();
						this.renderTooltip(matrices, stonecutter_groups$appendToEntryTooltip(entry, getTooltipFromItem(stack)), stack.getTooltipImage(), x, y);
					}
				}
			}
		}
	}

	@Unique
	private List<Component> stonecutter_groups$appendToRecipeTooltip(ItemStack result, List<Component> tooltips) {
		ItemStack input = menu.getSlot(0).getItem();
		if (input.isEmpty())
			return tooltips;
		tooltips.add(Component.empty());
		tooltips.add(Component.translatable("stonecutter_groups.requirement", 1, input.getHoverName()).withStyle(ChatFormatting.GOLD));
		tooltips.add(Component.translatable("stonecutter_groups.craftable", input.getCount() * result.getCount()).withStyle(ChatFormatting.GOLD));
		return tooltips;
	}

	@Unique
	private List<Component> stonecutter_groups$appendToEntryTooltip(StonecutterGroupEntry entry, List<Component> tooltips) {
		ItemStack input = menu.getSlot(0).getItem();
		if (input.isEmpty() || !(menu instanceof StonecutterMenuExtensions access))
			return tooltips;
		long units = entry.units();
		StonecutterGroupEntry inputEntry = access.stonecutter_groups$inputEntry(entry.group());
		if (inputEntry == null)
			return tooltips;
		long inputUnits = inputEntry.units();
		long inputUnitsAvailable = inputUnits * input.getCount();
		long craftable = inputUnitsAvailable / units;
		long required = Math.max(units / inputUnits, 1);
		tooltips.add(Component.empty());
		tooltips.add(Component.translatable("stonecutter_groups.requirement", required, input.getHoverName()).withStyle(ChatFormatting.GOLD));
		tooltips.add(Component.translatable("stonecutter_groups.craftable", craftable).withStyle(ChatFormatting.GOLD));
		return tooltips;
	}

	/**
	 * @author Tropheus Jay
	 * @reason to render buttons for group entries and to
	 */
	@Overwrite
	private void renderButtons(PoseStack matrices, int mouseX, int mouseY, int x, int y, int scrollOffset) {
		int numRecipes = menu.getNumRecipes(); // move to variable
		int groupEntries = ((StonecutterMenuExtensions) menu).stonecutterGroups$GetEntries().size();
																		// add group entry count to max
		for (int i = this.startIndex; i < scrollOffset && i < (numRecipes + groupEntries); ++i) {
			int j = i - this.startIndex;
			int k = x + j % 4 * 16;
			int l = j / 4;
			int m = y + l * 18 + 2;
			int n = this.imageHeight;
			if (i == this.menu.getSelectedRecipeIndex() || ( // add check for group entry selection
					i - numRecipes > 0 && i - numRecipes == ((StonecutterMenuExtensions) menu).stonecutter_groups$selectedStack()
					)) {
				n += 18;
			} else if (mouseX >= k && mouseY >= m && mouseX < k + 16 && mouseY < m + 18) {
				n += 36;
			}
			this.blit(matrices, k, m - 1, 0, n, 16, 18);
		}
	}

	/**
	 * @author Tropheus Jay
	 * @reason pickup with groups where recipes end, avoid an IOOB
	 */
	@Overwrite
	private void renderRecipes(int startX, int startY, int scrollOffset) {
		List<StonecutterRecipe> recipes = this.menu.getRecipes();
		List<StonecutterGroupEntry> entries = ((StonecutterMenuExtensions) menu).stonecutterGroups$GetEntries();
		int numRecipes = menu.getNumRecipes(); // moved count check to here    // add group entry count to maximum
		for (int i = this.startIndex; i < scrollOffset && i < (numRecipes + entries.size()); ++i) {
			int relativeIndex = i - this.startIndex;
			int x = startX + relativeIndex % 4 * 16;
			int row = relativeIndex / 4;
			int y = startY + row * 18 + 2;
			if (i < numRecipes) { // added check to avoid IOOB and defer to groups instead
				this.minecraft.getItemRenderer().renderAndDecorateItem(recipes.get(i).getResultItem(), x, y);
			} else {
				this.minecraft.getItemRenderer().renderAndDecorateItem(entries.get(i - numRecipes).stack(), x, y);
			}
		}
	}
}
