package net.osmand.plus;

public interface EventListener<T> {
    void dispatch(T change);
}
