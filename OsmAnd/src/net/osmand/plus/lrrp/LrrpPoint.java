package net.osmand.plus.lrrp;

import android.content.Context;

import net.osmand.Location;
import net.osmand.data.LocationPoint;
import net.osmand.data.PointDescription;

import org.json.JSONException;
import org.json.JSONObject;

public class LrrpPoint implements LocationPoint {
    public final double latitude;
    public final double longitude;
    public int from;
    public int resolvedFrom;
    public int tg;
    public int time;
    public int group;
    public int virtualFrom = 0;
    public double mSpeed;
    public int mCC = 0;
    public boolean isShort = false;
    public boolean isHeadPoint = true;
    public PointDescription mPointDescription;
    public double dist = 0;

    public LrrpPoint(double latitude, double longitude, int from, int group, int time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.from = from;
        this.time = time;
        this.group = group;
    }

    public LrrpPoint(double latitude, double longitude, int from, int group) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.from = from;
        this.group = group;

        this.time = (int) System.currentTimeMillis()/1000;
    }

    public LrrpPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LrrpPoint(long lat, long lon) {
        this.latitude = 360.0 * (lat/4294967296.0) - 180.0;
        this.longitude = 360.0 * (lon/4294967296.0);
    }

    public boolean isHeadPoint() {
        return isHeadPoint;
    }

    public void setHeadPoint(boolean headPoint) {
        isHeadPoint = headPoint;
    }

    public int getIdentifier() {
        return virtualFrom != 0 ? virtualFrom : from;
    }

    public void setVirtualFrom(int virtualFrom) {
        this.virtualFrom = virtualFrom;
    }

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    public int getGroup() {
        return group;
    }

    public void setSpeed(double speed) {
        mSpeed = speed;
    }

    public double getSpeed() {
        return mSpeed;
    }

    public int getTime() {
        return time;
    }

    public int getFrom() {
        return from;
    }

    public boolean isExpire() {
        return (isShort ? (isHeadPoint ? 240 : 140) : 720) < getAge();
    }

    public int getAge() {
        return (int)(System.currentTimeMillis()/1000 - time);
    }

    public boolean isShort() {
        return isShort;
    }

    public int getColorCode() {
        return mCC;
    }

    @Override
    public int getColor() {
        return 0;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    public int getResolvedFrom() {
        return resolvedFrom;
    }

    public int getTg() {
        return tg;
    }

    @Override
    public PointDescription getPointDescription(Context ctx) {
        return mPointDescription;
    }

    public void setPointDescription(PointDescription p) {
        mPointDescription = p;
    }

    public static LrrpPoint create(JSONObject json) throws JSONException {

        LrrpPoint point = new LrrpPoint(
            json.getJSONArray("p").getDouble(0),
            json.getJSONArray("p").getDouble(1),
            json.optInt("f"),
            json.has("g") ? json.getInt("g") : 0,
            json.getInt("t")
        );

        point.resolvedFrom = point.from;
        point.isHeadPoint = point.resolvedFrom > 0;

        if (!json.isNull("c")) {
            point.mCC = json.getInt("c");
        }
        if (json.has("tg")) {
            point.tg = json.optInt("tg");
        }

        if (json.isNull("f") || !json.isNull("c")) {
            point.from = point.calculateCRC(0, 0); // Virtual from in grid xy 275x250m
            point.isShort = true;
        }

        return point;
    }

    public int calculateCRC(int latOff, int lonOff)
    {
        long val = (Math.round(400*(latitude - (int)latitude) + latOff)*25) % 10000;
        val += Math.round(250*(longitude - (int)longitude) + lonOff);
        val += 10000 * mCC;
        val += 1000000;

        return (int)val;
    }

    public double bearingDeg(double lat, double lon) {
        double x = Math.cos(lat/57.295791) * (longitude - lon);
        double y = (latitude - lat);

        double th = (Math.atan2(x, y)*180/Math.PI + 360);
        if (th > 360) {
            th -= 360;
        }

        return th;
    }

    public double distance(LocationPoint a) {
        return LrrpPoint.distance(this, a);
    }

    public double distance(Location a) {
        return this.distance(a.getLongitude(), a.getLatitude());
    }

    public double distance(double x, double y) {
        return LrrpPoint.distance(x, y, this.getLongitude(), this.getLatitude());
    }

    public static double distance(LocationPoint a, LocationPoint b) {
        return LrrpPoint.distance(a.getLongitude(), a.getLatitude(), b.getLongitude(), b.getLatitude());
    }

    public static double distance(double x1, double y1, double x2, double y2) {
        double ly = 111138.5;
        double lx = Math.cos(y1/57.295791) * ly;

        return Math.sqrt(Math.pow(ly * (y1-y2), 2) +
                Math.pow(lx * (x1-x2), 2));
    }

    public static void main(String[] args) {
        LrrpPoint p = new LrrpPoint(45.177474, 141.233925, 0, 0);
        double d1 = p.bearingDeg(45.165311, 141.232973);
        double d2 = p.bearingDeg(45.168144, 141.216826);
        double d3 = p.bearingDeg(45.179751, 141.260192);
        double d4 = p.bearingDeg(45.191442, 141.230271);
    }
}
