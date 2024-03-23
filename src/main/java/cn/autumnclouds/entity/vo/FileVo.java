package cn.autumnclouds.entity.vo;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author Fu Qiujie
 * @since 2024/3/23
 */
public class FileVo {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private String name;
    private String type;
    private Long size;
    private LocalDateTime lastModifiedTime;

    public FileVo(String name, String type, Long size, LocalDateTime lastModifiedTime) {
        this.name = name;
        this.type = type;
        this.size = size;
        this.lastModifiedTime = lastModifiedTime;
    }

    public FileVo(File file) {
        this.name = file.getName();
        this.type = file.isDirectory() ? "dir" : "file";
        this.size = file.isDirectory() ? 0 : file.length();
        this.lastModifiedTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneId.systemDefault());
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Long getSize() {
        return size;
    }

    public LocalDateTime getLastModifiedTime() {
        return lastModifiedTime;
    }

    @Override
    public String toString() {
        return name + "  " + type + "  " + size + "B  " + lastModifiedTime.format(FORMATTER);
    }
}
