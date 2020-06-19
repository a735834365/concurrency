package com.zyf.concurrency.chapter06;

import com.zyf.concurrency.chapter05.LaunderThrowable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 使用Future等待图像下载
 * 引自原文：
 *      将渲染过程分解为两个任务，一个是渲染所有文本（CPU密集型），另一个是下载所有图像（I/O密集型）。
 *      当然，还能做得更好：每下载一幅图像时就立刻显示出来
 *
 * create by yifeng
 */
public abstract class FutureRenderer {
    private final ExecutorService executor =
            Executors.newCachedThreadPool();

    void renderPage(CharSequence source) {
        final List<ImageInfo> imageInfos = scanForImageInfo(source);
        Callable<List<ImageData>> task =
                () -> {
                    List<ImageData> result
                            = new ArrayList<>();
                    for (ImageInfo imageInfo : imageInfos) {
                        result.add(imageInfo.downloadImage());
                    }
                    return result;
                };
        // 下载图像的同时渲染文本
        Future<List<ImageData>> future = executor.submit(task);
        renderText(source);

        try {
            List<ImageData> imageData = future.get();
            for (ImageData data : imageData) {
                renderImage(data);
            }
            // 关于Future.get的异常处理，可参见5.5.2和5.4节
        } catch (InterruptedException e) {
            // 重新设置线程的中断状态
            Thread.currentThread().interrupt();
            // 由于不需要结果，因此取消任务
            future.cancel(true);
        } catch (ExecutionException e) {
            throw LaunderThrowable.launderThrowable(e.getCause());
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
