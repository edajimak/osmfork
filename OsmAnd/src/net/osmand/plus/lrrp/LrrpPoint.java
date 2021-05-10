package net.osmand.plus.lrrp;

import android.content.Context;

import net.osmand.data.LocationPoint;
import net.osmand.data.PointDescription;

import org.json.JSONException;
import org.json.JSONObject;

public class LrrpPoint implements LocationPoint {
    private final double latitude;
    private final double longitude;
    private final int from;
    private final int time;

    public LrrpPoint(double latitude, double longitude, int from, int time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.from = from;
        this.time = time;
    }

    public LrrpPoint(double latitude, double longitude, int from) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.from = from;

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

    public double distance(LocationPoint a) {
        return LrrpPoint.distance(this, a);
    }

    public static LrrpPoint create(JSONObject json) throws JSONException {

        return new LrrpPoint(
                json.getJSONArray("p").getDouble(0),
                json.getJSONArray("p").getDouble(1),
                json.getInt("f"),
                json.getInt("t")
        );
    }

    public static double distance(LocationPoint a, LocationPoint b) {
        double ly = 111138.5;
        double lx = Math.cos(a.getLatitude()/57.295791) * ly;

        return Math.sqrt(Math.pow(ly * (a.getLatitude()-b.getLatitude()), 2) +
                Math.pow(lx * (a.getLongitude()-b.getLongitude()), 2));
    }
}
