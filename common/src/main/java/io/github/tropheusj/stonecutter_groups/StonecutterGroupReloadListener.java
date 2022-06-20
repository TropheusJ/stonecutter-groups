package io.github.tropheusj.stonecutter_groups;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public class StonecutterGroupReloadListener extends SimpleJsonResourceReloadListener {
	public StonecutterGroupReloadListener() {
		super(StonecutterGroups.GSON, "stonecutter_groups");
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> prepared, ResourceManager manager, ProfilerFiller profiler) {
		profiler.push("stonecutter group reload");
		StonecutterGroupRegistry.ALL.clear();
		prepared.forEach(this::handleGroup);
		profiler.pop();
	}

	private void handleGroup(ResourceLocation id, JsonElement json) {
		if (!(json instanceof JsonObject obj)) {
			StonecutterGroups.LOGGER.error("Stonecutter group [{}] is not an object", id);
			return;
		}
		StonecutterGroup group = StonecutterGroupParser.parse(obj, id);
		if (group != null)
			StonecutterGroupRegistry.ALL.put(id, group);
	}
}
