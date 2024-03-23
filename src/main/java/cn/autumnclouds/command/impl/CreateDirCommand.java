package cn.autumnclouds.command.impl;

import cn.autumnclouds.annotation.CommandImpl;
import cn.autumnclouds.command.Command;
import cn.autumnclouds.filesystem.FileSystem;

/**
 * @author Fu Qiujie
 * @since 2024/3/16
 */
@CommandImpl
public class CreateDirCommand implements Command {

    private static final String NAME = "mkdir";

    @Override
    public Object execute(FileSystem fs,String options, String... args) {
        return fs.createDirectory(args[0]) ? null : "Directory already exists";
    }

    @Override
    public String getName() {
        return NAME;
    }
}
