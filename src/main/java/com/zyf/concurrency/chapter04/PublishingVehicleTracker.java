package com.zyf.concurrency.chapter04;

import com.zyf.concurrency.annotations.ThreadSafe;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 安全发布底层状态的车辆追踪器
 *
 * 引自原文：
 *      PublishingVehicleTracker是线程安全的，但如果它在车辆位置的有效性
 *      上施加了任何约束，那么就不再是线程安全的。
 *
 * create by yifeng
 */
@ThreadSafe
public class PublishingVehicleTracker {
    private final Map<String, SafePoint> locations;
    private final Map<String, SafePoint> unmodifiableMap;

    public PublishingVehicleTracker(
            Map<String, SafePoint> locations) {
        this.locations
                = new ConcurrentHashMap<>(locations);
        this.unmodifiableMap
                = Collections.unmodifiableMap(this.locations);
    }

    public Map<String, SafePoint> getLocations() {
        return unmodifiableMap;
    }

    // 可以通过返回的SafePoint修改车辆的位置
    public SafePoint getLocation(String id) {
        return locations.get(id);
    }

    public void setLocation(String id, int x, int y) {
        if (!locations.containsKey(id)) {
            throw new IllegalArgumentException(
                    "invalid vehicle name: " + id);
        }
        locations.get(id).set(x, y);
    }



}
