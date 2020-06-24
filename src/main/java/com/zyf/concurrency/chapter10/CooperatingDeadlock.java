package com.zyf.concurrency.chapter10;

import com.zyf.concurrency.annotations.GuardedBy;
import com.zyf.concurrency.expmple.Point;

import java.util.HashSet;
import java.util.Set;

/**
 * 在相互协作对象之间的锁顺序死锁（不要这么做）
 * 引自原文：
 *      某些获取多个锁的操作不像在LeftRightDeadlock或transferMoney中的那么明显，这两个锁并不一定
 *  必须在同一个方法中被获取。
 *
 * create by yifeng
 */
public class CooperatingDeadlock {
    // deadlock-prone！
    class Taxi {
        @GuardedBy private Point location, destination;
        private final Dispatcher dispatcher;

        public Taxi(Dispatcher dispatcher) {
            this.dispatcher = dispatcher;
            // 目的地就是当前所在点，则通知调度
            if (location.equals(destination))
                dispatcher.notifyAvailable(this);
        }

        public synchronized Point getLocation() {
            return location;
        }

        // 同样的死锁触发条件 LeftRightDeadlock
        // 1、拿到taxi的锁
        public synchronized void setLocation(Point location) {
            this.location = location;
            // 目的地就是当前所在点，则通知调度
            if (location.equals(destination))
                // 2、尝试拿到dispatcher的锁
                // 这时，如果其他线程正在等待taxi的锁，并占有了dispatcher的锁，那可就糟了
                dispatcher.notifyAvailable(this);
        }

        public Point getDestination() {
            return destination;
        }

        public void setDestination(Point destination) {
            this.destination = destination;
        }
    }

    class Dispatcher {
        @GuardedBy("this") private final Set<Taxi> taxis;
        @GuardedBy("this") private final Set<Taxi> availableTaxis;

        public Dispatcher() {
            this.taxis = new HashSet<>();
            this.availableTaxis = new HashSet<>();
        }

        public synchronized void notifyAvailable(Taxi taxi) {
            availableTaxis.add(taxi);
        }

        // 1、拿到dispatcher锁。
        public synchronized Image getImage() {
            Image image = new Image();
            for (Taxi t : taxis) {
                // 2、每次循环将会获取一个taxi的锁，尝试获取t的锁
                // 如果此时有其他线程已经持有t的锁，而正在等待dispatcher的锁，那可就糟了
                image.drawMarker(t.getLocation());
            }
            return image;
        }
    }

    class Image {
        public void drawMarker(Point p) {
        }
    }
}
