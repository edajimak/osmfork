package net.osmand.plus.lrrp;

import java.util.ArrayList;

public class PointsBucket {
    private final ArrayList<LrrpPoint> points = new ArrayList<>();
    private int level = -1;
    private long triggerLevelTime = 0;
    private String streetName;
    private long streetUpdateTime = 0;
    private LrrpPoint streetUpdatePoint;
    private double lastDist = 0;
    private int mFrom;
    private int useCount = 0;

    public double getLastDist() {
        return lastDist;
    }

    public void setLastDist(double lastDist) {
        this.lastDist = lastDist;
    }

    public void push(LrrpPoint point) {
        useCount++;
        mFrom = point.getFrom();
        if (points.size() > 4) {
            points.remove(0);
        }

        point.setSpeed(getSpeed(point));

        points.add(point);
    }

    public int getUseCount() {
        return useCount;
    }

    public double getSpeed(LrrpPoint point) {
        LrrpPoint prev = getLast();
        if (prev != null) {
            int dt = Math.abs(point.getTime() - prev.getTime());
            if (dt == 0) {
                dt = 1;
            }

            return 3.6*point.distance(prev) / dt;
        }

        return 0;
    }

    public int getFrom() {
        return mFrom;
    }

    public LrrpPoint getLast() {
        return points.size() > 0 ? points.get(points.size() - 1) : null;
    }

    public String getStreetName() {
        boolean needUpdate = false;
        if (streetUpdatePoint != null && getLast() != null && streetUpdatePoint.distance(getLast()) > 600) {
            needUpdate = true;
        }
        if (System.currentTimeMillis()/1000 - streetUpdateTime > 1800) {
            needUpdate = true;
        }

        return needUpdate ? streetName : null;
    }

    public void setStreetName(String streetName) {
        streetUpdateTime = System.currentTimeMillis()/1000;
        streetUpdatePoint = getLast();
        this.streetName = streetName;
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
