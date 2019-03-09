package com.rxliuli.example.ftpdemo.common.ftp;

import java.util.Objects;

/**
 * 统一的文件信息
 *
 * @author rxliuli
 */
public class Stat {
    /**
     * 文件名
     */
    private String name;
    /**
     * 路径
     */
    private String path;
    /**
     * 是否是文件
     */
    private Boolean isDir;
    /**
     * 文件大小，如果是文件夹则默认为 0
     */
    private Long size;

    public Stat() {
    }

    public Stat(String name, String path, Boolean isDir, Long size) {
        this.name = name;
        this.path = path;
        this.isDir = isDir;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public Stat setName(String name) {
        this.name = name;
        return this;
    }

    public String getPath() {
        return path;
    }

    public Stat setPath(String path) {
        this.path = path;
        return this;
    }

    public Boolean getIsDir() {
        return isDir;
    }

    public Stat setIsDir(Boolean dir) {
        isDir = dir;
        return this;
    }

    public Long getSize() {
        return size;
    }

    public Stat setSize(Long size) {
        this.size = size;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Stat)) {
            return false;
        }
        Stat stat = (Stat) o;
        return Objects.equals(getName(), stat.getName()) &&
                Objects.equals(getPath(), stat.getPath()) &&
                Objects.equals(getIsDir(), stat.getIsDir()) &&
                Objects.equals(getSize(), stat.getSize());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getPath(), getIsDir(), getSize());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Stat{");
        sb.append("name='").append(name).append('\'');
        sb.append(", path='").append(path).append('\'');
        sb.append(", isDir=").append(isDir);
        sb.append(", size=").append(size);
        sb.append('}');
        return sb.toString();
    }
}
