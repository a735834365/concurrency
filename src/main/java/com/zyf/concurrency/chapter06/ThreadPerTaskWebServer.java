package com.zyf.concurrency.chapter06;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 在Web服务器中为每个请求启动一个新的线程（不要这么做）
 *
 * create by yifeng
 */
public class ThreadPerTaskWebServer {
    public static void main(String[] args) throws IOException {
        ServerSocket socket = new ServerSocket(80);
        while (true) {
            final Socket connection = socket.accept();
            Runnable task = () -> handleRquest(connection);
            new Thread(task).run();
        }
    }

    private static void handleRquest(Socket connection) {
        // logic here
    }
}
