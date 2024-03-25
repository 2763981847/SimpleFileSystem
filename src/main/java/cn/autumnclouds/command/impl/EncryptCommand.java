package cn.autumnclouds.command.impl;

import cn.autumnclouds.annotation.CommandImpl;
import cn.autumnclouds.command.Command;
import cn.autumnclouds.filesystem.FileSystem;

import java.util.Set;

/**
 * @author Fu Qiujie
 * @since 2024/3/25
 */
@CommandImpl
public class EncryptCommand implements Command {
    public static final String NAME = "encrypt";

    @Override
    public void execute(FileSystem fs, Set<Character> options, String... args) {
        if (args.length < 3) {
            fs.encryptFile(args[0], args[1]);
        } else fs.encryptFile(args[0], args[1], args[2]);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
