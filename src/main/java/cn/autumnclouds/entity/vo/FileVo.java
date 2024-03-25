package cn.autumnclouds.entity.vo;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

/**
 * 文件视图对象类，用于展示文件的基本信息。
 *
 * @author Fu Qiujie
 * @since 2024/3/23
 */
public class FileVo {

    // 时间格式化器，用于将文件最后修改时间格式化为指定格式
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String name; // 文件名或目录名
    private String type; // 文件类型，"dir" 表示目录，"file" 表示文件
    private Long size;   // 文件大小，单位为字节
    private LocalDateTime lastModifiedTime; // 文件最后修改时间

    /**
     * 构造函数，根据给定的属性值创建 FileVo 对象。
     *
     * @param name             文件名或目录名
     * @param type             文件类型，"dir" 表示目录，"file" 表示文件
     * @param size             文件大小，单位为字节
     * @param lastModifiedTime 文件最后修改时间
     */
    public FileVo(String name, String type, Long size, LocalDateTime lastModifiedTime) {
        this.name = name;
        this.type = type;
        this.size = size;
        this.lastModifiedTime = lastModifiedTime;
    }

    /**
     * 构造函数，根据给定的 File 对象创建 FileVo 对象。
     *
     * @param file 要表示的文件对象
     */
    public FileVo(File file) {
        this.name = file.getName();
        this.type = file.isDirectory() ? "dir" : "file";
        this.size = file.isDirectory() ? 0 : file.length();
        this.lastModifiedTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneId.systemDefault());
    }

    /**
     * 获取文件名或目录名。
     *
     * @return 文件名或目录名
     */
    public String getName() {
        return name;
    }

    /**
     * 获取文件类型。
     *
     * @return 文件类型，"dir" 表示目录，"file" 表示文件
     */
    public String getType() {
        return type;
    }

    /**
     * 获取文件大小。
     *
     * @return 文件大小，单位为字节
     */
    public Long getSize() {
        return size;
    }

    /**
     * 获取文件最后修改时间。
     *
     * @return 文件最后修改时间
     */
    public LocalDateTime getLastModifiedTime() {
        return lastModifiedTime;
    }

    /**
     * 将 FileVo 对象转换为字符串表示形式。
     *
     * @return FileVo 对象的字符串表示形式
     */
    @Override
    public String toString() {
        return name + "  " + type + "  " + size + "B  " + lastModifiedTime.format(FORMATTER);
    }
}