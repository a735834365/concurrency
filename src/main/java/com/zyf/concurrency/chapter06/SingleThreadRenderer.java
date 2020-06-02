package com.zyf.concurrency.chapter06;

import com.sun.scenario.effect.ImageData;

import java.util.ArrayList;
import java.util.List;

/**
 * 串行地渲染页面元素
 * 引自原文：
 *      图片下载过程的大部分时间都在等待I/O操作执行完成，这种串行执行方法没有充分利用CPU，通过将问题分解为多个独立的任务并发执行，能够获得更高的CPU利用率和响应灵敏度。
 *
 * create by yifeng
 */
public abstract class SingleThreadRenderer {
    void randerPage(CharSequence source) {
        // 先绘制文本元素，为图形预留矩形的占位空间
        renderText(source);
        List<ImageData> imageData = new ArrayList<>();
        for (ImageInfo imageInfo : scanForImageInfo(source)) {
            // 串行下载图形
            imageData.add(imageInfo.downloadImage());
        }
        // 渲染图形
        for (ImageData data : imageData) {
            renderImage(data);
        }
    }


    interface ImageData {
    }
    interface ImageInfo{
        ImageData downloadImage();
    }
    abstract void renderText(CharSequence s);

    abstract void renderImage(ImageData i);

    abstract List<ImageInfo> scanForImageInfo(CharSequence s);
}
