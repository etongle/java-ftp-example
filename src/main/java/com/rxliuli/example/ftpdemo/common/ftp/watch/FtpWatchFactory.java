package com.rxliuli.example.ftpdemo.common.ftp.watch;

import com.rxliuli.example.ftpdemo.common.ftp.FtpOperator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ftp 监听器的工厂类
 *
 * @author rxliuli
 */
public class FtpWatchFactory {
    private static final Map<FtpOperator, FtpWatch> FTP_WATCH_MAP = new ConcurrentHashMap<>();

    /**
     * 根据 ftp 操作对象和 ftp 监听配置创建 ftp 监听器对象
     *
     * @param ftpOperator    ftp 操作对象
     * @param ftpWatchConfig ftp 监听配置
     * @return ftp 监听器对象
     */
    public static FtpWatch newInstance(FtpOperator ftpOperator, FtpWatchConfig ftpWatchConfig) {
        final FtpWatch oldFtpWatch = FTP_WATCH_MAP.getOrDefault(ftpOperator, null);
        if (oldFtpWatch != null && oldFtpWatch.getFtpWatchConfig().equals(ftpWatchConfig)) {
            return oldFtpWatch;
        }
        final FtpWatch ftpWatch = new FtpWatch(ftpOperator, ftpWatchConfig);
        FTP_WATCH_MAP.put(ftpOperator, ftpWatch);
        ftpWatch.startWatch();
        return ftpWatch;
    }

    /**
     * 根据 ftp 操作对象获取 ftp 监听器对象
     *
     * @param ftpOperator ftp 操作对象
     * @return ftp 监听器对象
     */
    public static FtpWatch getInstance(FtpOperator ftpOperator) {
        return FTP_WATCH_MAP.getOrDefault(ftpOperator, null);
    }
}
