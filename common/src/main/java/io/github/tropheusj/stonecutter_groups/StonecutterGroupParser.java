package io.github.tropheusj.stonecutter_groups;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class StonecutterGroupParser {
	@Nullable
	public static StonecutterGroup parse(JsonObject json, ResourceLocation id) {
		Builder<StonecutterGroupEntry> builder = ImmutableList.builder();
		for (Entry<String, JsonElement> entry : json.entrySet()) {
			JsonElement element = entry.getValue();
			String key = entry.getKey().trim();
			long units = parseUnits(element, key, id);
			if (units == -1)
				continue;
			if (key.startsWith("%")) {
				// todo including other groups
			} else if (key.startsWith("#")) {
				List<ItemStack> items = parseTag(key.substring(1), element, id);
				if (items == null)
					continue;
				for (ItemStack stack : items) {
					builder.add(new StonecutterGroupEntry(stack, units));
				}
			} else {
				ItemStack stack = parseItem(key, element, id);
				if (stack == null)
					continue;
				builder.add(new StonecutterGroupEntry(stack, units));
			}
		}
		List<StonecutterGroupEntry> stacks = builder.build();
		if (stacks.isEmpty()) {
			StonecutterGroups.LOGGER.error("Stonecutter group [{}] is empty!", id);
			return null;
		}
		return new StonecutterGroup(stacks);
	}

	@Nullable
	public static ItemStack parseItem(String key, JsonElement element, ResourceLocation groupId) {
		ResourceLocation itemId = ResourceLocation.tryParse(key);
		Item item = Registry.ITEM.get(itemId);
		if (item == Items.AIR) {
			StonecutterGroups.LOGGER.error("Stonecutter group [{}] has an item which is not a valid ID: [{}]", groupId, key);
			return null;
		}
		ItemStack stack = item.getDefaultInstance();
		stack.setTag(parseNbt(element, groupId));
		return stack;
	}

	@Nullable
	public static List<ItemStack> parseTag(String key, JsonElement element, ResourceLocation groupId) {
		ResourceLocation tagId = ResourceLocation.tryParse(key);
		if (tagId == null) {
			StonecutterGroups.LOGGER.error("Stonecutter group [{}] has a tag which is not a valid ID: [{}]", groupId, key);
			return null;
		}
		Iterable<Holder<Item>> contents = Registry.ITEM.getTagOrEmpty(TagKey.create(Registry.ITEM_REGISTRY, tagId));
		List<ItemStack> stacks = new ArrayList<>();
		CompoundTag nbt = parseNbt(element, groupId);
		for (Holder<Item> item : contents) {
			ItemStack stack = item.value().getDefaultInstance();
			if (nbt != null)
				stack.setTag(nbt.copy());
			stacks.add(stack);
		}
		if (stacks.isEmpty()) {
			StonecutterGroups.LOGGER.error("Stonecutter group [{}] specifies an empty tag: [{}]", groupId, key);
			return null;
		}
		return stacks;
	}

	@Nullable
	public static CompoundTag parseNbt(JsonElement data, ResourceLocation groupId) {
		if (!(data instanceof JsonObject object))
			return null;
		JsonElement nbt = object.get("nbt");
		if (nbt != null && !nbt.isJsonNull() && nbt.isJsonPrimitive()) {
			String nbtString = nbt.getAsString();
			try {
				return TagParser.parseTag(nbtString);
			} catch (CommandSyntaxException e) {
				StonecutterGroups.LOGGER.error("Invalid NBT in group [{}]: [{}]", groupId, e.getMessage());
			}
		}
		return null;
	}

	public static long parseUnits(JsonElement element, String entryKey, ResourceLocation groupId) {
		if (element instanceof JsonPrimitive primitive) {
			return primitive.getAsLong();
		} else if (element instanceof JsonObject extended) {
			JsonElement units = extended.get("units");
			if (units != null && !units.isJsonNull() && units.isJsonPrimitive()) {
				return units.getAsLong();
			} else {
				StonecutterGroups.LOGGER.error("Units for a stonecutter group entry [{}] in group [{}] was not specified!", entryKey, groupId);
				return -1;
			}
		}
		StonecutterGroups.LOGGER.error("Units for a stonecutter group entry [{}] in group [{}] was invalid! Expected a number!", entryKey, groupId);
		return -1;
	}
}
