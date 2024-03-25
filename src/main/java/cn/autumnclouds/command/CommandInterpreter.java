package cn.autumnclouds.command;

import cn.autumnclouds.annotation.CommandImpl;
import cn.autumnclouds.filesystem.FileSystem;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author Fu Qiujie
 * @since 2024/3/16
 */
public class CommandInterpreter {
    private final Set<Command> commands = new HashSet<>();
    private FileSystem fs;

    public CommandInterpreter(FileSystem fs) {
        new CommandInterpreter(fs, this.getClass().getPackageName());
    }


    public CommandInterpreter(FileSystem fs, String commandsPackage) {
        this.fs = fs;
        ClassUtil.scanPackage(commandsPackage).stream()
                .filter(clazz -> clazz.isAnnotationPresent(CommandImpl.class))
                .forEach(clazz -> {
                    try {
                        registerCommand((Command) clazz.getDeclaredConstructor().newInstance());
                    } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public void registerCommand(Command command) {
        commands.add(command);
    }

    public String interpretAndExecute(String commandString) {
        if (StrUtil.isBlank(commandString)) {
            return "Unknown command: " + commandString;
        }
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
        commands.stream()
                .filter(command -> command.isMatch(commandName))
                .findFirst()
                .ifPresentOrElse(command -> command.execute(fs, options, args.toArray(String[]::new))
                        , () -> System.out.println("Unknown command: " + commandString));
        return "Unknown command: " + commandString;
    }

}
