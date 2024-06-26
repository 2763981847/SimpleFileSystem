package cn.autumnclouds.command.impl;

import cn.autumnclouds.annotation.CommandImpl;
import cn.autumnclouds.command.Command;
import cn.autumnclouds.filesystem.FileSystem;

import java.util.Set;

/**
 * @author Fu Qiujie
 * @since 2024/3/23
 */
@CommandImpl
public class TouchCommand implements Command {
    public static final String NAME = "touch";

    @Override
    public void execute(FileSystem fs, Set<Character> options, String... args) {
        fs.createFile(args[0]);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
