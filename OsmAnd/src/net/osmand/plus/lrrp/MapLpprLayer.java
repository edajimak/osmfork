package net.osmand.plus.lrrp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.osmand.data.LatLon;
import net.osmand.data.PointDescription;
import net.osmand.data.RotatedTileBox;
import net.osmand.plus.R;
import net.osmand.plus.activities.MapActivity;
import net.osmand.plus.views.OsmandMapTileView;
import net.osmand.plus.views.layers.ContextMenuLayer.IContextMenuProvider;
import net.osmand.plus.views.layers.base.OsmandMapLayer;

import java.util.List;

public class MapLpprLayer extends OsmandMapLayer implements IContextMenuProvider {
    private final MapActivity map;
    private final LrrpOsmandPlugin plugin;
    private OsmandMapTileView view;
    private Paint bitmapPaint;
    private final Bitmap[] markerBitmap = new Bitmap[7];

    public MapLpprLayer(@NonNull Context ctx, MapActivity map, LrrpOsmandPlugin plugin) {
        super(ctx);
        this.map = map;
        this.plugin = plugin;
    }

    private void initUI() {
        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true);
        bitmapPaint.setFilterBitmap(true);

        markerBitmap[0] = BitmapFactory.decodeResource(view.getResources(), R.drawable.map_marker_blue);
        markerBitmap[1] = BitmapFactory.decodeResource(view.getResources(), R.drawable.map_marker_green);
        markerBitmap[2] = BitmapFactory.decodeResource(view.getResources(), R.drawable.map_marker_orange);
        markerBitmap[3] = BitmapFactory.decodeResource(view.getResources(), R.drawable.map_marker_red);
        markerBitmap[4] = BitmapFactory.decodeResource(view.getResources(), R.drawable.map_marker_yellow);
        markerBitmap[5] = BitmapFactory.decodeResource(view.getResources(), R.drawable.map_marker_teal);
        markerBitmap[6] = BitmapFactory.decodeResource(view.getResources(), R.drawable.map_marker_purple);
    }

    @Override
    public void initLayer(OsmandMapTileView view) {
        this.view = view;
        initUI();
    }

    public MapActivity getMapActivity() {
        return map;
    }

    @Override
    public void onDraw(Canvas canvas, RotatedTileBox tileBox, DrawSettings settings) {
        Bitmap bmp = markerBitmap[0];
        int marginX = bmp.getWidth() / 6;
        int marginY = bmp.getHeight();

        PointsCollection points = plugin.getPoints();
        synchronized (plugin.getPoints()) {
            for (PointsBucket pts : points.toArray()) {
                if (pts.getLast() == null || pts.getLast().isExpire()) {
                    points.remove(pts.getFrom());
                    continue;
                }
                LrrpPoint p = pts.getLast();
                if (p.isShort() && pts.getUseCount() < 2) {
                    continue;
                }

                int locationX = tileBox.getPixXFromLonNoRot(p.getLongitude());
                int locationY = tileBox.getPixYFromLatNoRot(p.getLatitude());
                canvas.rotate(-tileBox.getRotate(), locationX, locationY);
                int group = p.getGroup();

                if (group == 0 && p.getSpeed() > 12) {
                    group = 1;
                }
                if (group == 0 && p.call) {
                    group = 3;
                }
                canvas.drawBitmap(markerBitmap[group % 7], locationX - marginX, locationY - marginY, bitmapPaint);
                canvas.rotate(tileBox.getRotate(), locationX, locationY);
            }
        }
    }

    @Override
    public void destroyLayer() {
    }

    @Override
    public boolean drawInScreenPixels() {
        return false;
    }

    @Override
    public void collectObjectsFromPoint(PointF point, RotatedTileBox tileBox, List<Object> o, boolean unknownLocation) {
        if (tileBox.getZoom() < 3 || !plugin.LRRP_CONNECTION_DEBUG.get()) {
            return;
        }

        int r = getDefaultRadiusPoi(tileBox);
        PointsCollection points = plugin.getPoints();
        for (PointsBucket pts : points.toArray()) {
            if (pts.getLast() == null) {
                continue;
            }

            LrrpPoint ptsLast = pts.getLast();
            int x = (int) tileBox.getPixXFromLatLon(ptsLast.getLatitude(), ptsLast.getLongitude());
            int y = (int) tileBox.getPixYFromLatLon(ptsLast.getLatitude(), ptsLast.getLongitude());
            if (calculateBelongs((int) point.x, (int) point.y, x, y, r)) {
                o.add(ptsLast);
            }
        }
    }

    @Override
    public LatLon getObjectLocation(Object o) {
        if (o instanceof LrrpPoint) {
            LrrpPoint p = (LrrpPoint) o;
            return new LatLon(p.getLatitude(), p.getLongitude());
        }

        return null;
    }

    @Override
    public PointDescription getObjectName(Object o) {
        if (o instanceof LrrpPoint) {
            LrrpPoint p = (LrrpPoint) o;
            String name = "";
            if (p.getTg() > 0) {
                name = "TG:" + p.getTg() + " ";
            }
            if (p.isShort()) {
                name += "C:" + p.getColorCode() + " T: " + (System.currentTimeMillis()/1000 - p.getTime()) + " sec.";
            } else {
                name += p.getIdentifier() + " T: " + (System.currentTimeMillis()/1000 - p.getTime()) + " sec.";
                name += " S: " + (int)p.getSpeed();
            }

            PointDescription obj = new PointDescription(PointDescription.POINT_TYPE_ALARM, name);
            p.setPointDescription(obj);
            return obj;
        }

        return null;
    }

    @Override
    public boolean disableSingleTap() {
        return false;
    }

    @Override
    public boolean disableLongPressOnMap(PointF point, RotatedTileBox tileBox) {
        return false;
    }

    @Override
    public boolean isObjectClickable(Object o) {
        return false;
    }

    @Override
    public boolean runExclusiveAction(@Nullable Object o, boolean unknownLocation) {
        return false;
    }

    @Override
    public boolean showMenuAction(@Nullable Object o) {
        return false;
    }

    private boolean calculateBelongs(int ex, int ey, int objx, int objy, int radius) {
        return Math.abs(objx - ex) <= radius * 1.5 && (ey - objy) <= radius * 1.5 && (objy - ey) <= 2.5 * radius;
    }
}
