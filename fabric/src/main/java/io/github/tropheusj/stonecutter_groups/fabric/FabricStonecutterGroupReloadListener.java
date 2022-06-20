package io.github.tropheusj.stonecutter_groups.fabric;

import io.github.tropheusj.stonecutter_groups.StonecutterGroupReloadListener;
import io.github.tropheusj.stonecutter_groups.StonecutterGroups;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.List;

public class FabricStonecutterGroupReloadListener extends StonecutterGroupReloadListener implements IdentifiableResourceReloadListener {
	public static final ResourceLocation ID = StonecutterGroups.id("reload_listener");
	public static final Collection<ResourceLocation> DEPENDENCIES = List.of(ResourceReloadListenerKeys.TAGS);
	public static final FabricStonecutterGroupReloadListener INSTANCE = new FabricStonecutterGroupReloadListener();

	@Override
	public ResourceLocation getFabricId() {
		return ID;
	}

	@Override
	public Collection<ResourceLocation> getFabricDependencies() {
		return DEPENDENCIES;
	}
}
