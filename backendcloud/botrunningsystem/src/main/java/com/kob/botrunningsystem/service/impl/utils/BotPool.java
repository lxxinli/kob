package com.kob.botrunningsystem.service.impl.utils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BotPool extends Thread {
    private final ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private Queue<Bot> bots = new LinkedList<>();

    public void addBot(Integer userId, String botCode, String input) {
        lock.lock();

        try {
            bots.add(new Bot(userId, botCode, input));
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private void consume(Bot bot) {
        // 后续支持其他语言可以选择： 通过在Java中执行命令行操作来启动一个docker，然后在docker中进行代码运行并返回结果

        Consumer consumer = new Consumer();
        consumer.startTimeout(2000, bot);  // 最多执行2s


    }

    @Override
    public void run() {
        while (true) {
            lock.lock();
                if (bots.isEmpty()) {
                    try {
                        condition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        lock.unlock();
                        break;
                    }
                } else {
                    Bot bot = bots.remove();
                    lock.unlock();
                    consume(bot);   // 比较耗时，不适合持有锁
                }

        }
    }
}
