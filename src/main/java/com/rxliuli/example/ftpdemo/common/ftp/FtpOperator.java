package com.rxliuli.example.ftpdemo.common.ftp;

import com.rxliuli.example.ftpdemo.common.ftp.watch.FtpWatch;
import com.rxliuli.example.ftpdemo.common.ftp.watch.FtpWatchConfig;
import com.rxliuli.example.ftpdemo.common.ftp.watch.FtpWatchFactory;
import com.rxliuli.example.ftpdemo.common.util.ListUtil;
import com.rxliuli.example.ftpdemo.common.util.PathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rxliuli.example.ftpdemo.common.util.PathUtil.SEPARATOR;
import static com.rxliuli.example.ftpdemo.common.util.PathUtil.join;


/**
 * ftp 操作的接口
 * 具体执行由具体的子类去实现
 *
 * @author rxliuli
 */
public interface FtpOperator {
    Logger log = LoggerFactory.getLogger(FtpOperator.class);


    /**
     * 上传文件
     * 默认创建不存在的父级目录
     *
     * @param localIs        本地文件
     * @param remoteFilePath 远程文件路径
     * @return 是否上传成功
     */
    default boolean put(InputStream localIs, String remoteFilePath) {
        return put(localIs, remoteFilePath, true);
    }

    /**
     * 上传文件
     * 默认创建不存在的父级目录
     *
     * @param localFile      本地文件
     * @param remoteFilePath 远程文件路径
     * @return 是否上传成功
     */
    default boolean put(File localFile, String remoteFilePath) {
        return put(localFile, remoteFilePath, true);
    }

    /**
     * 上传文件
     *
     * @param localIs        本地文件
     * @param remoteFilePath 远程文件路径
     * @param isCreateDir    是否创建父目录
     * @return 是否上传成功
     */
    boolean put(InputStream localIs, String remoteFilePath, boolean isCreateDir);

    /**
     * 上传文件
     *
     * @param localFile      本地文件
     * @param remoteFilePath 远程文件路径
     * @param isCreateDir    是否创建父目录
     * @return 是否上传成功
     */
    default boolean put(File localFile, String remoteFilePath, boolean isCreateDir) {
        try {
            return put(new FileInputStream(localFile), remoteFilePath, isCreateDir);
        } catch (FileNotFoundException e) {
            log.error("sftp put operation failed: {}", e);
            return false;
        }
    }

    /**
     * 下载文件
     * 下载之前会自动创建目录
     *
     * @param remoteFilePath 远程文件路径
     * @param localFile      本地文件
     * @return 是否下载成功
     */
    default boolean get(String remoteFilePath, File localFile) {
        return get(remoteFilePath, localFile, true);
    }

    /**
     * 下载文件
     *
     * @param remoteFilePath 远程文件路径
     * @param localFile      本地文件
     * @param isCreateFile   是否创建父目录
     * @return 是否下载成功
     */
    boolean get(String remoteFilePath, File localFile, boolean isCreateFile);

    /**
     * 下载文件
     * 此处使用回调函数对流进行操作
     *
     * @param remoteFilePath 远程文件路径
     * @param action         操作
     * @param <R>            返回类型
     * @return 对流操作的返回值
     */
    <R> R get(String remoteFilePath, Function<InputStream, R> action);

    /**
     * 下载文件
     * 此处使用回调函数对流进行操作
     *
     * @param remoteFilePath 远程文件路径
     * @param action         操作
     */
    default void get(String remoteFilePath, Consumer<InputStream> action) {
        get(remoteFilePath, is -> {
            action.accept(is);
            return null;
        });
    }

    /**
     * 创建目录
     * 目录必须以 {@link PathUtil#SEPARATOR} 进行分割，并且以 / 开头
     * 如果目录存在则返回 true，否则尝试创建目录
     *
     * @param path 目录路径
     * @return 是否创建成功
     */
    boolean mkdir(String path);

    /**
     * 递归创建多级目录
     * 目录必须以 {@link PathUtil#SEPARATOR} 进行分割，并且以 / 开头
     *
     * @param path 文件路径
     * @return 文件目录是否创建成功
     */
    default boolean mkdirR(String path) {
        final String[] pathUnits = path.split(SEPARATOR);
        String temp = "";
        for (String pathUnit : pathUnits) {
            temp = join(temp, pathUnit);
            if (!exist(temp) && !mkdir(temp)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 删除文件
     *
     * @param path 文件相对路径
     * @return 是否删除成功
     */
    boolean rm(String path);

    /**
     * 删除空目录
     * 如果是非空目录则一定会删除失败！
     *
     * @param path 空目录相对路径
     * @return 是否删除成功
     */
    boolean rmdir(String path);

    /**
     * 查看目录下的所有文件/目录
     * 只会获取当前目录下的文件/目录，不会获取子目录下的文件信息
     *
     * @param path 相对路径
     * @return 当前目录下的文件信息列表
     */
    List<Stat> ls(String path);

    /**
     * 判断指定路径的文件/目录是否存在
     *
     * @param path 相对路径
     * @return 是否存在
     */
    boolean exist(String path);

    /**
     * 递归获取指定目录下的所有文件信息
     *
     * @param path 指定目录
     * @return 获取所有文件信息
     */
    default List<Stat> lsR(String path) {
        final List<Stat> ls = ls(path);
        return ls.stream()
                .flatMap(stat -> {
                    final Stream<Stat> stream = Stream.of(stat);
                    if (!stat.getIsDir()) {
                        return stream;
                    }
                    return Stream.concat(stream, lsR(stat.getPath()).stream());
                })
                .collect(Collectors.toList());
    }

    /**
     * 递归删除指定目录
     *
     * @param path 指定目录
     * @return 删除是否成功
     */
    default boolean rmdirR(String path) {
        return lsR(path).stream()
                .sorted(Comparator.comparing(stat -> -stat.getPath().length()))
                .map(stat -> {
                    if (stat.getIsDir()) {
                        return rmdir(stat.getPath());
                    } else {
                        return rm(stat.getPath());
                    }
                })
                .reduce((res, item) -> res && item)
                .orElse(true) && rmdir(path);
    }

    /**
     * 监听文件变化
     * 如果只是监听新出现的文件，可以使用 {@link #watch(String)}
     *
     * @param f        临界条件
     * @param callback 回调函数
     */
    @Deprecated
    default void watch(Function<Map<ListUtil.ListDiffState, List<Stat>>, Optional<Stat>> f, Consumer<Stat> callback) {
        FtpWatch ftpWatch = FtpWatchFactory.getInstance(this);
        if (ftpWatch == null) {
            throw new RuntimeException("You don't have a profile listener");
        }
        ftpWatch.addListener(f, callback);
    }

    /**
     * 监听文件出现
     * 当文件新增后将执行回调函数 {@param callback}，请使用现代的 {@link #watch(String)}
     *
     * @param path     新增文件的路径
     * @param callback 新增文件出现时执行的回调函数
     */
    @Deprecated
    default void watch(String path, Consumer<Stat> callback) {
        watch(map -> map.get(ListUtil.ListDiffState.right).stream()
                .filter(stat -> Objects.equals(stat.getPath(), path))
                .findFirst(), callback);
    }

    /**
     * 监听文件变化
     * 该方法返回一个 {@link CompletableFuture<Stat>}，更加现代化的异步
     * 如果只是监听新出现的文件，可以使用 {@link #watch(String)}
     *
     * @param condition 临界条件
     * @return 异步完成对象
     */
    default CompletableFuture<Stat> watch(Function<Map<ListUtil.ListDiffState, List<Stat>>, Optional<Stat>> condition) {
        FtpWatch ftpWatch = FtpWatchFactory.getInstance(this);
        if (ftpWatch == null) {
            throw new RuntimeException("You don't have a profile listener");
        }
        return ftpWatch.addListener(condition);
    }

    /**
     * 监听文件出现
     * 该方法返回一个 {@link CompletableFuture<Stat>}，更加现代化的异步
     *
     * @param path 新增文件的路径
     * @return 异步完成对象
     */
    default CompletableFuture<Stat> watch(String path) {
        return watch((Function<Map<ListUtil.ListDiffState, List<Stat>>, Optional<Stat>>) map -> map.get(ListUtil.ListDiffState.right).stream()
                .filter(stat -> stat.getPath().equals(path))
                .findFirst());
    }

    /**
     * 监听文件出现
     * 该方法返回一个 {@link CompletableFuture<Stat>}，更加现代化的异步
     *
     * @param condition 新增文件的路径的路径判断条件
     * @return 异步完成对象
     */
    default CompletableFuture<Stat> watch(Predicate<String> condition) {
        return watch((Function<Map<ListUtil.ListDiffState, List<Stat>>, Optional<Stat>>) map -> map.get(ListUtil.ListDiffState.right).stream()
                .filter(stat -> condition.test(stat.getPath()))
                .findFirst());
    }


    /**
     * 如果这个 FtpOperator 对象需要监听文件变化，请务必调用此方法
     *
     * @param ftpWatchConfig ftp 监听器的配置
     * @return 返回一个 ftp 监听器对象
     */
    default FtpWatch initWatch(FtpWatchConfig ftpWatchConfig) {
        return FtpWatchFactory.newInstance(this, ftpWatchConfig);
    }
}
