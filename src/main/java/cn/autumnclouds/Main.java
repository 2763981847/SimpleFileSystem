package cn.autumnclouds;

import cn.autumnclouds.command.CommandInterpreter;
import cn.autumnclouds.filesystem.impl.SimpleFileSystem;

import java.util.Scanner;

/**
 * @author Fu Qiujie
 * @since 2024/3/17
 */
public class Main {
    public static void main(String[] args) {
        CommandInterpreter commandInterpreter = new CommandInterpreter(SimpleFileSystem.getInstance(),"cn.autumnclouds.command.impl");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            commandInterpreter.interpretAndExecute(scanner.nextLine());
        }
    }
}
