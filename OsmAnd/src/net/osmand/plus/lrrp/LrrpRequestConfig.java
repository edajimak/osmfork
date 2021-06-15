package net.osmand.plus.lrrp;

public class LrrpRequestConfig {
    public int maxSendInterval = 20;
    public String specUrl;
    public String specHost;
    public String authorization;
    public int autoDeactivationTimeInterval = 10800;
    public long active;
    public boolean speakStreet = true;

    public int[] triggerLevels = new int[] {0, 200, 800};

    public int getTriggerLevel(double dist) {
        for (int i : triggerLevels) {
            if (dist < i) {
                return i;
            }
        }

        return -1;
    }
}
