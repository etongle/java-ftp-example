package com.rxliuli.example.ftpdemo.common.util;

/**
 * 允许抛出错误的 function 接口
 * 主要在 lambda 需要抛出异常统一处理的情况下使用
 * 借贷模式 using
 *
 * @param <T> 参数类型
 * @param <R> 返回值类型
 * @author rxliuli
 */
public interface AllowErrorFunction<T, R> {
    /**
     * 子对象需要实现的函数
     * 其实主要用于 lambda 表达式
     *
     * @param t 对象
     * @return 返回一个结果
     * @throws Throwable 可能抛出的任何检查型异常
     */
    R apply(T t) throws Throwable;
}
