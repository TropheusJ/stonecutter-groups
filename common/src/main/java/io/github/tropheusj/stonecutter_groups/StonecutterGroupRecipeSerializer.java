package io.github.tropheusj.stonecutter_groups;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public enum StonecutterGroupRecipeSerializer implements RecipeSerializer<StonecutterGroupRecipe> {
	INSTANCE;

	public static final ResourceLocation ID = StonecutterGroups.id("stonecutter_group");

	@Override
	public StonecutterGroupRecipe fromJson(ResourceLocation id, JsonObject json) {
		JsonElement maybeEntries = json.get("entries");
		if (!(maybeEntries instanceof JsonObject entries)) {
			throw new JsonSyntaxException("Stonecutter Group [" + id + "] expected an 'entries' object, found: " + maybeEntries);
		}
		List<GroupEntry> groupEntries = new ArrayList<>();
		for (Entry<String, JsonElement> entry : entries.entrySet()) {
			ResourceLocation itemId = ResourceLocation.tryParse(entry.getKey());
			Item item = itemId == null ? null :  Registry.ITEM.get(itemId);
			if (item == null) {
				throw new JsonSyntaxException("Stonecutter Group [" + id + "] expected a valid item, found: " + entry.getKey());
			}

			JsonElement value = entry.getValue();
			if (value instanceof JsonObject extended) {
				JsonElement maybeNbt = extended.get("nbt");
				if (maybeNbt == null) { // why would you do this? but you can
					long units = parseUnits(extended.get("units"), id, item);
					groupEntries.add(new GroupEntry(item.getDefaultInstance(), units));
					continue;
				}

				if (!(maybeNbt instanceof JsonPrimitive primitive)) {
					throw new JsonSyntaxException("Stonecutter Group [" + id + "] expected valid NBT for item [" + item + "], found: " + maybeNbt);
				}
				String nbt = primitive.getAsString();
				try {
					CompoundTag tag = TagParser.parseTag(nbt);
					ItemStack stack = item.getDefaultInstance();
					stack.setTag(tag);
					long units = parseUnits(extended.get("units"), id, item);
					groupEntries.add(new GroupEntry(stack, units));
				} catch (CommandSyntaxException e) {
					JsonSyntaxException ex = new JsonSyntaxException(
							"Stonecutter Group [" + id + "] expected valid NBT for item [" + item + "], found: " + nbt
					);
					ex.addSuppressed(e);
					throw ex;
				}

			} else { // not an object, should just be an integer
				long units = parseUnits(value, id, item);
				groupEntries.add(new GroupEntry(item.getDefaultInstance(), units));
			}
		}

		if (groupEntries.isEmpty()) {
			throw new JsonSyntaxException("Stonecutter Group [" + id + "] contains no entries!");
		}
		return new StonecutterGroupRecipe(id, groupEntries);
	}

	public static long parseUnits(@Nullable JsonElement maybeUnits, ResourceLocation groupId, Item item) {
		if (maybeUnits instanceof JsonPrimitive unitsJson && unitsJson.isNumber()) {
			return maybeUnits.getAsLong();
		}
		throw new JsonSyntaxException("Stonecutter Group [" + groupId + "] entry [" + item + " ] expected a number, found: " + maybeUnits);
	}

	@Override
	public StonecutterGroupRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
		throw new IllegalStateException("Tried to call fromNetwork for a StonecutterGroupRecipe, this is bad!");
	}

	@Override
	public void toNetwork(FriendlyByteBuf buf, StonecutterGroupRecipe recipe) {
		throw new IllegalStateException("Tried to call toNetwork for a StonecutterGroupRecipe, this is bad!");
	}

	public record GroupEntry(ItemStack stack, long units) {
	}
}
