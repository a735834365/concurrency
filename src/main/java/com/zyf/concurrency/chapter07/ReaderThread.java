package com.zyf.concurrency.chapter07;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * 通过改写interrupt方法，将非标准的取消操作封装在Thread中
 * 引自原文：
 *      ReaderThread给出了如何封装非标准的取消操作
 *      无论ReaderThread是在read方法中阻塞还是在某个可中断的阻塞方法中阻塞，都可以被中断
 *   并停止执行当前的工作
 *
 * create by yifeng
 */
public class ReaderThread extends Thread {
    private static final int BUFSZ = 512;
    private final Socket socket;
    private final InputStream in;

    public ReaderThread(Socket socket) throws IOException {
        this.socket = socket;
        this.in = socket.getInputStream();
    }

    @Override
    public void run() {
        try {
            byte[] buf = new byte[BUFSZ];
            while (true) {
                int count = in.read(buf);
                if (count < 0)
                    break;
                else if (count > 0)
                    processBuffer(buf, count);
            }
        } catch (IOException e) {
            // 允许线程退出
        }
    }

    private void processBuffer(byte[] buf, int count) {
    }

    /**
     * 改写了interrupt，使其既能处理标准的中断，也能关闭底层的套接字
     */
    @Override
    public void interrupt() {
        try {
            socket.close();
        } catch (IOException ignored) {
        } finally {
            super.interrupt();
        }
    }
}
