package com.rxliuli.example.ftpdemo.common.util;

/**
 * 线程相关工具类
 *
 * @author rxliuli
 */
public class ThreadUtil {
    /**
     * 将当前线程休眠指定的时间
     *
     * @param millis 毫秒
     */
    public static void sleep(long millis) {
        sleep(millis, 0);
    }

    /**
     * 将当前线程休眠指定的时间
     * 此处主要是将检查型异常转换为运行时异常，避免每次都要额外的手动捕获
     *
     * @param millis 毫秒
     * @param nanos  额外的纳秒
     */
    public static void sleep(long millis, int nanos) {
        try {
            Thread.sleep(millis, nanos);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
