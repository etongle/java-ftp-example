package com.rxliuli.example.ftpdemo.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * 路径相关工具类
 *
 * @author rxliuli
 */
public class PathUtil {
    /**
     * 路径分隔符
     */
    public static final String SEPARATOR = "/";
    /**
     * 当前目录
     */
    public static final String CURRENT_DIR = ".";
    /**
     * 上一级目录
     */
    public static final String PARENT_DIR = "..";

    /**
     * 拼接两个路径
     *
     * @param pathStart 开始路径
     * @param pathEnd   结束路径
     * @return 拼接完成的两个路径
     */
    public static String join(String pathStart, String pathEnd) {
        if (StringUtils.endsWith(pathStart, SEPARATOR)) {
            return StringUtils.replaceAll(pathStart + pathEnd, SEPARATOR + SEPARATOR, SEPARATOR);
        }
        if (StringUtils.startsWith(pathEnd, SEPARATOR)) {
            return pathStart + pathEnd;
        }
        return pathStart + SEPARATOR + pathEnd;
    }

    /**
     * 拼接多个路径
     *
     * @param paths 路径数组
     * @return 拼接完成的路径
     */
    public static String join(String... paths) {
        return Arrays.stream(paths).reduce(PathUtil::join).orElse("");
    }

    /**
     * 分离文件路径中的目录路径和文件名
     *
     * @param path 文件路径
     * @return 分离后得到的目录路径
     */
    public static String getParentDir(String path) {
        final String parentPath = StringUtils.substringBeforeLast(path, SEPARATOR);
        return parentPath.isEmpty() ? "/" : parentPath;
    }

    /**
     * 分离文件路径中的目录路径和文件名
     *
     * @param path 文件路径
     * @return 分离后得到的文件名
     */
    public static String getFileName(String path) {
        return StringUtils.substringAfterLast(path, SEPARATOR);
    }
}
