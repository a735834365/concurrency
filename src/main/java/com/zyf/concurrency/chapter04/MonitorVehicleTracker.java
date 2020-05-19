package com.zyf.concurrency.chapter04;

import com.sun.org.apache.regexp.internal.REUtil;
import com.zyf.concurrency.annotations.GuardedBy;
import com.zyf.concurrency.annotations.ThreadSafe;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * 基于监视器模式的车辆追踪
 *
 * create by yifeng
 */
@ThreadSafe
public class MonitorVehicleTracker {
    @GuardedBy
    private final Map<String, MutablePoint> locations;

    public MonitorVehicleTracker(Map<String, MutablePoint> locations) {
        this.locations = deepcopy(locations);
    }

    public synchronized MutablePoint getLocation(String id) {
        MutablePoint loc = locations.get(id);
        return loc == null ? null : new MutablePoint(loc);
    }

    public synchronized void setLocations(String id, int x, int y){
        MutablePoint loc = locations.get(id);
        if (loc == null)
            throw new IllegalArgumentException("No such ID: " + id);
        loc.x = x;
        loc.y = y;
    }

    /**
     * 深度拷贝
     * 引自原文：
     *      deepCopy并不只是用unmodifiableMap来包装Map的，因为这只能
     * @param m
     * @return
     */
    private static Map<String, MutablePoint> deepcopy(Map<String, MutablePoint> m) {
        Map<String, MutablePoint> result = new HashMap<>();
        for (String id : m.keySet()) {
            result.put(id, new MutablePoint(m.get(id)));
        }
        return Collections.unmodifiableMap(result);
    }
}

