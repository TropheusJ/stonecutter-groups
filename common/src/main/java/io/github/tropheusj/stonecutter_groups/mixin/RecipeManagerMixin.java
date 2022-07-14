package io.github.tropheusj.stonecutter_groups.mixin;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import io.github.tropheusj.stonecutter_groups.StonecutterGroupRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {
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
}
