package io.github.tropheusj.stonecutter_groups;

import java.util.List;

public interface StonecutterMenuExtensions {
	List<StonecutterGroupEntry> stonecutterGroups$GetEntries();
	StonecutterGroupEntry stonecutter_groups$inputEntry(StonecutterGroup group);
	int stonecutter_groups$selectedStack();
}
