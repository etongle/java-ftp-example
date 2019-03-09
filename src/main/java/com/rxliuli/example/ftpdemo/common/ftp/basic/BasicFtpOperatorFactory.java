package com.rxliuli.example.ftpdemo.common.ftp.basic;

import com.rxliuli.example.ftpdemo.common.ftp.BaseFtpOperatorFactory;

/**
 * @author rxliuli
 */
public class BasicFtpOperatorFactory implements BaseFtpOperatorFactory {
    private BasicFtpClientConfig config;

    public BasicFtpOperatorFactory(BasicFtpClientConfig config) {
        this.config = config;
    }

    @Override
    public BasicFtpOperator createOperator() {
        return new BasicFtpOperator(config);
    }
}
