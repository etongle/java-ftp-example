package com.rxliuli.example.ftpdemo.common.ftp.sftp;

import com.jcraft.jsch.*;
import com.rxliuli.example.ftpdemo.common.ftp.FtpOperator;
import com.rxliuli.example.ftpdemo.common.ftp.Stat;
import com.rxliuli.example.ftpdemo.common.util.AllowErrorFunction;
import com.rxliuli.example.ftpdemo.common.util.PathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.rxliuli.example.ftpdemo.common.util.PathUtil.getParentDir;


/**
 * sftp 操作的具体实现
 *
 * @author rxliuli
 */
public class SftpOperator implements FtpOperator {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final SftpClientConfig sftpClientConfig;

    public SftpOperator(SftpClientConfig sftpClientConfig) {
        this.sftpClientConfig = sftpClientConfig;
    }

    /**
     * MybatisUtil 中 usingOptional() 方法再封装而已
     *
     * @param f   执行方法
     * @param <R> 执行方法
     * @return 方法的返回值
     */
    private <R> R using(AllowErrorFunction<ChannelSftp, R> f) {
        final JSch jSch = new JSch();
        ChannelSftp sftp = null;
        Session session = null;
        try {
            session = jSch.getSession(sftpClientConfig.getUsername(), sftpClientConfig.getHost(), sftpClientConfig.getPort());
            session.setPassword(sftpClientConfig.getPassword());
            final Properties properties = new Properties();
            properties.put("StrictHostKeyChecking", sftpClientConfig.getStrictHostKeyChecking());
            session.setConfig(properties);
            session.connect();
            log.info("JSch open ssh session successful: {}", session);

            sftp = (ChannelSftp) session.openChannel("sftp");
            sftp.connect();

            //使用反射强制修改编码格式
            Class cl = ChannelSftp.class;
            Field f1 = cl.getDeclaredField("server_version");
            f1.setAccessible(true);
            f1.set(sftp, 2);
            sftp.setFilenameEncoding(sftpClientConfig.getEncoding());

            log.info("Sftp connection successful: {}", sftp);

            return f.apply(sftp);
        } catch (Throwable e) {
            log.error("sftp operation failed: {}", e);
            throw new RuntimeException(e);
        } finally {
            if (sftp != null && sftp.isConnected()) {
                sftp.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }

    @Override
    public List<Stat> ls(String path) {
        //noinspection unchecked
        final Vector<ChannelSftp.LsEntry> vector = using(sftp -> sftp.ls(path));
        return vector.stream()
                .map(lsEntry ->
                        new Stat(
                                lsEntry.getFilename(),
                                PathUtil.join(path, lsEntry.getFilename()),
                                lsEntry.getAttrs().isDir(),
                                lsEntry.getAttrs().getSize()
                        )
                )
                .filter(stat -> !stat.getName().equals(PathUtil.CURRENT_DIR) && !stat.getName().equals(PathUtil.PARENT_DIR))
                .collect(Collectors.toList());
    }

    @Override
    public boolean get(String remoteFilePath, File localFile, boolean isCreateFile) {
        if (isCreateFile) {
            final boolean mkdirs = localFile.getParentFile().mkdirs();
            log.info("create file successful: {}", mkdirs);
        }
        return using(sftp -> {
            try (final OutputStream os = new FileOutputStream(localFile)) {
                sftp.get(remoteFilePath, os);
                return true;
            } catch (SftpException e) {
                log.error("sftp get operation failed: {}", e);
                return false;
            }
        });
    }

    @Override
    public <R> R get(String remoteFilePath, Function<InputStream, R> action) {
        return using(sftp -> {
            try (InputStream is = sftp.get(remoteFilePath)) {
                return action.apply(is);
            } catch (SftpException e) {
                log.error("sftp get operation failed: {}", e);
                return null;
            }
        });
    }

    @Override
    public boolean put(InputStream localIs, String remoteFilePath, boolean isCreateDir) {
        if (isCreateDir) {
            mkdirR(getParentDir(remoteFilePath));
        }
        return using(sftp -> {
            try {
                sftp.put(localIs, remoteFilePath);
                return true;
            } catch (SftpException e) {
                log.error("sftp put operation failed: {}", e);
                return false;
            } finally {
                if (localIs != null) {
                    localIs.close();
                }
            }
        });
    }

    @Override
    public boolean rm(String path) {
        return using(sftp -> {
            try {
                sftp.rm(path);
                return true;
            } catch (SftpException e) {
                log.error("sftp rm operation failed: {}", e);
                return false;
            }
        });
    }

    @Override
    public boolean rmdir(String path) {
        return using(sftp -> {
            try {
                sftp.rmdir(path);
                return true;
            } catch (SftpException e) {
                log.error("sftp rmdir operation failed: {}", e);
                return false;
            }
        });
    }

    @Override
    public boolean mkdir(String path) {
        return using(sftp -> {
            try {
                sftp.mkdir(path);
                return true;
            } catch (SftpException e) {
                log.error("sftp mkdir operation failed: {}", e);
                return false;
            }
        });
    }

    @Override
    public boolean exist(String path) {
        return using(sftp -> {
            final SftpATTRS stat;
            try {
                stat = sftp.lstat(path);
            } catch (SftpException e) {
                log.info("sftp exist operation failed: {}", e.getMessage());
                return false;
            }
            return stat.isDir() || stat.getSize() >= 0;
        });
    }
}
