package io.github.tropheusj.stonecutter_groups.forge;

import io.github.tropheusj.stonecutter_groups.StonecutterGroupReloadListener;
import io.github.tropheusj.stonecutter_groups.StonecutterGroups;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@Mod(StonecutterGroups.ID)
@EventBusSubscriber
public class StonecutterGroupsForge {
	public static final StonecutterGroupReloadListener LISTENER = new StonecutterGroupReloadListener();

	public StonecutterGroupsForge() {
		StonecutterGroups.init();
	}

	@SubscribeEvent
	public static void reload(AddReloadListenerEvent event) {
		event.addListener(LISTENER);
	}
}
