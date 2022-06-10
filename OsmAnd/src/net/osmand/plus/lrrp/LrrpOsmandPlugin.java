package net.osmand.plus.lrrp;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import net.osmand.Location;
import net.osmand.StateChangedListener;
import net.osmand.plus.NavigationService;
import net.osmand.plus.OsmandApplication;

import net.osmand.plus.R;
import net.osmand.plus.activities.MapActivity;
import net.osmand.plus.plugins.OsmandPlugin;
import net.osmand.plus.routing.MutableVoiceAware;
import net.osmand.plus.settings.backend.ApplicationMode;
import net.osmand.plus.settings.backend.OsmandSettings;
import net.osmand.plus.settings.backend.preferences.CommonPreference;
import net.osmand.plus.settings.fragments.BaseSettingsFragment;
import net.osmand.plus.views.OsmandMapTileView;
import net.osmand.plus.voice.CommandPlayer;

public class LrrpOsmandPlugin extends OsmandPlugin implements MutableVoiceAware, StateChangedListener<Boolean> {

    public static final String ID = "lrrp.plugin";
    //private MapActivity mapActivity;
    private MapLpprLayer mapLayer;
    private final LrrpRequestHelper lrrpRequestHelper;

    public final CommonPreference<String> LRRP_CONNECTION_CONFIG;
    public final CommonPreference<Boolean> LRRP_CONNECTION_PLAY;
    public final CommonPreference<Boolean> LRRP_CONNECTION_DEBUG;

    private boolean enabledPlayer = false;

    public LrrpOsmandPlugin(@NonNull OsmandApplication app) {
        super(app);
        lrrpRequestHelper = new LrrpRequestHelper(app, this);

        OsmandSettings set = app.getSettings();
        LRRP_CONNECTION_CONFIG = set.registerStringPreference("lrrp_connection_config", null);
        LRRP_CONNECTION_PLAY = set.registerBooleanPreference("lrrp_connection_play", false);
        LRRP_CONNECTION_DEBUG = set.registerBooleanPreference("lrrp_connection_debug", false);
    }

    @Override
    public void disable(OsmandApplication app) {
        super.disable(app);
        if (app.getNavigationService() != null) {
            app.getNavigationService().stopIfNeeded(app, NavigationService.USED_BY_LRRP);
        }
        lrrpRequestHelper.disable();
        LRRP_CONNECTION_PLAY.set(false);
    }

    @Override
    public boolean init(@NonNull final OsmandApplication app, Activity activity) {
        super.init(app, activity);
        lrrpRequestHelper.triggerLrrpLocation(null);

        return true;
    }

    @Override
    public int getLogoResourceId() {
        return R.drawable.ic_plugin_nautical_map;
    }

    private void initSoundPlayer() {
        if (LRRP_CONNECTION_PLAY.get() && !enabledPlayer && mapLayer.getMapActivity() != null) {
            ApplicationMode mode = app.getSettings().DEFAULT_APPLICATION_MODE.get();
            app.initVoiceCommandPlayer(mapLayer.getMapActivity(), mode, null, true, true, true, true, this);
            enabledPlayer = true;
            app.startNavigationService(NavigationService.USED_BY_LRRP);
        }

        if (!LRRP_CONNECTION_PLAY.get()) {
            enabledPlayer = false;
            if (app.getNavigationService() != null) {
                app.getNavigationService().stopIfNeeded(app, NavigationService.USED_BY_LRRP);
            }
        }
    }

    public boolean isEnabledPlayer() {
        return enabledPlayer;
    }

    public CommandPlayer getPlayer() {
        if (!LRRP_CONNECTION_PLAY.get()) {
            return null;
        }

        return app.getPlayer();
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
    public void registerLayers(@NonNull Context context, MapActivity activity) {
        OsmandApplication app = (OsmandApplication) context.getApplicationContext();
        if (mapLayer != null) {
            app.getOsmandMap().getMapView().removeLayer(mapLayer);
        }

        mapLayer = new MapLpprLayer(context, activity, this);
        app.getOsmandMap().getMapView().addLayer(mapLayer, 3.5f);

        LRRP_CONNECTION_PLAY.addListener(this);
        LRRP_CONNECTION_CONFIG.addListener(lrrpRequestHelper);
    }

    @Override
    public void updateLayers(@NonNull Context context, MapActivity activity) {
        //OsmandMapTileView mapView
        OsmandMapTileView mapView = app.getOsmandMap().getMapView();
        if (isActive()) {
            if (mapLayer == null) {
                registerLayers(context, activity);
            } else if (!mapView.getLayers().contains(mapLayer)) {
                mapView.addLayer(mapLayer, 3.5f);
            }
            initSoundPlayer();
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

    public PointsCollection getPoints() {
        return lrrpRequestHelper.getPoints();
    }

    @Override
    public Drawable getAssetResourceImage() {
        return app.getUIUtilities().getIcon(R.drawable.ski_map);
    }

    @Override
    public BaseSettingsFragment.SettingsScreenType getSettingsScreenType() {
        return BaseSettingsFragment.SettingsScreenType.LRRP_SETTINGS;
    }

    @Override
    public boolean isMute() {
        return !LRRP_CONNECTION_PLAY.get();
    }

    @Override
    public void stateChanged(Boolean change) {
        initSoundPlayer();
    }
}
