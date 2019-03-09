package com.rxliuli.example.ftpdemo.common.ftp.basic;

import com.rxliuli.example.ftpdemo.common.ftp.BaseFtpClientConfig;
import org.apache.commons.net.ftp.FTP;

/**
 * 普通的 ftp 配置对象
 *
 * @author rxliuli
 */
public class BasicFtpClientConfig extends BaseFtpClientConfig {
    /**
     * 本地字符集的字符集，默认为 GBK
     */
    public static final String DEFAULT_LOCAL_CHARSET = "ISO-8859-1";
    /**
     * 服务器字符集，默认为 ISO-8859-1
     */
    public static final String DEFAULT_SERVER_CHARSET = "GBK";
    /**
     * 文件传输形式，默认以二进制流传输
     */
    public static final int DEFAULT_FILE_TYPE = FTP.BINARY_FILE_TYPE;
    /**
     * 本地字符集的字符集，默认为 GBK
     */
    private String localCharset = DEFAULT_LOCAL_CHARSET;
    /**
     * 服务器字符集，默认为 ISO-8859-1
     */
    private String serverCharset = DEFAULT_SERVER_CHARSET;
    /**
     * 文件传输形式，默认以二进制流传输
     */
    private Integer fileType = DEFAULT_FILE_TYPE;

    public BasicFtpClientConfig(String username, String host, String password, Integer port) {
        super(username, host, password, port);
    }

    public String getLocalCharset() {
        return localCharset;
    }

    public BasicFtpClientConfig setLocalCharset(String localCharset) {
        this.localCharset = localCharset;
        return this;
    }

    public Integer getFileType() {
        return fileType;
    }

    public BasicFtpClientConfig setFileType(Integer fileType) {
        this.fileType = fileType;
        return this;
    }

    public String getServerCharset() {
        return serverCharset;
    }

    public BasicFtpClientConfig setServerCharset(String serverCharset) {
        this.serverCharset = serverCharset;
        return this;
    }
}
