package net.osmand.plus.lrrp;

import android.os.AsyncTask;
import android.os.SystemClock;

import net.osmand.Location;
import net.osmand.PlatformUtil;
import net.osmand.plus.OsmandApplication;
import org.apache.commons.logging.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class LrrpRequestHelper {
    private final static Log log = PlatformUtil.getLog(LrrpRequestHelper.class);

    private long lastTimeUpdated;
    private final OsmandApplication app;
    private final LrrpOsmandPlugin plugin;
    private boolean started = false;
    private Location lastLocation;
    private String lastHash;

    private final HashMap<Integer, LrrpPoint> points = new HashMap<Integer, LrrpPoint>();

    public LrrpRequestHelper(OsmandApplication app, LrrpOsmandPlugin plugin) {
        this.app = app;
        this.plugin = plugin;
    }

    public boolean isMonitoringEnabled() {
        return plugin.isActive();
    }

    public HashMap<Integer, LrrpPoint> getPoints() {
        return points;
    }

    public void triggerLrrpLocation(Location location) {
        this.lastLocation = location;
        if (isMonitoringEnabled()) {
            if (!started) {
                new LiveFetcher().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                started = true;
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
                if (System.currentTimeMillis()/1000 - lastSendInterval > conf.maxSendInterval && conf.specUrl != null) {
                    lastSendInterval = System.currentTimeMillis()/1000;
                    fetchData(conf);
                }

                SystemClock.sleep(2000);
            }
            started = false;
            return null;
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

            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            urlConnection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            urlConnection.setDoOutput(true);
            urlConnection.getOutputStream().write(postDataBytes);

            log.info("Monitor " + postData);
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
                for (int i = 0 ; i < coords.length() ; i++){
                    try {
                        LrrpPoint point = LrrpPoint.create(coords.getJSONObject(i));
                        points.put(point.getFrom(), point);
                    } catch (Exception e) { }
                }
            }

            urlConnection.disconnect();
        } catch (Exception e) {
            log.warn("Failed connect to: " + e.getMessage(), e);
        }
    }

    public LrrpRequestConfig getConfig() {
        LrrpRequestConfig conf = new LrrpRequestConfig();
        try {
            String json = plugin.LRRP_CONNECTION_CONFIG.get();
            JSONObject config = new JSONObject(json);

            conf.specUrl = config.has("url") ? config.getString("url") : null;
            conf.specHost = config.has("host") ? config.getString("host") : null;
            conf.maxSendInterval = Math.max(config.has("interval") ? config.getInt("interval") : 20, 5);
        } catch (Exception e) { }

        return conf;
    }
}
