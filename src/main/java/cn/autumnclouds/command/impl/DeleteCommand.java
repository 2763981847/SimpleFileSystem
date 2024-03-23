package cn.autumnclouds.command.impl;

import cn.autumnclouds.annotation.CommandImpl;
import cn.autumnclouds.command.Command;
import cn.autumnclouds.filesystem.FileSystem;

/**
 * @author Fu Qiujie
 * @since 2024/3/16
 */
@CommandImpl
public class DeleteCommand implements Command {
    private static final String NAME = "rm";

    @Override
    public Object execute(FileSystem fs,String options, String... args) {
        fs.deleteDirectory(args[0]);
        return null;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
