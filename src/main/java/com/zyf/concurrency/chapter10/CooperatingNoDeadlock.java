package com.zyf.concurrency.chapter10;

import com.zyf.concurrency.annotations.GuardedBy;
import com.zyf.concurrency.annotations.ThreadSafe;
import com.zyf.concurrency.chapter04.Point;

import java.util.HashSet;
import java.util.Set;

/**
 * 通过公开调用来避免在相互协作的对象之前产生死锁
 * 引自原文：
 *      将CooperatingDeadlock中Taxi和Dispatcher修改为开放调用，从而消除发生死锁的风险。
 *      收缩同步代码块的保护范围还可以提高可伸缩性。
 *
 *
 * create by yifeng
 */
public class CooperatingNoDeadlock {
    @ThreadSafe
    class Taxi {
        @GuardedBy("this") private Point location, destination;
        private final Dispatcher dispatcher;

        public Taxi(Dispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }

        public synchronized Point getLocation() {
            return location;
        }
        // 使用公开调用，收缩同步代码块
        public void setLocation(Point location) {
            boolean reachedDestination;
            synchronized (this) {
                this.location = location;
                reachedDestination = location.equals(destination);
            }
            if (reachedDestination)
                dispatcher.notifyAvailable(this);
        }

        public Point getDestination() {
            return destination;
        }

        public void setDestination(Point destination) {
            this.destination = destination;
        }
    }

    @ThreadSafe
    class Dispatcher {
        @GuardedBy("this") private final Set<Taxi> taxis;
        @GuardedBy("this") private final Set<Taxi> availableTaxis;

        public Dispatcher() {
            this.taxis = new HashSet<>();
            availableTaxis = new HashSet<>();
        }

        public synchronized void notifyAvailable(Taxi taxi) {
            availableTaxis.add(taxi);
        }

        // 使用公开调用，收缩同步代码块
        public Image getImage() {
            Set<Taxi> copy;
            synchronized (this) {
                copy = new HashSet<>(taxis);
            }

            Image image = new Image();
            for (Taxi t : copy) {
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
