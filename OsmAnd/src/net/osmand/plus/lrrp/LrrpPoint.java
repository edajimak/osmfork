package net.osmand.plus.lrrp;

import android.content.Context;

import net.osmand.Location;
import net.osmand.data.LocationPoint;
import net.osmand.data.PointDescription;

import org.json.JSONException;
import org.json.JSONObject;

public class LrrpPoint implements LocationPoint {
    private final double latitude;
    private final double longitude;
    private final int from;
    private final int time;
    private final int group;

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

    public int getTime() {
        return time;
    }

    public int getFrom() {
        return from;
    }

    public boolean isExpire() {
        return time + 900 < System.currentTimeMillis()/1000;
    }

    @Override
    public int getColor() {
        return 0;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public PointDescription getPointDescription(Context ctx) {
        return null;
    }

    public static LrrpPoint create(JSONObject json) throws JSONException {

        return new LrrpPoint(
                json.getJSONArray("p").getDouble(0),
                json.getJSONArray("p").getDouble(1),
                json.getInt("f"),
                json.has("g") ? json.getInt("g") : 0,
                json.getInt("t")
        );
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
