package io.github.tropheusj.stonecutter_groups.mixin;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import io.github.tropheusj.stonecutter_groups.StonecutterGroupRecipe;
import io.github.tropheusj.stonecutter_groups.StonecutterGroupRecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {
	// targets both .put() calls
	@WrapWithCondition(
			at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap$Builder;"),
			method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V"
	)
	private boolean stonecutter_groups$handleGroupRecipes(ImmutableMap.Builder<ResourceLocation, Recipe<?>> builder, /* ResourceLocation */ Object id, /* Recipe<?> */ Object recipe) {
		if (recipe instanceof StonecutterGroupRecipe group) {
			group.recipes.forEach(r -> builder.put(r.getId(), r));
			return false;
		}
		return true;
}

	// stonecutter group based recipes go into the wrong list. we need to move them.
	@Inject(at = @At(value = "INVOKE", target = "Ljava/util/Set;stream()Ljava/util/stream/Stream;"),
			method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void stonecutter_groups$correctRecipeTypes(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager,
													   ProfilerFiller profilerFiller, CallbackInfo ci,
													   Map<RecipeType<?>, Builder<ResourceLocation, Recipe<?>>> map2) {
		Builder<ResourceLocation, Recipe<?>> grouped = map2.remove(StonecutterGroupRecipeType.INSTANCE);
		if (grouped == null) {
			return;
		}
		Builder<ResourceLocation, Recipe<?>> stonecutting = map2.get(RecipeType.STONECUTTING);
		grouped.build().forEach((id, recipe) -> {
			if (!(recipe instanceof StonecutterGroupRecipe)) {
				stonecutting.put(id, recipe);
			}
		});
	}
}
