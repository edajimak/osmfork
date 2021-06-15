package net.osmand.plus.lrrp;

import android.os.AsyncTask;
import android.os.SystemClock;

import androidx.annotation.Nullable;

import com.ibm.icu.text.Transliterator;

import net.osmand.Location;
import net.osmand.PlatformUtil;
import net.osmand.binary.BinaryMapIndexReader;
import net.osmand.binary.GeocodingUtilities;
import net.osmand.binary.GeocodingUtilities.GeocodingResult;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.OsmandPlugin;
import net.osmand.plus.resources.ResourceManager;
import net.osmand.plus.settings.backend.ApplicationMode;
import net.osmand.plus.voice.CommandBuilder;
import net.osmand.plus.voice.CommandPlayer;
import net.osmand.router.RoutePlannerFrontEnd;
import net.osmand.router.RoutingConfiguration;
import net.osmand.router.RoutingContext;
import net.osmand.plus.resources.ResourceManager.BinaryMapReaderResource;
import net.osmand.util.MapUtils;

import org.apache.commons.logging.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class LrrpRequestHelper {
    private final static Log log = PlatformUtil.getLog(LrrpRequestHelper.class);

    private final OsmandApplication app;
    private final LrrpOsmandPlugin plugin;
    private boolean started = false;
    private Location lastLocation;
    private String lastHash;
    private long startTime;
    private LrrpRequestConfig config;

    private RoutingContext defCtx;
    private List<BinaryMapReaderResource> usedReaders = new ArrayList<>();

    private final String[] ttcDirections = new String[] {
            "north", "northeast",
            "east", "southeast",
            "south", "southwest",
            "west", "northwest", "north"
    };

    private final PointsCollection points = new PointsCollection();

    public LrrpRequestHelper(OsmandApplication app, LrrpOsmandPlugin plugin) {
        this.app = app;
        this.plugin = plugin;
    }

    public boolean isMonitoringEnabled() {
        return plugin.isActive();
    }

    public PointsCollection getPoints() {
        return points;
    }

    public void triggerLrrpLocation(Location location) {
        this.lastLocation = location;
        if (isMonitoringEnabled()) {
            if (!started) {
                startTime = System.currentTimeMillis()/1000;
                started = true;
                new LiveFetcher().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } else {
            started = false;
        }
    }

    private class LiveFetcher extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            long lastSendInterval = 0;
            while (isMonitoringEnabled()) {
                LrrpRequestConfig conf = getConfig();
                long currentTime = System.currentTimeMillis()/1000;
                if (currentTime - lastSendInterval > conf.maxSendInterval && conf.specUrl != null) {
                    lastSendInterval = System.currentTimeMillis()/1000;
                    fetchData(conf);
                }

                SystemClock.sleep(2500);

                playRoutes();
                // Auto disable plugin
                if (currentTime - startTime > conf.autoDeactivationTimeInterval) {
                    started = false;
                    OsmandPlugin.enablePlugin(null, app, plugin, false);
                    break;
                }
            }
            started = false;
            return null;
        }
    }

    private void playRoutes() {
        CommandPlayer player = plugin.getPlayer();
        if (null == player) {
            return;
        }

        CommandBuilder cb = player.newCommandBuilder();
        LrrpRequestConfig config = getConfig();
        List<String> commands = new ArrayList<>();

        for (PointsBucket pts : plugin.getPoints().toArray()) {
            if (pts.getLast() == null || pts.getLast().isExpire()) {
                continue;
            }

            LrrpPoint p = pts.getLast();
            double dist = p.distance(lastLocation.getLongitude(), lastLocation.getLatitude());
            int level = config.getTriggerLevel(dist);
            if (!p.isExpire() && pts.isTriggerLevel(level)) {
                pts.triggerLevel(level);
                commands.add(getAlertCommand(p));
            }
        }

        HashSet<String> array = new HashSet<>(commands);
        for (String comm : array) {
            cb.addFromString(comm);
        }
        if (array.size() > 0) {
            cb.play();
        }
    }

    public void fetchData(LrrpRequestConfig conf) {
        try {
            // Parse the URL and let the URI constructor handle proper encoding of special characters such as spaces
            URL url = new URL(conf.specUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);

            String postData = "s=" + (lastHash != null ? lastHash : "");
            byte[] postDataBytes = postData.getBytes("UTF-8");

            urlConnection.setRequestMethod("POST");
            if (conf.specHost != null) {
                urlConnection.setRequestProperty("Host", conf.specHost);
            }
            if (conf.authorization != null) {
                urlConnection.setRequestProperty("Authorization", conf.authorization);
            }

            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            urlConnection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            urlConnection.setDoOutput(true);
            urlConnection.getOutputStream().write(postDataBytes);

            if (urlConnection.getResponseCode() >= 300) {

                String msg = urlConnection.getResponseCode() + " : " + //$NON-NLS-1$//$NON-NLS-2$
                        urlConnection.getResponseMessage();
                log.warn("Error sending monitor request: " + msg);
            } else {
                InputStream is = urlConnection.getInputStream();
                StringBuilder responseBody = new StringBuilder();
                if (is != null) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8")); //$NON-NLS-1$
                    String s;
                    while ((s = in.readLine()) != null) {
                        responseBody.append(s);
                        responseBody.append("\n");
                    }
                    is.close();
                }
                String json = responseBody.toString();

                JSONObject obj = new JSONObject(json);
                lastHash = obj.getString("next");
                JSONArray coords = obj.getJSONArray("coords");
                synchronized (points) {
                    for (int i = 0 ; i < coords.length() ; i++){
                        try {
                            LrrpPoint point = LrrpPoint.create(coords.getJSONObject(i));
                            points.push(point);
                        } catch (Exception e) { }
                    }
                }
            }

            urlConnection.disconnect();
        } catch (Exception e) {
            log.warn("Failed connect to: " + e.getMessage(), e);
        }
    }

    private String getAlertCommand(LrrpPoint p) {
        double dist = p.distance(lastLocation.getLongitude(), lastLocation.getLatitude());
        double deg = p.bearingDeg(lastLocation.getLatitude(), lastLocation.getLongitude());
        int direction = (int) Math.round(deg/45.0);
        String command = "Attention in " + Math.round(dist/100)* 100 + " meters";
        if (ttcDirections.length > direction) {
            command += " in " + ttcDirections[direction];
        }
        if (getConfig().speakStreet) {
            List<GeocodingResult> result = reverseGeocoding(p.getLatitude(), p.getLongitude());
            if (null == result || result.size() == 0) {
                return command;
            }

            int match = 0;
            StringBuilder streetName = new StringBuilder();
            for (GeocodingResult geo : result) {
                if (null != geo.streetName && !geo.streetName.isEmpty() && geo.getDistance() < 500) {
                    match++;
                    streetName.append(geo.streetName).append(" ");
                    if (match >= 2) {
                        break;
                    }
                }
            }

            if (!streetName.toString().isEmpty()) {
                command += " on the street " + streetName.toString();
            }
        }

        return command;
    }

    private LrrpRequestConfig getConfig() {
        if (config != null && config.active + 30 > System.currentTimeMillis()/1000) {
            return config;
        }

        LrrpRequestConfig conf = new LrrpRequestConfig();
        conf.active = System.currentTimeMillis()/1000;

        try {
            String json = plugin.LRRP_CONNECTION_CONFIG.get();
            JSONObject c = new JSONObject(json);

            conf.specUrl = c.has("url") ? c.getString("url") : null;
            conf.specHost = c.has("host") ? c.getString("host") : null;
            conf.maxSendInterval = Math.max(c.has("interval") ? c.getInt("interval") : 20, 3);
            conf.authorization = c.has("authorization") ? c.getString("authorization") : null;
            if (c.has("autoDeactivationTimeInterval")) {
                conf.autoDeactivationTimeInterval = c.getInt("autoDeactivationTimeInterval");
            }

            // Triggers
            JSONArray array = c.optJSONArray("triggers");
            if (array != null) {
                int[] triggers = new int[array.length()];
                for (int i = 0; i < array.length(); ++i) {
                    triggers[i] = array.optInt(i);
                }
                if (triggers.length > 0) {
                    conf.triggerLevels = triggers;
                }
            }

        } catch (Exception e) { }

        config = conf;
        return conf;
    }

    private List<GeocodingResult> reverseGeocoding(double lat, double lon) {
        List<BinaryMapReaderResource> checkReaders = checkReaders(lat, lon, usedReaders);
        if (defCtx == null || checkReaders != usedReaders) {
            //initCtx(app, checkReaders, app.getSettings().getApplicationMode());
            BinaryMapIndexReader[] rs = new BinaryMapIndexReader[checkReaders.size()];
            if (rs.length > 0) {
                int i = 0;
                for (BinaryMapReaderResource rep : checkReaders) {
                    rs[i++] = rep.getReader(ResourceManager.BinaryMapReaderResourceType.STREET_LOOKUP);
                }

                RoutingConfiguration defCfg = app.getDefaultRoutingConfig().build("geocoding", 10,
                        new HashMap<String, String>());
                defCtx = new RoutePlannerFrontEnd().buildRoutingContext(defCfg, null, rs);
            } else {
                defCtx = null;
            }
        }
        if (defCtx == null) {
            return null;
        }

        try {
            return new GeocodingUtilities().reverseGeocodingSearch(defCtx, lat, lon, true);
        } catch (Exception e) {
            log.warn("Exception happened during reverseGeocoding", e);
            return null;
        }
    }

    private List<BinaryMapReaderResource> checkReaders(double lat, double lon,
                                                       List<BinaryMapReaderResource> ur) {
        List<BinaryMapReaderResource> res = ur;
        for(BinaryMapReaderResource t : ur ) {
            if(t.isClosed()) {
                res = new ArrayList<>();
                break;
            }
        }
        int y31 = MapUtils.get31TileNumberY(lat);
        int x31 = MapUtils.get31TileNumberX(lon);
        for(BinaryMapReaderResource r : app.getResourceManager().getFileReaders()) {
            if (!r.isClosed()) {
                BinaryMapIndexReader shallowReader = r.getShallowReader();
                if (shallowReader != null && shallowReader.containsRouteData(x31, y31, x31, y31, 15)) {
                    if (!res.contains(r)) {
                        res = new ArrayList<>(res);
                        res.add(r);
                    }
                }
            }
        }
        return res;
    }
}
