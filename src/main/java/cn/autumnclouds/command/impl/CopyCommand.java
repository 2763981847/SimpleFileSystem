package cn.autumnclouds.command.impl;

import cn.autumnclouds.annotation.CommandImpl;
import cn.autumnclouds.command.Command;
import cn.autumnclouds.filesystem.FileSystem;

import java.io.IOException;
import java.util.Set;

/**
 * @author Fu Qiujie
 * @since 2024/3/16
 */
@CommandImpl
public class CopyCommand implements Command {
    private static final String NAME = "cp";

    @Override
    public void execute(FileSystem fs, Set<Character> options, String... args) {
        boolean runInBackground = options.contains('d');
        fs.copy(args[0], args[1], runInBackground);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
