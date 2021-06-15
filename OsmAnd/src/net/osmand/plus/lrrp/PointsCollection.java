package net.osmand.plus.lrrp;

import java.util.HashMap;

public class PointsCollection {
    private final HashMap<Integer, PointsBucket> buckets = new HashMap<Integer, PointsBucket>();

    public void push(LrrpPoint point) {
        if (!buckets.containsKey(point.getFrom())) {
            buckets.put(point.getFrom(), new PointsBucket());
        }

        PointsBucket bucket = buckets.get(point.getFrom());
        if (null != bucket) {
            bucket.push(point);
        }
    }

    public PointsBucket[] toArray() {
        int i = 0;
        PointsBucket[] pointsBuckets = new PointsBucket[buckets.size()];
        for (PointsBucket value : buckets.values()) {
            pointsBuckets[i++] = value;
        }

        return pointsBuckets;
    }
}
