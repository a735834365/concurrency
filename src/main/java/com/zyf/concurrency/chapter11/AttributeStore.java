package com.zyf.concurrency.chapter11;

import com.zyf.concurrency.annotations.GuardedBy;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 将一个锁不必要地持有过长时间
 * 引自原文
 *      业务：在Map对象中查找用户的位置，并使用正则表达式进行匹配以判断结果值是否匹配所提供的模式。
 *
 * create by yifeng
 */
public class AttributeStore {
    @GuardedBy("this")
    private final Map<String, String> attributes = new HashMap<>();

    public synchronized boolean userLocationMatches(String name, String regexp) {
        String key = "users." + name + ".location";
        // 只有get操作才需要锁，不必要将所有方法都锁上
        String location = attributes.get(key);
        if (location == null) {
            return false;
        } else {
            return Pattern.matches(regexp, location);
        }

    }
}
