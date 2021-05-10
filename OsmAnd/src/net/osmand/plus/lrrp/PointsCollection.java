package net.osmand.plus.lrrp;

import java.util.HashMap;

public class PointsCollection {
    private final HashMap<Integer, PointsBucket> buckets = new HashMap<Integer, PointsBucket>();
    private final HashMap<Integer, LrrpPoint> pointMapping = new HashMap<>();

    private final static int[] X_GRID = new int[]{0, 1, -1, 2, -2};
    private final static int[] Y_GRID = new int[]{0, 1, -1, 2, -2};

    public void push(LrrpPoint point) {
        PointsBucket defaultBucket = null;
        if (!buckets.containsKey(point.getFrom())) {
            if (point.isShort()) {
                for (int x: X_GRID) {
                    for (int y: Y_GRID) {
                        int hash = point.calculateCRC(x, y);
                        PointsBucket current = buckets.get(hash);
                        if (null != current && current.getSpeed(point) < 65) {
                            buckets.remove(hash);
                            buckets.put(point.getFrom(), current);
                            point.setVirtualFrom(current.getFrom());
                            defaultBucket = current;
                            break;
                        }
                    }

                    if (null != defaultBucket) {
                        break;
                    }
                }
            }

            if (null == defaultBucket) {
                buckets.put(point.getFrom(), new PointsBucket());
            }
        }

        PointsBucket bucket = defaultBucket != null ? defaultBucket : buckets.get(point.getFrom());
        if (null != bucket) {
            bucket.push(point);
        }

        if (point.getResolvedFrom() > 0) {
            LrrpPoint prev = pointMapping.get(point.getResolvedFrom());
            if (null != prev) {
                prev.setHeadPoint(false);
            }
            pointMapping.put(point.getResolvedFrom(), point);
        }
    }

    public void remove(int from) {
        buckets.remove(from);
    }

    public void clear() {
        buckets.clear();
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
