package com.zyf.concurrency.chapter11;

import com.zyf.concurrency.annotations.GuardedBy;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 减少锁的持有时间
 * 引自原文：
 *      缩小方法userLocationMatches中锁的作用范围，能极大减少在持有锁时需要执行的指令数量。根据Amdahl定律，这消除了
 *      限制可伸缩性的一个因素，因为串行代码的总量减少了。
 *
 * create by yifeng
 */
public class BetterAttributeStore {
    // 可以通过将线程安全性委托给其他的类进一步提升它的性能，参见4.3节
    @GuardedBy("this")
    private final Map<String, String> attributes = new HashMap<>();

    public boolean userLocationMatches(String name, String regexp) {
        String key = "user." + name + ".location";
        String location;
        // 减少了锁被持有的时间
        synchronized (this) {
            location = attributes.get(key);
        }
        if (location == null) {
            return false;
        } else
            return Pattern.matches(regexp, location);
    }
}
