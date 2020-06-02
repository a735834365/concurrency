package com.zyf.concurrency.chapter06;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 支持关闭操作的Web服务器
 *
 * create by yifeng
 */
public class LifecycleWebServer {
    private final ExecutorService exec = Executors.newCachedThreadPool();

    public void start() throws IOException {
        ServerSocket socket = new ServerSocket(80);
        while (!exec.isShutdown()) {
            try {
                final Socket conn = socket.accept();
                exec.execute(() -> handleRequest(conn));
            } catch (RejectedExecutionException e) {
                if (!exec.isShutdown()) {
                    log("task shbmission rejected", e);
                }
            }
        }
    }

    public void stop() {
        exec.shutdown();
    }

    private void log(String msg, Exception e) {
        Logger.getAnonymousLogger().log(Level.WARNING, msg, e);
    }


    void handleRequest(Socket conn) {
        Request req = readRequest(conn);
        if (isShutdownRequest(req))
            stop();
        else
            dispatchRequest(req);
    }

    interface Request{}

    private void dispatchRequest(Request req) {

    }

    private boolean isShutdownRequest(Request req) {
        return false;
    }

    private Request readRequest(Socket conn) {
        return null;
    }

}
