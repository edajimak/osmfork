package net.osmand.plus.lrrp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import net.osmand.data.RotatedTileBox;
import net.osmand.plus.R;
import net.osmand.plus.activities.MapActivity;
import net.osmand.plus.views.OsmandMapLayer;
import net.osmand.plus.views.OsmandMapTileView;

import java.util.HashMap;
import java.util.Map;

public class MapLpprLayer extends OsmandMapLayer {
    private final MapActivity map;
    private final LrrpOsmandPlugin plugin;
    private OsmandMapTileView view;
    private Paint bitmapPaint;
    private final Bitmap[] markerBitmap = new Bitmap[7];

    public MapLpprLayer(MapActivity map, LrrpOsmandPlugin plugin) {
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

        synchronized (plugin.getPoints()) {
            for (PointsBucket pts : plugin.getPoints().toArray()) {
                if (pts.getLast() == null || pts.getLast().isExpire()) {
                    continue;
                }
                LrrpPoint p = pts.getLast();

                int locationX = tileBox.getPixXFromLonNoRot(p.getLongitude());
                int locationY = tileBox.getPixYFromLatNoRot(p.getLatitude());
                canvas.rotate(-tileBox.getRotate(), locationX, locationY);
                canvas.drawBitmap(markerBitmap[p.getGroup() % 7], locationX - marginX, locationY - marginY, bitmapPaint);
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
}
