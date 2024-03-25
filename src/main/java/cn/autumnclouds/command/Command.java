package cn.autumnclouds.command;

import cn.autumnclouds.filesystem.FileSystem;
import cn.autumnclouds.filesystem.impl.SimpleFileSystem;

import java.util.Objects;
import java.util.Set;

/**
 * @author Fu Qiujie
 * @since 2024/3/16
 */
public interface Command {


    default boolean isMatch(String command) {
        return Objects.equals(command, getName());
    }

    void execute(FileSystem fs, Set<Character> options, String... args);

    String getName();
}
