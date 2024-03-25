package cn.autumnclouds.command;

import cn.autumnclouds.filesystem.FileSystem;
import cn.autumnclouds.filesystem.impl.SimpleFileSystem;

import java.util.Objects;
import java.util.Set;

/**
 * 命令接口，定义了命令对象的基本行为。
 * 所有的命令对象都必须实现该接口。
 * 命令对象通过名称识别命令，并执行相应的操作。
 * 命令对象可以接收选项和参数，并根据需要执行相应的操作。
 *
 * @author Fu Qiujie
 * @since 2024/3/16
 */
public interface Command {

    /**
     * 检查给定的命令字符串是否与当前命令对象的名称匹配。
     *
     * @param command 待匹配的命令字符串
     * @return 如果匹配返回 true，否则返回 false
     */
    default boolean isMatch(String command) {
        return Objects.equals(command, getName());
    }

    /**
     * 执行命令操作。
     *
     * @param fs      文件系统对象，命令操作可能需要对文件系统进行操作
     * @param options 命令的选项，用于指定命令的特定行为或参数
     * @param args    命令的参数，用于传递命令执行所需的参数值
     */
    void execute(FileSystem fs, Set<Character> options, String... args);

    /**
     * 获取命令对象的名称。
     *
     * @return 命令对象的名称
     */
    String getName();
}