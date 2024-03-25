package cn.autumnclouds.command.impl;

import cn.autumnclouds.annotation.CommandImpl;
import cn.autumnclouds.command.Command;
import cn.autumnclouds.filesystem.FileSystem;

import java.util.Set;

/**
 * @author Fu Qiujie
 * @since 2024/3/16
 */
@CommandImpl
public class CreateDirCommand implements Command {

    private static final String NAME = "mkdir";

    @Override
    public void execute(FileSystem fs, Set<Character> options, String... args) {
         fs.createDirectory(args[0]);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
