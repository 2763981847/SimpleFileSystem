package cn.autumnclouds.command.impl;

import cn.autumnclouds.annotation.CommandImpl;
import cn.autumnclouds.command.Command;
import cn.autumnclouds.filesystem.FileSystem;

import java.io.IOException;

/**
 * @author Fu Qiujie
 * @since 2024/3/16
 */
@CommandImpl
public class CopyCommand implements Command {
    private static final String NAME = "cp";

    @Override
    public Object execute(FileSystem fs,String options, String... args) {
        return fs.copy(args[0], args[1]) ? "Copy success" : "Copy failed";
    }

    @Override
    public String getName() {
        return NAME;
    }
}
