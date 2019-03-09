package com.rxliuli.example.ftpdemo.common.ftp.sftp;


import com.rxliuli.example.ftpdemo.common.ftp.BaseFtpClientConfig;

/**
 * sftp 的客户端配置
 *
 * @author rxliuli
 */
public class SftpClientConfig extends BaseFtpClientConfig {
    public static final String DEFAULT_STRICT_HOST_KEY_CHECKING = "no";
    public static final String DEFAULT_ENCODING = "UTF-8";
    /**
     * 是否进行严格 RSA 密钥检查，默认使用密码登录所以不检查
     */
    private String strictHostKeyChecking = DEFAULT_STRICT_HOST_KEY_CHECKING;
    /**
     * sftp 发送命令的编码格式，默认全局使用 UTF-8（最广泛也最安全）
     */
    private String encoding = DEFAULT_ENCODING;

    public SftpClientConfig(String username, String host, String password, Integer port) {
        super(username, host, password, port);
    }

    public String getStrictHostKeyChecking() {
        return strictHostKeyChecking;
    }

    public SftpClientConfig setStrictHostKeyChecking(String strictHostKeyChecking) {
        this.strictHostKeyChecking = strictHostKeyChecking;
        return this;
    }

    public String getEncoding() {
        return encoding;
    }

    public SftpClientConfig setEncoding(String encoding) {
        this.encoding = encoding;
        return this;
    }
}
