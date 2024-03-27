package cn.autumnclouds.command;

import cn.autumnclouds.annotation.CommandImpl;
import cn.autumnclouds.filesystem.FileSystem;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 命令解释器类，用于解释并执行用户输入的命令。
 * 命令解释器根据命令名称调用对应的命令对象执行相应的操作。
 * 命令对象必须实现 Command 接口。
 * 命令解释器可以注册和管理多个命令对象。
 * 支持解析命令中的选项和参数。
 *
 * @author Fu Qiujie
 * @since 2024/3/16
 */
public class CommandInterpreter {
    // 存储注册的命令对象集合
    private final Set<Command> commands = new HashSet<>();
    private FileSystem fs;

    /**
     * 构造函数，创建命令解释器对象并关联指定的文件系统。
     *
     * @param fs 文件系统对象
     */
    public CommandInterpreter(FileSystem fs) {
        // 调用另一个构造函数，指定命令包的默认路径
        new CommandInterpreter(fs, this.getClass().getPackageName());
    }

    /**
     * 构造函数，创建命令解释器对象并关联指定的文件系统，并从指定的包中扫描并注册命令。
     *
     * @param fs              文件系统对象
     * @param commandsPackage 命令包的路径
     */
    public CommandInterpreter(FileSystem fs, String commandsPackage) {
        this.fs = fs;
        // 扫描指定包下的类，并注册带有 CommandImpl 注解的命令类
        ClassUtil.scanPackage(commandsPackage).stream()
                .filter(clazz -> clazz.isAnnotationPresent(CommandImpl.class))
                .forEach(clazz -> {
                    try {
                        // 实例化命令对象，并注册到命令解释器中
                        registerCommand((Command) clazz.getDeclaredConstructor().newInstance());
                    } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    /**
     * 注册命令对象到命令解释器中。
     *
     * @param command 要注册的命令对象
     */
    public void registerCommand(Command command) {
        commands.add(command);
    }

    /**
     * 解释并执行用户输入的命令。
     *
     * @param commandString 用户输入的命令字符串
     */
    public void interpretAndExecute(String commandString) {
        // 如果命令字符串为空，则返回未知命令消息
        if (StrUtil.isBlank(commandString)) {
            System.out.println("Unknown command: " + commandString);
            return;
        }
        // 解析命令字符串，获取命令名称、选项和参数
        String[] tokens = commandString.split(" ");
        String commandName = tokens[0];
        Set<Character> options = new HashSet<>();
        List<String> args = new ArrayList<>();
        for (int i = 1; i < tokens.length; i++) {
            if (tokens[i].startsWith("-")) {
                for (int j = 1; j < tokens[i].length(); j++) {
                    options.add(tokens[i].charAt(j));
                }
            } else {
                args.add(tokens[i]);
            }
        }

        // 查找匹配的命令对象，并执行对应的命令
        commands.stream()
                .filter(command -> command.isMatch(commandName))
                .findFirst()
                .ifPresentOrElse(command -> command.execute(fs, options, args.toArray(String[]::new))
                        , () -> System.out.println("Unknown command: " + commandString));
    }
}