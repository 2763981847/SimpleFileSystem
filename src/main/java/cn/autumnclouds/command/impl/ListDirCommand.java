package cn.autumnclouds.command.impl;

import cn.autumnclouds.annotation.CommandImpl;
import cn.autumnclouds.command.Command;
import cn.autumnclouds.entity.vo.FileVo;
import cn.autumnclouds.filesystem.FileSystem;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @author Fu Qiujie
 * @since 2024/3/16
 */
@CommandImpl
public class ListDirCommand implements Command {
    private static final String NAME = "ls";

    @Override
    public Object execute(FileSystem fs, String options, String... args) {
        File[] files = fs.listContents();
        FileVo[] fileVos = Arrays.stream(files).map(FileVo::new).sorted(getComparator(options)).toArray(FileVo[]::new);
        for (FileVo fileVo : fileVos) {
            System.out.println(fileVo);
        }
        return fileVos;
    }

    @Override
    public String getName() {
        return NAME;
    }

    private Comparator<FileVo> getComparator(String options) {
        Comparator<FileVo> comparator = Comparator.comparing(FileVo::getName);
        if (options == null) {
            return comparator;
        }
        if (options.contains("n")) {
            comparator = Comparator.comparing(FileVo::getName);
        } else if (options.contains("t")) {
            comparator = Comparator.comparing(FileVo::getLastModifiedTime);
        } else if (options.contains("s")) {
            comparator = Comparator.comparing(FileVo::getSize);
        }
        if (options.contains("r")) {
            comparator = comparator.reversed();
        }
        return comparator;
    }
}
