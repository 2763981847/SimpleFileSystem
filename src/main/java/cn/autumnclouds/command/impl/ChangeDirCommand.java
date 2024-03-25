package cn.autumnclouds.command.impl;

import cn.autumnclouds.annotation.CommandImpl;
import cn.autumnclouds.command.Command;
import cn.autumnclouds.filesystem.FileSystem;
import cn.hutool.core.util.StrUtil;

import java.util.Set;

/**
 * @author Fu Qiujie
 * @since 2024/3/16
 */
@CommandImpl
public class ChangeDirCommand implements Command {
    private static final String NAME = "cd";

    @Override
    public void execute(FileSystem fs, Set<Character> options, String... args) {
        fs.changeDirectory(args[0]);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
