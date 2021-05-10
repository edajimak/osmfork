package net.osmand.plus;

import net.osmand.StateChangedListener;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class EventDispatcher<T> {
    private List<WeakReference<EventListener<T>>> l = null;

    public synchronized void addListener(String eventName, EventListener<T> listener) {
        if (l == null) {
            l = new LinkedList<WeakReference<EventListener<T>>>();
        }
        if (!l.contains(new WeakReference<EventListener<T>>(listener))) {
            l.add(new WeakReference<EventListener<T>>(listener));
        }
    }

    public synchronized void dispatch(String eventName, T value) {
        if (l != null) {
            Iterator<WeakReference<EventListener<T>>> it = l.iterator();
            while (it.hasNext()) {
                EventListener<T> t = it.next().get();
                if (t == null) {
                    it.remove();
                } else {
                    t.dispatch(value);
                }
            }
        }
    }

    public synchronized void removeListener(EventListener<T> listener) {
        if (l != null) {
            Iterator<WeakReference<EventListener<T>>> it = l.iterator();
            while (it.hasNext()) {
                EventListener<T> t = it.next().get();
                if (t == listener) {
                    it.remove();
                }
            }
        }
    }
}
