package com.rxliuli.example.ftpdemo.common.ftp.sftp;

import com.rxliuli.example.ftpdemo.common.ftp.BaseFtpOperatorFactory;

/**
 * @author rxliuli
 */
public class SftpOperatorFactory implements BaseFtpOperatorFactory {
    private SftpClientConfig baseFtpClientConfig;

    public SftpOperatorFactory(SftpClientConfig baseFtpClientConfig) {
        this.baseFtpClientConfig = baseFtpClientConfig;
    }

    @Override
    public SftpOperator createOperator() {
        return new SftpOperator(baseFtpClientConfig);
    }
}
