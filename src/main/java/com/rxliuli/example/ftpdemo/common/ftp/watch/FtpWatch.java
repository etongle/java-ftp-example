package com.rxliuli.example.ftpdemo.common.ftp.watch;

import com.rxliuli.example.ftpdemo.common.ftp.FtpOperator;
import com.rxliuli.example.ftpdemo.common.ftp.Stat;
import com.rxliuli.example.ftpdemo.common.util.ListUtil;
import com.rxliuli.example.ftpdemo.common.util.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * ftp 监听操作
 *
 * @author rxliuli
 */
public class FtpWatch {
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 保存有这个 ftp 监听器的映射表
     * k 是临界条件，代表在什么时候需要进行回调
     * v 是回调函数，在满足临界条件后调用一次，然后删除
     */
    private final ConcurrentHashMap<Function<Map<ListUtil.ListDiffState, List<Stat>>, Optional<Stat>>, Consumer<Stat>> listenerMap = new ConcurrentHashMap<>();
    /**
     * ftp 操作类
     */
    private final FtpOperator ftpOperator;
    /**
     * 监听配置
     */
    private final FtpWatchConfig ftpWatchConfig;
    /**
     * 旧的 stat 列表
     */
    private List<Stat> oldStatList;

    /**
     * 避免直接使用构造函数初始化 FtpWatch
     */
    protected FtpWatch(FtpOperator ftpOperator, FtpWatchConfig ftpWatchConfig) {
        this.ftpOperator = ftpOperator;
        this.ftpWatchConfig = ftpWatchConfig;
    }

    /**
     * 监听根目录的变化，并通知所有异步请求
     */
    public void startWatch() {
        oldStatList = ftpOperator.lsR(ftpWatchConfig.getPath());
        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> {
                    try {
                        final List<Stat> statList = ftpOperator.lsR(ftpWatchConfig.getPath());
                        final Map<ListUtil.ListDiffState, List<Stat>> different = ListUtil.different(oldStatList, statList);
                        oldStatList = statList;
                        ftpWatchConfig.getCallback().accept(different, listenerMap);
                    } catch (Exception e) {
                        log.error("Listening to directory change scheduled task exception: {}", e);
                    }
                },
                0,
                ftpWatchConfig.getInterval(),
                ftpWatchConfig.getUnit()
        );
    }

    /**
     * 添加一个监听器
     *
     * @param critical 临界条件
     * @param callback 回调函数
     */
    public void addListener(Function<Map<ListUtil.ListDiffState, List<Stat>>, Optional<Stat>> critical, Consumer<Stat> callback) {
        listenerMap.put(critical, callback);
    }

    /**
     * 添加一个监听器
     *
     * @param condition 临界条件
     * @return 异步完成的对象
     */
    public CompletableFuture<Stat> addListener(Function<Map<ListUtil.ListDiffState, List<Stat>>, Optional<Stat>> condition) {
        final List<Stat> temp = new LinkedList<>(this.oldStatList);
        final LocalDateTime start = LocalDateTime.now();
        return CompletableFuture.supplyAsync(() -> {
            while (true) {
                final Map<ListUtil.ListDiffState, List<Stat>> different = ListUtil.different(temp, oldStatList);
                //如果发现符合条件就返回当前结果
                final Optional<Stat> result = condition.apply(different);
                if (result.isPresent()) {
                    return result.get();
                }
                temp.clear();
                temp.addAll(this.oldStatList);
                if (ChronoUnit.MINUTES.between(start, LocalDateTime.now()) > 10) {
                    return null;
                }
                //每次暂停 100ms
                ThreadUtil.sleep(100);
            }
        });
    }

    public FtpOperator getFtpOperator() {
        return ftpOperator;
    }

    public FtpWatchConfig getFtpWatchConfig() {
        return ftpWatchConfig;
    }
}
