package net.osmand.plus.lrrp;

import android.os.AsyncTask;
import android.os.SystemClock;

import net.osmand.Location;
import net.osmand.PlatformUtil;
import net.osmand.StateChangedListener;
import net.osmand.binary.BinaryMapIndexReader;
import net.osmand.binary.GeocodingUtilities;
import net.osmand.binary.GeocodingUtilities.GeocodingResult;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.OsmandPlugin;
import net.osmand.plus.resources.ResourceManager;
import net.osmand.plus.voice.CommandBuilder;
import net.osmand.plus.voice.CommandPlayer;
import net.osmand.router.RoutePlannerFrontEnd;
import net.osmand.router.RoutingConfiguration;
import net.osmand.router.RoutingContext;
import net.osmand.plus.resources.ResourceManager.BinaryMapReaderResource;
import net.osmand.util.MapUtils;

import org.apache.commons.logging.Log;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LrrpRequestHelper implements StateChangedListener<String> {
    private final static Log log = PlatformUtil.getLog(LrrpRequestHelper.class);

    private final OsmandApplication app;
    private final LrrpOsmandPlugin plugin;
    private boolean started = false;
    private Location lastLocation;

    private Location lastStreetLocation;
    private String lastStreetName;
    private String lastBorder;

    private WebSocketClient wsClient;
    private final AtomicBoolean wsClientInit = new AtomicBoolean(false);
    private final AtomicLong wsClientTimeout = new AtomicLong(0);
    private final AtomicLong wsClientPing = new AtomicLong(0);
    private final Queue<LrrpPoint> mQueuedEvents = new ConcurrentLinkedQueue<>();
    private String lastHash;
    private long startTime;
    private LrrpRequestConfig config;

    private RoutingContext defCtx;
    private final List<BinaryMapReaderResource> usedReaders = new ArrayList<>();

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

    public void disable() {
        lastBorder = null;
        started = false;
        lastHash = null;
        mQueuedEvents.clear();
        synchronized (points) {
            points.clear();
        }
        config = null;

        if (wsClient != null) {
            wsClientInit.set(false);
            try {
                wsClient.close();
            } catch (Exception e) { }
            wsClient = null;
        }
    }

    @Override
    public void stateChanged(String change) {
        this.config = null;
    }

    private class LiveFetcher extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            long lastSendInterval = 0;
            while (isMonitoringEnabled() && started) {
                LrrpRequestConfig conf = getConfig();
                long currentTime = System.currentTimeMillis()/1000;
                if (currentTime - lastSendInterval > conf.maxSendInterval && conf.specUrl != null) {
                    lastSendInterval = currentTime;
                    fetchData(conf);
                }

                if (conf.specWs != null) {
                    try {
                        if (currentTime - wsClientTimeout.get() > 60) {
                            wsClientTimeout.set(currentTime);
                            wsClientInit.set(false);
                        }
                        runWebsocket(conf);
                        byte[] area = getBorderParam();
                        if (wsClient != null && area != null && lastBorder != null && !lastBorder.equals(byteArrayToHex(area))) {
                            lastBorder = byteArrayToHex(area);
                            wsClient.send(area);
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }

                try {
                    processQueuedEvents();
                    SystemClock.sleep(2500);
                    playRoutes();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }

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


    private void processQueuedEvents() {
        if (mQueuedEvents.size() == 0) {
            return;
        }

        LrrpRequestConfig conf = getConfig();
        synchronized (points) {
            LrrpPoint point = mQueuedEvents.poll();
            while (point != null) {
                if (lastLocation == null
                    || !conf.onlyNearest
                    || point.distance(lastLocation.getLongitude(), lastLocation.getLatitude()) <
                        (conf.nearestRadius > 0 ? conf.nearestRadius : 45000)
                ) {
                    points.push(point);
                }

                point = mQueuedEvents.poll();
            }
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
            if (pts.getLast().isShort() && pts.getUseCount() < 2) {
                continue;
            }

            LrrpPoint p = pts.getLast();
            double dist = p.distance(lastLocation.getLongitude(), lastLocation.getLatitude());
            double prevDist = pts.getLastDist();
            pts.setLastDist(dist);
            
            if (dist <= prevDist) {
                if (p.getSpeed() > 70) {
                    dist = dist/config.triggerS2;
                } else if (p.getSpeed() > 40) {
                    dist = dist/config.triggerS1;
                }
            }

            int maxTriggerDist = config.triggerLevels.length > 0 ? config.triggerLevels[config.triggerLevels.length - 1] : 0;
            if (dist < maxTriggerDist * config.triggerR1 && config.triggerR1 > 1.01) {
                String street = pts.getStreetName();
                if (null == street) {
                    street = getStreetName(p.getLatitude(), p.getLongitude(), 1);
                    street = street != null ? street : "";
                    pts.setStreetName(street);
                }

                if (!street.equals("") && street.equals(getCurrentStreetName())) {
                    dist = dist/config.triggerR1;
                }
            }

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

    private void runWebsocket(LrrpRequestConfig conf) throws Exception {
        if (wsClientInit.get()) {
            return;
        }

        long current = System.currentTimeMillis()/1000;
        if (current - wsClientPing.get() < 5) {
            return;
        }

        wsClientInit.set(true);
        wsClientTimeout.set(current);
        wsClientPing.set(current);

        URI uri = new URI(conf.specWs);
        Map<String,String> httpHeaders = new HashMap<>();
        Matcher match = Pattern.compile("token=(\\w+)").matcher(uri.getQuery());
        if (match.find()) {
            String token = match.group(1);
            httpHeaders.put("Authorization", "Token " + token);
        }

        byte[] border = getBorderParam();
        if (border != null) {
            lastBorder = byteArrayToHex(border);
            httpHeaders.put("X-Var", lastBorder);
        }

        match = Pattern.compile("host=([\\w.-]+)").matcher(uri.getQuery());
        if (match.find()) {
            httpHeaders.put("Host", match.group(1));
        }

        URI serverUri = new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), uri.getPath(), null, null);
        wsClient = new WebSocketClient(serverUri, httpHeaders) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
            }

            @Override
            public void onMessage(ByteBuffer bytes) {
                wsClientTimeout.set(System.currentTimeMillis()/1000);
                try {
                    processSocketBuffer(bytes);
                } catch (Exception e) {}
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                log.info("Connection was close: " + reason);
                wsClientPing.set(System.currentTimeMillis()/1000);
                wsClientInit.set(false);
            }

            @Override
            public void onError(Exception ex) {
                log.warn("Error: " + ex);
                wsClientInit.set(false);
                wsClient.close();
            }
        };

        match = Pattern.compile("proxy=([\\w.\\-:]+)").matcher(uri.getQuery());
        if (match.find()) {
            String proxyHost = match.group(1);
            if (proxyHost != null && !proxyHost.isEmpty()) {
                URI proxyUrl = new URI("http://" + proxyHost);
                Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyUrl.getHost(), proxyUrl.getPort()));
                wsClient.setProxy(proxy);
            }

            // remove dns resolver
            String host = serverUri.getHost();
            if (host.endsWith(".i2p") || host.endsWith(".onion") || host.endsWith(".ygg")) {
                wsClient.setDnsResolver(null);
            }
        }

        wsClient.connectBlocking();
    }

    private void processSocketBuffer(ByteBuffer bytes)
    {
        while (bytes.hasRemaining()) {
            byte op = bytes.get();
            switch (op) {
                case 0x1C:
                    byte[] payload = new byte[18];
                    bytes.get(payload);

                    long lat = ((payload[10] & 0xFFL) << 24) + ((payload[11] & 0xFF) << 16)
                            + ((payload[12] & 0xFF) << 8) + (payload[13] & 0xFF);
                    long lon = ((payload[14] & 0xFFL) << 24) + ((payload[15] & 0xFF) << 16)
                            + ((payload[16] & 0xFF) << 8) + (payload[17] & 0xFF);

                    LrrpPoint point = new LrrpPoint(lat, lon);
                    point.time = ((payload[0] & 0xFF) << 24) + ((payload[1] & 0xFF) << 16)
                            + ((payload[2] & 0xFF) << 8) + (payload[3] & 0xFF);
                    if (payload[4] != 0) {
                        point.mCC = payload[4] & 0xFF;
                    }

                    point.from = ((payload[7] & 0xFF) << 16) + ((payload[8] & 0xFF) << 8)
                            + (payload[9] & 0xFF);
                    point.tg = ((payload[5] & 0xFF) << 8) + (payload[6] & 0xFF);
                    point.resolvedFrom = point.from;
                    if (point.from <= 0 || point.mCC > 0) {
                        point.isShort = true;
                        point.from = point.calculateCRC(0, 0);
                    }

                    mQueuedEvents.add(point);
                    break;
                default:
                    return;
            }
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
                            if (lastLocation != null
                                && conf.onlyNearest
                                && point.distance(lastLocation.getLongitude(), lastLocation.getLatitude()) >
                                    (conf.nearestRadius > 0 ? conf.nearestRadius : 45000)
                            ) {
                                continue;
                            }
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

    private String getCurrentStreetName() {
        if (null == lastLocation) {
            return "";
        }

        if (lastStreetLocation != null && lastStreetLocation.distanceTo(lastLocation) > 600) {
            lastStreetName = null;
        }

        if (null == lastStreetName) {
            lastStreetName = getStreetName(lastLocation.getLatitude(), lastLocation.getLongitude(), 1);
            lastStreetName = lastStreetName != null ? lastStreetName : "";
            lastStreetLocation = lastLocation;
        }

        return lastStreetName;
    }

    private String getAlertCommand(LrrpPoint p) {
        double dist = p.distance(lastLocation.getLongitude(), lastLocation.getLatitude());
        double deg = p.bearingDeg(lastLocation.getLatitude(), lastLocation.getLongitude());
        int direction = (int) Math.round(deg/45.0);
        LrrpRequestConfig config = getConfig();
        String attention = config.aliasMap.get(p.tg);
        if (attention == null || attention.isEmpty()) {
            attention = "attention";
        }

        TransBuilder trans = new TransBuilder(config.trans);
        trans.add(attention)
            .add("in")
            .add((Math.round(dist/100)* 100) + "")
            .add("meters");

        if (ttcDirections.length > direction) {
            trans.add("on").add(ttcDirections[direction]);
        }
        if (getConfig().speakStreet) {
            String streetName = getStreetName(p.getLatitude(), p.getLongitude(), 2);

            if (null != streetName && !streetName.isEmpty()) {
                trans.add("on the street").add(streetName);
            }
        }

        return trans.trans();
    }

    private String getStreetName(double lat, double lon, int matchCount) {
        List<GeocodingResult> result = reverseGeocoding(lat, lon);
        if (null == result || result.size() == 0) {
            return null;
        }

        int match = 0;
        StringBuilder streetName = new StringBuilder();
        for (GeocodingResult geo : result) {
            if (null != geo.streetName && !geo.streetName.isEmpty() && geo.getDistance() < 500) {
                match++;
                streetName.append(geo.streetName).append(" ");
                if (match >= matchCount) {
                    break;
                }
            }
        }

        return streetName.toString();
    }

    private byte[] getBorderParam() {
        byte[] payload = new byte[7];
        payload[0] = 0x11;
        LrrpRequestConfig config = getConfig();

        if (lastLocation == null || config.nearestRadius == 0) {
            return null;
        }

        payload[1] = (byte) ((config.nearestRadius >> 8) & 0xFF);
        payload[2] = (byte) (config.nearestRadius);

        int lat = (int)(((lastLocation.getLatitude() + 180.0)/360.0) * 65536);
        int lon = (int)(((lastLocation.getLongitude())/360.0) * 65536);

        payload[3] = (byte) ((lat >> 8) & 0xFF);
        payload[4] = (byte) (lat & 0xFF);
        payload[5] = (byte) ((lon >> 8) & 0xFF);
        payload[6] = (byte) (lon & 0xFF);

        return payload;
    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b: a) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private LrrpRequestConfig getConfig() {
        if (config != null) {
            return config;
        }

        LrrpRequestConfig conf = new LrrpRequestConfig();
        conf.active = System.currentTimeMillis()/1000;

        String json = null;
        try {
            json = plugin.LRRP_CONNECTION_CONFIG.get();
            if (config != null && config.json.equals(json)) {
                return config;
            }
        } catch (Exception e) {
            return conf;
        }

        try {
            JSONObject c = new JSONObject(json);
            conf.json = json;
            conf.specUrl = c.has("url") ? c.optString("url") : null;
            conf.specHost = c.has("host") ? c.optString("host") : null;
            conf.specWs = c.has("ws") ? c.getString("ws") : null;

            conf.maxSendInterval = Math.max(c.has("interval") ? c.getInt("interval") : 20, 3);
            conf.authorization = c.has("authorization") ? c.getString("authorization") : null;
            if (c.has("autoDeactivationTimeInterval")) {
                conf.autoDeactivationTimeInterval = c.getInt("autoDeactivationTimeInterval");
            }
            if (c.has("onlyNearest") && !c.optBoolean("onlyNearest")) {
                conf.onlyNearest = false;
            }
            if (c.has("nearestRadius")) {
                conf.nearestRadius = c.optInt("nearestRadius");
            }
            if (c.has("aliases")) {
                conf.processAliases(c.optJSONObject("aliases"));
            }

            // Triggers
            JSONArray array = c.optJSONArray("triggerLevels");
            if (array != null) {
                int[] triggers = new int[array.length()];
                for (int i = 0; i < array.length(); ++i) {
                    triggers[i] = array.optInt(i);
                }
                if (triggers.length > 0) {
                    conf.triggerLevels = triggers;
                }
            }

            // Triggers config
            if (c.has("triggerS1")) {
                conf.triggerS1 = c.getDouble("triggerS1");
            }
            if (c.has("triggerS2")) {
                conf.triggerS2 = c.getDouble("triggerS2");
            }
            if (c.has("triggerR1")) {
                conf.triggerR1 = c.getDouble("triggerR1");
            }
            if (c.has("trans")) {
                conf.trans = c.optJSONObject("trans");
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
