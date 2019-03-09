package com.rxliuli.example.ftpdemo.common.ftp;

import com.rxliuli.example.ftpdemo.common.ftp.basic.BasicFtpClientConfig;
import com.rxliuli.example.ftpdemo.common.ftp.basic.BasicFtpOperatorFactory;
import com.rxliuli.example.ftpdemo.common.ftp.sftp.SftpClientConfig;
import com.rxliuli.example.ftpdemo.common.ftp.sftp.SftpOperatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ftp 操作的工厂类
 * 用于根据不同的配置对象创建不同的子工厂，然后创建对应 ftp 操作对象
 * 子工厂之所以存在是因为需要保证之后需要线程池管理时的拓展性
 *
 * @author rxliuli
 */
public interface BaseFtpOperatorFactory {
    Logger log = LoggerFactory.getLogger(BaseFtpOperatorFactory.class);

    /**
     * 获取一个 ftp 子类实例
     *
     * @param baseFtpClientConfig 基本的 ftp 配置类
     * @return 一个 FtpOperator 的子类对象
     */
    static FtpOperator getInstance(BaseFtpClientConfig baseFtpClientConfig) {
        if (baseFtpClientConfig instanceof BasicFtpClientConfig) {
            return new BasicFtpOperatorFactory((BasicFtpClientConfig) baseFtpClientConfig).createOperator();
        } else if (baseFtpClientConfig instanceof SftpClientConfig) {
            return new SftpOperatorFactory((SftpClientConfig) baseFtpClientConfig).createOperator();
        } else {
            log.error("ftp operator create failed, config is {}", baseFtpClientConfig);
            throw new RuntimeException("ftp operator create failed, config is " + baseFtpClientConfig);
        }
    }

    /**
     * 创建一个 ftp 操作对象
     *
     * @return FtpOperator 操作对象
     */
    FtpOperator createOperator();
}
