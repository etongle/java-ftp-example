package com.rxliuli.example.ftpdemo.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 获取应用的配置文件
 *
 * @author rxliuli
 */
@Component("springConfigUtil")
@Order(Integer.MIN_VALUE)
public class SpringConfigUtil {
    private static Environment env;
    @Autowired
    private Environment environment;

    /**
     * 获取配置文件中的指定属性
     *
     * @param key        指定的 key，一般使用 xxx.xxx，不管是 .properties 还是 .yml 格式
     * @param clazz      类型对象
     * @param defaultVal 默认值
     * @param <T>        类型
     * @return 获取到的属性值，如果不存在则为默认值 {@code defaultVal}
     */
    public static <T> T get(String key, Class<T> clazz, T defaultVal) {
        return env.getProperty(key, clazz, defaultVal);
    }

    /**
     * 获取配置文件中的指定属性
     *
     * @param key   指定的 key，一般使用 xxx.xxx，不管是 .properties 还是 .yml 格式
     * @param clazz 类型对象
     * @param <T>   类型
     * @return 获取到的属性值，如果不存在则默认为 {@code null}
     */
    public static <T> T get(String key, Class<T> clazz) {
        return get(key, clazz, null);
    }

    /**
     * 获取配置文件中的指定属性
     *
     * @param key        指定的 key，一般使用 xxx.xxx，不管是 .properties 还是 .yml 格式
     * @param defaultVal 默认值
     * @return 获取到的属性值，如果不存在则为默认值 {@code defaultVal}
     */
    public static String get(String key, String defaultVal) {
        return env.getProperty(key, String.class, defaultVal);
    }

    /**
     * 获取到 String 类型属性
     *
     * @param key 指定的 key
     * @return 获取到的属性值，默认为 {@code null}
     */
    public static String get(String key) {
        return get(key, String.class, null);
    }

    /**
     * 获取配置文件中的指定属性
     *
     * @param key   指定的 key，一般使用 xxx.xxx，不管是 .properties 还是 .yml 格式
     * @param clazz 类型对象
     * @param <T>   类型
     * @return 获取到的属性值，如果不存在则默认为 {@link Optional#empty()}
     */
    public static <T> Optional<T> getOptional(String key, Class<T> clazz) {
        //noinspection ConstantConditions
        return Optional.ofNullable(get(key, clazz, null));
    }

    /**
     * 获取配置文件中的指定属性
     *
     * @param key 指定的 key，一般使用 xxx.xxx，不管是 .properties 还是 .yml 格式
     * @return 获取到的属性值，如果不存在则默认为 {@link Optional#empty()}
     */
    public static Optional<String> getOptional(String key) {
        return getOptional(key, String.class);
    }

    /**
     * 获取到 Integer 类型属性
     *
     * @param key 指定的 key
     * @return 获取到的属性值，默认为 {@code null}
     */
    public static Integer getInteger(String key) {
        return get(key, Integer.class, null);
    }

    /**
     * 获取到 Long 类型属性
     *
     * @param key 指定的 key
     * @return 获取到的属性值，默认为 {@code null}
     */
    public static Long getLong(String key) {
        return get(key, Long.class, null);
    }

    /**
     * 获取到 Boolean 类型属性
     *
     * @param key 指定的 key
     * @return 获取到的属性值，默认为 {@code null}
     */
    public static Boolean getBoolean(String key) {
        return get(key, Boolean.class, false);
    }

    /**
     * 获取到 Double 类型属性
     *
     * @param key 指定的 key
     * @return 获取到的属性值，默认为 {@code null}
     */
    public static Double getDouble(String key) {
        return get(key, Double.class, null);
    }

    /**
     * 获取到 Float 类型属性
     *
     * @param key 指定的 key
     * @return 获取到的属性值，默认为 {@code null}
     */
    public static Float getFloat(String key) {
        return get(key, Float.class, null);
    }

    /**
     * 获取到 Date 类型属性
     *
     * @param key     指定的 key
     * @param pattern Date 字符串的格式
     * @return 获取到的属性值，默认为 {@code null}
     */
    public static Date getDate(String key, String pattern) {
        try {
            return new SimpleDateFormat(pattern).parse(get(key));
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取到 LocalDateTime 类型属性
     * 注意：pattern 格式必须符合 Java 规定的，链接 https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html
     * 常见的格式是 yyyy-MM-dd HH:mm:ss，注意 HH 是大写的，这真的很坑！
     *
     * @param key     指定的 key
     * @param pattern LocalDateTime 字符串的格式
     * @return 获取到的属性值，默认为 {@code null}
     */
    public static LocalDateTime getLocalDateTime(String key, String pattern) {
        return LocalDateTime.parse(get(key), DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 获取到 LocalDateTime 类型属性
     * 默认格式为 yyyy-MM-dd[ [HH][:mm][:ss][.SSS]]，即日期一定要有，但时间部分却不然
     *
     * @param key 指定的 key
     * @return 获取到的属性值，默认为 {@code null}
     */
    public static LocalDateTime getLocalDateTime(String key) {
        return getLocalDateTime(key, "yyyy-MM-dd[ [HH][:mm][:ss][.SSS]]");
    }


    /**
     * 获取到 LocalDate 类型属性
     * 注意：pattern 格式必须符合 Java 规定的，链接 https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html
     * 常见的格式是 yyyy-MM-dd
     *
     * @param key     指定的 key
     * @param pattern LocalDate 字符串的格式
     * @return 获取到的属性值，默认为 {@code null}
     */
    public static LocalDate getLocalDate(String key, String pattern) {
        return LocalDate.parse(get(key), DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 获取到 LocalDate 类型属性
     * 默认格式为 yyyy-MM-dd
     *
     * @param key 指定的 key
     * @return 获取到的属性值，默认为 {@code null}
     */
    public static LocalDate getLocalDate(String key) {
        return getLocalDate(key, "yyyy-MM-dd");
    }

    /**
     * 获取到 LocalTime 类型属性
     * 注意：pattern 格式必须符合 Java 规定的，链接 https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html
     * 注意：如果配置文件中包含 10:11:12 这种时间，必须要使用引号包裹起来才行
     * 常见的格式是 HH:mm:ss
     *
     * @param key 指定的 key
     * @return 获取到的属性值，默认为 {@code null}
     */
    public static LocalTime getLocalTime(String key, String pattern) {
        return LocalTime.parse(get(key), DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 获取到 LocalTime 类型属性
     * 默认时间格式是 HH:mm:ss
     *
     * @param key 指定的 key
     * @return 获取到的属性值，默认为 {@code null}
     */
    public static LocalTime getLocalTime(String key) {
        return getLocalTime(key, "HH:mm:ss");
    }

    /**
     * 获取字符串列表的属性
     *
     * @param key 指定的 key
     * @return 获取到的属性值，默认为 {@link Collections#emptyList()}
     */
    public static List<String> getList(String key) {
        List<String> list = new ArrayList<>();
        for (int i = 0; ; i++) {
            final String v = env.getProperty(key + "[" + i + "]");
            //noinspection ConstantConditions
            if (v == null) {
                return list;
            }
            list.add(v);
        }
    }

    @PostConstruct
    public void init() {
        env = environment;
    }
}
