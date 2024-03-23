package cn.autumnclouds.command.impl;

import cn.autumnclouds.annotation.CommandImpl;
import cn.autumnclouds.command.Command;
import cn.autumnclouds.filesystem.FileSystem;
import cn.hutool.core.util.StrUtil;

/**
 * @author Fu Qiujie
 * @since 2024/3/16
 */
@CommandImpl
public class ChangeDirCommand implements Command {
    private static final String NAME = "cd";

    @Override
    public Object execute(FileSystem fs,String options, String... args) {
        boolean success = fs.changeDirectory(args[0]);
        return success ? null : "Directory not found";
    }

    @Override
    public String getName() {
        return NAME;
    }
}
