package io.github.tropheusj.stonecutter_groups.fabric;

import com.google.gson.JsonObject;
import io.github.tropheusj.serialization_hooks.ingredient.CustomIngredient;
import io.github.tropheusj.serialization_hooks.ingredient.IngredientDeserializer;
import io.github.tropheusj.stonecutter_groups.StonecutterGroupIngredient;
import io.github.tropheusj.stonecutter_groups.StonecutterGroupRecipeSerializer.GroupEntry;
import io.github.tropheusj.stonecutter_groups.StonecutterGroups;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class FabricStonecutterGroupIngredient extends StonecutterGroupIngredient implements CustomIngredient {
	public FabricStonecutterGroupIngredient(List<GroupEntry> entries, GroupEntry except) {
		super(entries, except);
	}

	@Override
	public IngredientDeserializer getDeserializer() {
		return Deserializer.INSTANCE;
	}

	public enum Deserializer implements IngredientDeserializer {
		INSTANCE;

		public static final ResourceLocation ID = StonecutterGroups.id("stonecutter_group");

		@Override
		public Ingredient fromNetwork(FriendlyByteBuf buffer) {
			throw new IllegalStateException("StonecutterGroupIngredient serializer should never be used");
		}

		@Override
		public Ingredient fromJson(JsonObject object) {
			throw new IllegalStateException("StonecutterGroupIngredient serializer should never be used");
		}
	}
}
