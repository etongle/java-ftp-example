package com.rxliuli.example.ftpdemo.common.ftp;

import com.rxliuli.example.ftpdemo.common.ftp.basic.BasicFtpClientConfig;
import com.rxliuli.example.ftpdemo.common.ftp.sftp.SftpClientConfig;
import com.rxliuli.example.ftpdemo.common.ftp.watch.FtpWatchConfig;
import com.rxliuli.example.ftpdemo.common.util.ListUtil;
import com.rxliuli.example.ftpdemo.common.util.SpringConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author rxliuli
 */
@Configuration
@DependsOn("springConfigUtil")
public class FtpSpringConfig {
    private static SftpClientConfig sftpClientConfig;
    private static BasicFtpClientConfig basicFtpClientConfig;
    private static FtpWatchConfig ftpWatchConfig;
    private final Logger log = LoggerFactory.getLogger(FtpSpringConfig.class);

    @PostConstruct
    void init() {
        //基本信息
        final String username = SpringConfigUtil.get(com.zx.idc.common.ftp.FtpSpringConfigNameConstants.FTP_USERNAME);
        final String host = SpringConfigUtil.get(com.zx.idc.common.ftp.FtpSpringConfigNameConstants.FTP_HOST);
        final String password = SpringConfigUtil.get(com.zx.idc.common.ftp.FtpSpringConfigNameConstants.FTP_PASSWORD);
        if (StringUtils.isAnyEmpty(username, host, password)) {
            log.error("Read the ftp and sftp configuration exception, read the configuration: username {}, host {}, password {}", username, host, password);
            return;
        }
        initFtp(username, host, password);
        initSftp(username, host, password);
        initFtpWatch();
    }

    /**
     * 初始化标准 ftp 配置项
     *
     * @param username 用户名
     * @param host     主机名
     * @param password 密码
     */
    private void initFtp(String username, String host, String password) {
        final Integer port = SpringConfigUtil.get(com.zx.idc.common.ftp.FtpSpringConfigNameConstants.FTP_BASIC_PORT, Integer.class, 21);
        final String localCharset = SpringConfigUtil.get(com.zx.idc.common.ftp.FtpSpringConfigNameConstants.FTP_BASIC_LOCAL_CHARSET, BasicFtpClientConfig.DEFAULT_LOCAL_CHARSET);
        final String serverCharset = SpringConfigUtil.get(com.zx.idc.common.ftp.FtpSpringConfigNameConstants.FTP_BASIC_SERVER_CHARSET, BasicFtpClientConfig.DEFAULT_SERVER_CHARSET);
        final Integer fileType = SpringConfigUtil.get(com.zx.idc.common.ftp.FtpSpringConfigNameConstants.FTP_BASIC_FILE_TYPE, Integer.class, BasicFtpClientConfig.DEFAULT_FILE_TYPE);
        basicFtpClientConfig = new BasicFtpClientConfig(username, host, password, port)
                .setLocalCharset(localCharset)
                .setServerCharset(serverCharset)
                .setFileType(fileType);
        log.info("Read the ftp configuration completed, read the configuration: username {}, host {}, password {}, port: {}, localCharset {}, serverCharset {}, serverCharset {}", username, host, password, port, localCharset, serverCharset, serverCharset);
    }

    /**
     * 初始化 sftp 配置项
     *
     * @param username 用户名
     * @param host     主机名
     * @param password 密码
     */
    private void initSftp(String username, String host, String password) {
        final Integer port = SpringConfigUtil.get(com.zx.idc.common.ftp.FtpSpringConfigNameConstants.FTP_SFTP_PORT, Integer.class, 22);
        final String strictHostKeyChecking = SpringConfigUtil.get(com.zx.idc.common.ftp.FtpSpringConfigNameConstants.FTP_SFTP_STRICT_HOST_KEY_CHECKING, SftpClientConfig.DEFAULT_STRICT_HOST_KEY_CHECKING);
        final String encoding = SpringConfigUtil.get(com.zx.idc.common.ftp.FtpSpringConfigNameConstants.FTP_SFTP_ENCODING, SftpClientConfig.DEFAULT_ENCODING);
        sftpClientConfig = new SftpClientConfig(username, host, password, port)
                .setStrictHostKeyChecking(strictHostKeyChecking)
                .setEncoding(encoding);
        log.info("Read the sftp configuration completed, read the configuration: username {}, host {}, password {}, port: {}, strictHostKeyChecking {}, encoding, {}", username, host, password, port, strictHostKeyChecking, encoding);
    }

    /**
     * 初始化 ftp 监听器的配置
     */
    private void initFtpWatch() {
        final Integer interval = SpringConfigUtil.get(com.zx.idc.common.ftp.FtpSpringConfigNameConstants.FTP_WATCH_INTERVAL, Integer.class, 1000);
        final String path = SpringConfigUtil.get(com.zx.idc.common.ftp.FtpSpringConfigNameConstants.FTP_WATCH_PATH, "/");
        final Integer timeout = SpringConfigUtil.get(com.zx.idc.common.ftp.FtpSpringConfigNameConstants.FTP_TIMEOUT, Integer.class, 10);
        ftpWatchConfig = new FtpWatchConfig(TimeUnit.MILLISECONDS, interval, path, timeout, (statMap, listenerMap) -> {
            //遍历找到符合条件的监听器
            listenerMap.forEach((key, value) -> {
                if (statMap.get(ListUtil.ListDiffState.right).isEmpty()) {
                    return;
                }
                final Optional<Stat> stat = key.apply(statMap);
                stat.ifPresent(s -> {
                    //执行回调
                    value.accept(s);
                    //删除掉监听器
                    listenerMap.remove(key);
                });
            });
        });
        log.info("Initial ftp watch config completed: {}", ftpWatchConfig);
    }

    /**
     * 提供一个标准的 ftp 操作对象
     */
    @Bean("basicFtpOperator")
    public FtpOperator basicFtpOperator() {
        if (basicFtpClientConfig == null) {
            log.error("Initial Bean basicFtpOperator failed, basic ftp client config is null!");
            return null;
        }
        final FtpOperator instance = BaseFtpOperatorFactory.getInstance(basicFtpClientConfig);
        if (SpringConfigUtil.getBoolean(com.zx.idc.common.ftp.FtpSpringConfigNameConstants.FTP_WATCH_ENABLE_BASIC)) {
            instance.initWatch(ftpWatchConfig);
        }
        return instance;
    }

    /**
     * 提供一个 sftp 操作对象
     */
    @Primary
    @Bean("sftpOperator")
    public FtpOperator sftpOperator() {
        if (sftpClientConfig == null) {
            log.error("Initial Bean sftpOperator failed, basic ftp client config is null!");
            return null;
        }
        final FtpOperator instance = BaseFtpOperatorFactory.getInstance(sftpClientConfig);
        if (SpringConfigUtil.getBoolean(com.zx.idc.common.ftp.FtpSpringConfigNameConstants.FTP_WATCH_ENABLE_SFTP)) {
            instance.initWatch(ftpWatchConfig);
        }
        return instance;
    }
}
