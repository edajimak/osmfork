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
                LrrpPoint expected = pointMapping.get(point.getResolvedFrom());
                double dist = expected != null ? expected.distance(point) : 10000;
                double speed = expected != null ? 3.6 * dist/(Math.abs(expected.time-point.time)+0.1) : 1000;

                for (int x: X_GRID) {
                    for (int y: Y_GRID) {
                        int hash = point.calculateCRC(x, y);
                        PointsBucket current = buckets.get(hash);
                        LrrpPoint currentP = current != null ? current.getLast() : null;
                        if (null == currentP || currentP.distance(point) > dist) {
                            continue;
                        }

                        if (current.getSpeed(point) < 65) {
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

                if (expected != null && defaultBucket == null) {
                    PointsBucket expectedBucket = buckets.get(expected.getFrom());
                    if (null != expectedBucket && speed < 100 && dist < 1500) {
                        defaultBucket = expectedBucket;
                        buckets.remove(expected.getFrom());
                        buckets.put(point.getFrom(), defaultBucket);
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
