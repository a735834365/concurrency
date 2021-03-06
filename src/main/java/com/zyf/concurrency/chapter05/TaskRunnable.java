package com.zyf.concurrency.chapter05;

import java.util.concurrent.BlockingQueue;

/**
 *
 * 
 * 恢复中断状态，避免掩盖中断
 *
 *
 *
 */

public class TaskRunnable implements Runnable {


        BlockingQueue<Task> queue;

        @Override        
        public void run() {
            try{
                processTask(queue.take());
            }catch(InterruptedException e) {
                // 恢复中断状态
                Thread.currentThread().interrupt(); 
            }
                                
        }

        // 处理任务
        void processTask(Task task) {
            // Handle the task
                            
        }
        interface Task{
        }                     
                                                                           
}