package com.rxliuli.example.ftpdemo.common.ftp;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * ftp 客户端抽象基类
 * 定义了一些标准的 ftp 连接必须属性
 *
 * @author rxliuli
 */
public abstract class BaseFtpClientConfig {
    /**
     * ftp 登录账号
     */
    private String username;
    /**
     * ftp 服务器地址
     */
    private String host;
    /**
     * ftp 登录密码
     */
    private String password;
    /**
     * 端口号
     */
    private Integer port;

    public BaseFtpClientConfig() {
    }

    public BaseFtpClientConfig(String username, String host, String password, Integer port) {
        this.username = username;
        this.host = host;
        this.password = password;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public BaseFtpClientConfig setHost(String host) {
        this.host = host;
        return this;
    }

    public Integer getPort() {
        return port;
    }

    public BaseFtpClientConfig setPort(Integer port) {
        this.port = port;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public BaseFtpClientConfig setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public BaseFtpClientConfig setPassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
