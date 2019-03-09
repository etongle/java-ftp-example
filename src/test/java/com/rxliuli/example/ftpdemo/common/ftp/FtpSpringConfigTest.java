package com.rxliuli.example.ftpdemo.common.ftp;

import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author rxliuli
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FtpSpringConfigTest {
    private final Logger log = LoggerFactory.getLogger(getClass());
    @Rule
    public Timeout globalTimeout = Timeout.seconds(1);
    @Autowired
    private FtpOperator ftp;

    @Test
    public void put() throws UnsupportedEncodingException {
        // 上传数据
        final ByteArrayInputStream is = new ByteArrayInputStream("测试数据".getBytes("UTF-8"));
        final boolean result = ftp.put(is, "/test.txt");
        assertThat(result)
                .isTrue();
    }

    @Test
    public void exist() {
        // 判断数据是否存在于 ftp 服务器
        final boolean exist = ftp.exist("/test.txt");
        assertThat(exist)
                .isTrue();
    }

    @Test
    public void get() {
        // 从 ftp 服务器上下载数据
        ftp.get("/test.txt", is -> {
            try {
                final List<String> list = IOUtils.readLines(is);
                log.info("list: {}", list);
                assertThat(list)
                        .isNotEmpty();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
    }

    @Test
    public void mkdir() {
        // 创建文件夹
        assertThat(ftp.mkdir("/test"))
                .isTrue();
    }

    @Test
    public void mkdirR() {
        // 递归创建文件夹
        assertThat(ftp.mkdirR("/test/test2/test3"))
                .isTrue();
    }

    @Test
    public void ls() {
        // 获取目录下的文件信息列表
        final List<Stat> list = ftp.ls("/");
        log.info("list: {}", list.stream()
                .map(Stat::getPath)
                .collect(Collectors.joining("\n")));
        assertThat(list)
                .isNotEmpty();
    }

    @Test
    public void lsr() {
        // 获取目录下的文件信息列表
        final List<Stat> list = ftp.lsR("/");
        log.info("list: {}", list.stream()
                .map(Stat::getPath)
                .collect(Collectors.joining("\n")));
        assertThat(list)
                .isNotEmpty();
    }

    @Test
    public void rm() {
        // 删除单个文件
        assertThat(ftp.rm("/test.txt"))
                .isTrue();
    }

    @Test
    public void rmdir() {
        // 删除指定空目录
        assertThat(ftp.rmdir("/test/test2/test3"))
                .isTrue();
    }

    @Test
    public void rmdirR() {
        // 递归删除指定目录
        assertThat(ftp.rmdirR("/test"))
                .isTrue();
    }
}