package net.osmand.plus.plugins.osmedit.helpers;

import androidx.annotation.Nullable;

import net.osmand.data.MapObject;
import net.osmand.osm.edit.Entity;
import net.osmand.osm.edit.EntityInfo;
import net.osmand.plus.plugins.osmedit.data.OsmPoint.Action;

import java.util.Set;

public interface OpenstreetmapUtil {

	EntityInfo getEntityInfo(long id);

	Entity commitEntityImpl(Action action, Entity n, EntityInfo info, String comment, boolean closeChangeSet, @Nullable Set<String> changedTags);

	void closeChangeSet();

	Entity loadEntity(MapObject mapObject);
}