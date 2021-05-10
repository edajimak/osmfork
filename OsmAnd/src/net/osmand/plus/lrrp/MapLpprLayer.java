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
    private Bitmap markerBitmapBlue;

    public MapLpprLayer(MapActivity map, LrrpOsmandPlugin plugin) {
        this.map = map;
        this.plugin = plugin;
    }

    private void initUI() {
        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true);
        bitmapPaint.setFilterBitmap(true);
        markerBitmapBlue = BitmapFactory.decodeResource(view.getResources(), R.drawable.map_marker_blue);
    }

    @Override
    public void initLayer(OsmandMapTileView view) {
        this.view = view;
        initUI();
    }

    @Override
    public void onDraw(Canvas canvas, RotatedTileBox tileBox, DrawSettings settings) {
        Bitmap bmp = markerBitmapBlue;
        int marginX = bmp.getWidth() / 6;
        int marginY = bmp.getHeight();

        HashMap<Integer, LrrpPoint> points = plugin.getLrrpRequestHelper().getPoints();
        for(Map.Entry<Integer, LrrpPoint> point : points.entrySet()) {
            if (!point.getValue().isExpire()) {
                int locationX = tileBox.getPixXFromLonNoRot(point.getValue().getLongitude());
                int locationY = tileBox.getPixYFromLatNoRot(point.getValue().getLatitude());
                canvas.rotate(-tileBox.getRotate(), locationX, locationY);
                canvas.drawBitmap(bmp, locationX - marginX, locationY - marginY, bitmapPaint);
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
