package net.osmand.plus.lrrp;

import java.util.ArrayList;

public class PointsBucket {
    private final ArrayList<LrrpPoint> points = new ArrayList<>();
    private int level = -1;
    private long triggerLevelTime = 0;

    public void push(LrrpPoint point) {
        if (points.size() > 3) {
            points.remove(0);
        }

        points.add(point);
    }

    public LrrpPoint getLast() {
        return points.get(points.size() - 1);
    }

    public ArrayList<LrrpPoint> getPoints() {
        return points;
    }

    public boolean isTriggerLevel(int level) {
        if (this.level == -1 || triggerLevelTime + 600 < System.currentTimeMillis()/1000) {
            this.level = -1;
        }

        return level != -1 && (this.level > level || this.level == -1);
    }

    public void triggerLevel(int level) {
        this.level = level;
        triggerLevelTime = System.currentTimeMillis()/1000;
    }
}
