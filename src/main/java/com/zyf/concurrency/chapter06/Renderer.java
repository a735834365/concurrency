package com.zyf.concurrency.chapter06;

import com.zyf.concurrency.chapter05.LaunderThrowable;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * 使用CompletionService，使页面元素在下载完成后立即显示出来
 *
 * 引自原文：
 *      多个ExecutorCompletionService（ECS）可以共享一个Executor，ECS相当于一组计算的句柄，这类似于Future作为单个计算的句柄。这样就可以记录提交的任务数量并计算出已经完成结果的数量。
 *      为每一幅图像的下载创建一个独立任务并在线程池中执行。将串行的下载过程转换为并行的过程：减少下载所有图像的总时间
 *      通过从CompletionService中获取结果以及使每张图片在下载完成后立即显示出啦，能使用户获得一个更加动态和更高响应的用户界面
 *
 * create by yifeng
 */
public abstract class Renderer {
    private final ExecutorService executor;

    public Renderer(ExecutorService executor) {
        this.executor = executor;
    }

    void renderPage(CharSequence source) {
        List<ImageInfo> imageInfos = scanForImageInfo(source);
        ExecutorCompletionService<ImageData> completionService = new ExecutorCompletionService<>(executor);
        // 遍历所有任务，并使所有任务同时下载
        for (final ImageInfo imageInfo : imageInfos) {
            completionService.submit(() -> imageInfo.downloadImage());
        }
        renderText(source);
        try {
            // 每加载一幅图像，就马上渲染
            // 提交的任务数量为imageInfos.size，已经完成的数量为i
            for (int i = 0; i < imageInfos.size(); i++) {
                // take方法，如果没有完成的任务，则阻塞，直到存在完成的任务
                Future<ImageData> f = completionService.take();
                ImageData imageData = f.get();
                renderImage(imageData);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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
