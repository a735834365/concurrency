package com.zyf.concurrency.chapter04;

import com.zyf.concurrency.annotations.ThreadSafe;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 引自原文：
 *      因为Point类是不可变的，线程安全的，所以在返回location时不需要做深度复制。
 *
 * create by yifeng
 */
@ThreadSafe
public class DelegatingVehicleTracker {

    // 该变量是一个动态的(车辆位置的修改会及时反映给该属性)
    private final ConcurrentHashMap<String, Point> locations;
    // 该属性为车辆位置的快照（可能导致不一致的车辆位置）
    private final Map<String, Point> unmodifiableMap;

    public DelegatingVehicleTracker(Map<String ,Point> points) {
        this.locations = new ConcurrentHashMap(points);
        this.unmodifiableMap = Collections.unmodifiableMap(points);
    }

    public Map<String, Point> getLocations() {
        return unmodifiableMap;
    }

    // 返回实时拷贝，因为使用的是ConcurrentHashMap，所以不需要担心线程安全的问题
    public Point getLocation(String id) {
        return locations.get(id);
    }

    public void setLocation(String id, int x, int y) {
        if (locations.replace(id, new Point(x, y)) == null) {
            throw new IllegalArgumentException(
                    "invalid vehicle name: " + id);
        }
    }

    // 返回locations的静态拷贝而非实时拷贝
    public Map<String, Point> getLocationAsStatic() {
        return Collections.unmodifiableMap(
                new HashMap<>(locations));
    }
}
