package cn.autumnclouds.command.impl;

import cn.autumnclouds.annotation.CommandImpl;
import cn.autumnclouds.command.Command;
import cn.autumnclouds.filesystem.FileSystem;

/**
 * @author Fu Qiujie
 * @since 2024/3/23
 */
@CommandImpl
public class CatCommand implements Command {
    public static final String NAME = "cat";

    @Override
    public Object execute(FileSystem fs,String options, String... args) {
        fs.cat(args[0]);
        return null;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
