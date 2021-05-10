package net.osmand.plus.lrrp;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import net.osmand.Location;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.OsmandPlugin;
import net.osmand.plus.R;
import net.osmand.plus.activities.MapActivity;
import net.osmand.plus.settings.backend.CommonPreference;
import net.osmand.plus.settings.backend.OsmandSettings;
import net.osmand.plus.settings.fragments.BaseSettingsFragment;
import net.osmand.plus.views.OsmandMapTileView;

public class LrrpOsmandPlugin extends OsmandPlugin {

    public static final String ID = "lrrp.plugin";
    //private MapActivity mapActivity;
    private MapLpprLayer mapLayer;
    private final LrrpRequestHelper lrrpRequestHelper;

    public final CommonPreference<String> LRRP_CONNECTION_CONFIG;

    public LrrpOsmandPlugin(@NonNull OsmandApplication app) {
        super(app);
        lrrpRequestHelper = new LrrpRequestHelper(app, this);

        OsmandSettings set = app.getSettings();
        LRRP_CONNECTION_CONFIG = set.registerStringPreference("lrrp_connection_config", null);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return app.getString(net.osmand.plus.R.string.plugin_lrrp_name);
    }

    @Override
    public CharSequence getDescription() {
        return app.getString(net.osmand.plus.R.string.plugin_lrrp_name);
    }

    @Override
    public void updateLocation(Location location) {
        lrrpRequestHelper.triggerLrrpLocation(location);
    }

    @Override
    public void registerLayers(MapActivity activity) {
        //this.mapActivity = activity;
        if (mapLayer != null) {
            activity.getMapView().removeLayer(mapLayer);
        }

        mapLayer = new MapLpprLayer(activity, this);
        activity.getMapView().addLayer(mapLayer, 3.5f);
    }

    @Override
    public void updateLayers(OsmandMapTileView mapView, MapActivity activity) {
        if (isActive()) {
            if (mapLayer == null) {
                registerLayers(activity);
            } else if (!mapView.getLayers().contains(mapLayer)) {
                mapView.addLayer(mapLayer, 3.5f);
            }
            mapView.refreshMap();
        } else {
            if (mapLayer != null) {
                mapView.removeLayer(mapLayer);
                mapView.refreshMap();
                mapLayer = null;
            }
        }
    }

    public LrrpRequestHelper getLrrpRequestHelper() {
        return lrrpRequestHelper;
    }

    @Override
    public Drawable getAssetResourceImage() {
        return app.getUIUtilities().getIcon(R.drawable.ski_map);
    }

    @Override
    public BaseSettingsFragment.SettingsScreenType getSettingsScreenType() {
        return BaseSettingsFragment.SettingsScreenType.LRRP_SETTINGS;
    }
}
