package com.zyf.concurrency.chapter06;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 串行地执行任务
 *
 * create by yifeng
 */
public class SingleThreadWebServer {
    public static void main(String[] args) throws IOException {
        // 通过80端口接收到HTTP请求
        ServerSocket socket = new ServerSocket(80);
        while (true) {
            Socket connection = socket.accept();
            handleRequest(connection);
        }
    }

    private static void handleRequest(Socket connection) {
        // logic here
    }
}
