package com.zx.idc.common.ftp;

/**
 * ftp/sftp 在 application.yml 中需要的配置项
 *
 * @author rxliuli
 */
public class FtpSpringConfigNameConstants {
    //region ftp 通用配置项，必需
    public static final String FTP_USERNAME = "ftp.username";
    public static final String FTP_HOST = "ftp.host";
    public static final String FTP_PASSWORD = "ftp.password";
    //endregion

    //region 标准 ftp 的可选配置项
    public static final String FTP_BASIC_PORT = "ftp.basic.port";
    public static final String FTP_BASIC_LOCAL_CHARSET = "ftp.basic.localCharset";
    public static final String FTP_BASIC_SERVER_CHARSET = "ftp.basic.serverCharset";
    public static final String FTP_BASIC_FILE_TYPE = "ftp.basic.fileType";
    //endregion

    //region sftp 的可选配置项
    public static final String FTP_SFTP_PORT = "ftp.sftp.port";
    public static final String FTP_SFTP_STRICT_HOST_KEY_CHECKING = "ftp.sftp.strictHostKeyChecking";
    public static final String FTP_SFTP_ENCODING = "ftp.sftp.encoding";
    //endregion

    //region ftp 监听器可选配置项
    public static final String FTP_WATCH_INTERVAL = "ftp.watch.interval";
    public static final String FTP_WATCH_PATH = "ftp.watch.path";
    public static final String FTP_TIMEOUT = "ftp.timeout";
    public static final String FTP_WATCH_ENABLE_BASIC = "ftp.watch.enable.basic";
    public static final String FTP_WATCH_ENABLE_SFTP = "ftp.watch.enable.sftp";
    //endregion
}
