package com.rxliuli.example.ftpdemo.common.ftp.basic;

import com.rxliuli.example.ftpdemo.common.ftp.FtpOperator;
import com.rxliuli.example.ftpdemo.common.ftp.Stat;
import com.rxliuli.example.ftpdemo.common.util.AllowErrorFunction;
import com.rxliuli.example.ftpdemo.common.util.GlobalException;
import com.rxliuli.example.ftpdemo.common.util.PathUtil;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.rxliuli.example.ftpdemo.common.util.PathUtil.*;


/**
 * 基本 ftp 操作的实现类
 *
 * @author rxliuli
 */
public class BasicFtpOperator implements FtpOperator {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final BasicFtpClientConfig config;

    public BasicFtpOperator(BasicFtpClientConfig config) {
        this.config = config;
    }

    private <R> R using(AllowErrorFunction<FTPClient, R> f) {
        final FTPClient ftp = new FTPClient();
        try {
            ftp.connect(config.getHost(), config.getPort());
            ftp.login(config.getUsername(), config.getPassword());
            //如果支持本地就使用 utf-8
            if (FTPReply.isPositiveCompletion(ftp.sendCommand(
                    "OPTS UTF8", "ON"))) {
                config.setLocalCharset("UTF-8");
            }
            //客户端被动模式
            ftp.enterLocalPassiveMode();
            //文件传输形式
            ftp.setFileType(config.getFileType());
            //编码格式
            ftp.setControlEncoding(config.getLocalCharset());
            return f.apply(ftp);
        } catch (Throwable e) {
            log.error("ftp operation failed: {}", e);
            throw new RuntimeException(e);
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException e) {
                    log.error("Close ftp client failed: {}", e);
                }
            }
        }
    }

    @Override
    public boolean put(InputStream localIs, String remoteFilePath, boolean isCreateDir) {
        if (isCreateDir) {
            mkdirR(getParentDir(remoteFilePath));
        }
        return using(ftp -> {
            try {
                cd(ftp, getParentDir(remoteFilePath));
                return ftp.storeFile(encodingPath(getFileName(remoteFilePath)), localIs);
            } catch (IOException e) {
                log.error("Put file failed: {}", e);
                return false;
            } finally {
                if (localIs != null) {
                    localIs.close();
                }
            }
        });
    }

    @Override
    public boolean get(String remoteFilePath, File localFile, boolean isCreateFile) {
        if (isCreateFile) {
            final boolean mkdirs = localFile.getParentFile().mkdirs();
            log.info("Create file parent dirs successful: {}", mkdirs);
        }
        return using(ftp -> {
            try (final OutputStream os = new FileOutputStream(localFile)) {
                ftp.enterLocalPassiveMode();
                cd(ftp, getParentDir(remoteFilePath));
                return ftp.retrieveFile(encodingPath(getFileName(remoteFilePath)), os);
            } catch (IOException e) {
                log.error("Get file failed: {}", e);
                return false;
            }
        });
    }

    @Override
    public <R> R get(String remoteFilePath, Function<InputStream, R> action) {
        return using(ftp -> {
            cd(ftp, getParentDir(remoteFilePath));
            ftp.enterLocalPassiveMode();
            try (InputStream is = ftp.retrieveFileStream(encodingPath(getFileName(remoteFilePath)))) {
                return action.apply(is);
            } catch (IOException e) {
                log.error("Get file failed: {}", e);
                return null;
            }
        });
    }

    @Override
    public boolean mkdir(String path) {
        return exist(path) || using(ftp -> {
            if (!cd(ftp, getParentDir(path))) {
                return false;
            }
            return ftp.makeDirectory(encodingPath(path));
        });
    }

    @Override
    public boolean rm(String path) {
        return using(ftp -> {
            if (!cd(ftp, getParentDir(path))) {
                return false;
            }
            return ftp.deleteFile(encodingPath(getFileName(path)));
        });
    }

    @Override
    public boolean rmdir(String path) {
        return using(ftp -> ftp.removeDirectory(encodingPath(path)));
    }

    @Override
    public List<Stat> ls(String path) {
        return using(ftp -> {
            cd(ftp, path);
            return Arrays.stream(ftp.listFiles())
                    .map(ftpFile -> new Stat(
                            ftpFile.getName(),
                            PathUtil.join(path, ftpFile.getName()),
                            ftpFile.isDirectory(),
                            ftpFile.getSize()
                    ))
                    .filter(stat -> !stat.getName().equals(PathUtil.CURRENT_DIR) && !stat.getName().equals(PathUtil.PARENT_DIR))
                    .collect(Collectors.toList());
        });
    }

    @Override
    public boolean exist(String path) {
        return using(ftp -> {
            //首先判断是否是目录
            if (cd(ftp, path)) {
                return true;
            }
            //判断是否是文件
            if (!cd(ftp, getParentDir(path))) {
                return false;
            }
            ftp.enterLocalPassiveMode();
            try (final InputStream is = ftp.retrieveFileStream(encodingPath(getFileName(path)))) {
                return is != null || ftp.getReplyCode() == 250;
            }
        });
    }

    /**
     * 辅助函数：改变当前目录
     *
     * @param ftp  ftp 对象，此处需要 {@link FTPClient} 对象是因为改变目录之后还要进行操作，是【连续】的
     * @param path 要改变的目录
     * @return 改变目录是否成功
     */
    private boolean cd(FTPClient ftp, String path) {
        try {
            if (path == null || path.isEmpty()) {
                path = SEPARATOR;
            }
            return ftp.changeWorkingDirectory(encodingPath(path));
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 将本地字符串转换为服务器的字符集格式
     * 主要用于处理发送文件路径时中文乱码
     *
     * @param path 需要处理的路径
     * @return 处理得到的路径
     */
    private String encodingPath(String path) {
        try {
            return new String(path.getBytes(config.getLocalCharset()), config.getServerCharset());
        } catch (UnsupportedEncodingException e) {
            throw new GlobalException("encoding path failed: {}", e);
        }
    }

    /**
     * 将服务器的字符串转换为本地字符集格式
     * 主要用于处理获取文件信息时中文乱码
     *
     * @param path 需要处理的路径
     * @return 处理后得到的路径
     */
    private String decodingPath(String path) {
        try {
            return new String(path.getBytes(config.getServerCharset()), config.getLocalCharset());
        } catch (UnsupportedEncodingException e) {
            throw new GlobalException("decoding path failed: {}", e);
        }
    }
}
