package io.github.tropheusj.stonecutter_groups.forge;

import com.google.gson.JsonObject;
import io.github.tropheusj.stonecutter_groups.StonecutterGroupIngredient;
import io.github.tropheusj.stonecutter_groups.StonecutterGroupRecipeSerializer.GroupEntry;
import io.github.tropheusj.stonecutter_groups.StonecutterGroups;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;

import java.util.List;

public class ForgeStonecutterGroupIngredient extends StonecutterGroupIngredient {
	public ForgeStonecutterGroupIngredient(List<GroupEntry> entries, GroupEntry except) {
		super(entries, except);
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return Serializer.INSTANCE;
	}

	public enum Serializer implements IIngredientSerializer<StonecutterGroupIngredient> {
		INSTANCE;

		public static final ResourceLocation ID = StonecutterGroups.id("stonecutter_group");

		@Override
		public StonecutterGroupIngredient parse(FriendlyByteBuf arg) {
			throw new IllegalStateException("StonecutterGroupIngredient serializer should never be used");
		}

		@Override
		public StonecutterGroupIngredient parse(JsonObject jsonObject) {
			throw new IllegalStateException("StonecutterGroupIngredient serializer should never be used");
		}

		@Override
		public void write(FriendlyByteBuf arg, StonecutterGroupIngredient arg2) {
			throw new IllegalStateException("StonecutterGroupIngredient serializer should never be used");
		}
	}
}
