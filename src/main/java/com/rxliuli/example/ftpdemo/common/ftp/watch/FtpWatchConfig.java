package com.rxliuli.example.ftpdemo.common.ftp.watch;

import com.rxliuli.example.ftpdemo.common.ftp.Stat;
import com.rxliuli.example.ftpdemo.common.util.ListUtil;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 监视文件变化的配置类
 *
 * @author rxliuli
 */
public class FtpWatchConfig {
    /**
     * 扫描间隔时间单位，默认为毫秒
     */
    private final TimeUnit unit;
    /**
     * 扫描间隔
     */
    private final Integer interval;
    /**
     * 扫描的目录
     */
    private final String path;
    /**
     * 超时时间，以分钟计算，默认为 10 分钟
     */
    private final Integer timeout;
    /**
     * ftp 调度监听器的回调函数
     * 主要根据监听目录的变化去调用相应的监听列表函数
     */
    private final BiConsumer<Map<ListUtil.ListDiffState, List<Stat>>, ConcurrentHashMap<Function<Map<ListUtil.ListDiffState, List<Stat>>, Optional<Stat>>, Consumer<Stat>>> callback;

    public FtpWatchConfig(TimeUnit unit, Integer interval, String path, Integer timeout, BiConsumer<Map<ListUtil.ListDiffState, List<Stat>>, ConcurrentHashMap<Function<Map<ListUtil.ListDiffState, List<Stat>>, Optional<Stat>>, Consumer<Stat>>> callback) {
        this.unit = unit;
        this.interval = interval;
        this.path = path;
        this.callback = callback;
        this.timeout = timeout;
    }

    public FtpWatchConfig(TimeUnit unit, Integer interval, String path, BiConsumer<Map<ListUtil.ListDiffState, List<Stat>>, ConcurrentHashMap<Function<Map<ListUtil.ListDiffState, List<Stat>>, Optional<Stat>>, Consumer<Stat>>> callback) {
        this(TimeUnit.MILLISECONDS, interval, path, 10, callback);
    }

    public FtpWatchConfig(Integer interval, String path, BiConsumer<Map<ListUtil.ListDiffState, List<Stat>>, ConcurrentHashMap<Function<Map<ListUtil.ListDiffState, List<Stat>>, Optional<Stat>>, Consumer<Stat>>> callback) {
        this(TimeUnit.MILLISECONDS, interval, path, callback);
    }

    public TimeUnit getUnit() {
        return unit;
    }


    public Integer getInterval() {
        return interval;
    }

    public String getPath() {
        return path;
    }

    public BiConsumer<Map<ListUtil.ListDiffState, List<Stat>>, ConcurrentHashMap<Function<Map<ListUtil.ListDiffState, List<Stat>>, Optional<Stat>>, Consumer<Stat>>> getCallback() {
        return callback;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FtpWatchConfig)) {
            return false;
        }
        FtpWatchConfig that = (FtpWatchConfig) o;
        return getUnit() == that.getUnit() &&
                Objects.equals(getInterval(), that.getInterval()) &&
                Objects.equals(getPath(), that.getPath()) &&
                Objects.equals(getCallback(), that.getCallback());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUnit(), getInterval(), getPath(), getCallback());
    }

    public Integer getTimeout() {
        return timeout;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FtpWatchConfig{");
        sb.append("unit=").append(unit);
        sb.append(", interval=").append(interval);
        sb.append(", path='").append(path).append('\'');
        sb.append(", callback=").append(callback);
        sb.append('}');
        return sb.toString();
    }
}
