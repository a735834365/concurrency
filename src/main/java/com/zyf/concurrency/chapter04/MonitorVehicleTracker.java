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
 * 个人理解：通过将可变的Point封装在deepCopy()中，使其线程安全
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
     *      deepCopy并不只是用unmodifiableMap来包装Map的，因为这只能防止容器对象被修改，
     *      而不能防止调用者修改保存在容器中的可变对象。如果只是通过拷贝构造函数来填充deepCopy
     *      中的HashMap中，那么同样是不正确的，因为这样做只赋值了指向point对象的引用，而不是
     *      Point本身。
     *      个人理解：不仅仅要修改要将Map设置成不可变的，还要修改point对象的引用，防止容器
     *      中的可变对象修改。
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

