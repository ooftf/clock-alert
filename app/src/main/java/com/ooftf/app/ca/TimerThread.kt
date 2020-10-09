package com.ooftf.app.ca

import android.util.Log
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
class TimerThread : ThreadPoolExecutor(
    1, 1,
    0L, java.util.concurrent.TimeUnit.MILLISECONDS,
    java.util.concurrent.LinkedBlockingQueue<java.lang.Runnable?>(), DefaultThreadFactory()
) {

}

class DefaultThreadFactory : ThreadFactory {
    override fun newThread(runnable: Runnable): Thread {
        val thread = Thread(runnable, "timer")
        //设为非后台线程
        if (thread.isDaemon) {
            thread.isDaemon = false
        }
        //优先级为normal
        if (thread.priority != Thread.MAX_PRIORITY) {
            thread.priority = Thread.MAX_PRIORITY
        }

        // 捕获多线程处理中的异常
        thread.uncaughtExceptionHandler =
            Thread.UncaughtExceptionHandler { thread1: Thread, ex: Throwable ->
                Log.e(
                    "TimerThread",
                    "Running task appeared exception! Thread [" + thread1.name + "], because [" + ex.message + "]"
                )
            }
        return thread
    }
}