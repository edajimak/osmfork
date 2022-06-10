package net.osmand.plus.lrrp;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class LrrpRequestConfig {
    public int maxSendInterval = 14;
    public String json;
    public String specUrl;
    public String specHost;
    public String specWs;
    public String authorization;
    public int autoDeactivationTimeInterval = 8 * 3600;

    public int audioStreamType = 3; //AudioManager.STREAM_MUSIC
    public int audioStreamVolume = 0;

    public long active;
    public boolean speakStreet = true;
    public boolean onlyNearest = false;
    public int nearestRadius = 0;
    public HashMap<Integer, String> aliasMap = new HashMap<>();

    public double triggerS1 = 1.0; // 40
    public double triggerS2 = 1.0; // 70
    public double triggerR1 = 1.0; // street

    public int[] triggerLevels = new int[] {0, 200, 800};
    public int[] subscriberTG = new int[]{};

    public JSONObject trans;

    public void processAliases(JSONObject alias) {
        try {
            Iterator<String> names = alias.keys();
            while(names.hasNext()) {
                try {
                    String name = names.next();
                    String trans = alias.optString(name);
                    if (trans.isEmpty()) {
                        continue;
                    }
                    String[] tokens = name.split(",");
                    for (String token: tokens) {
                        if (token.contains("-")) {
                            String[] range = token.split("-");
                            int left = Integer.parseInt(range[0]);
                            int right = Integer.parseInt(range[1]);
                            for (int i = left; i <= right; i++) {
                                aliasMap.put(i, trans);
                            }
                        } else {
                            int left = Integer.parseInt(token);
                            aliasMap.put(left, trans);
                        }
                    }
                } catch (Exception ignored) { }
            }

        } catch (Exception ignored) {}
    }

    public int getTriggerLevel(double dist) {
        for (int i : triggerLevels) {
            if (dist < i) {
                return i;
            }
        }

        return -1;
    }
}
