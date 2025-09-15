package top.xinsin.parser;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import top.xinsin.annotation.Command;
import top.xinsin.entity.CommandEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TerminalControlApplication {
    private static List<String> COMMANDS = null;
    private static List<CommandEntity> COMMANDSMAPPING = null;
    public static void startTerminalControl(List<CommandEntity> commandEntities) {
        parseActuator(commandEntities);
        try {
            // 1. 初始化终端（Windows需Jansi支持）
            Terminal terminal = TerminalBuilder.builder()
                    .jansi(true) // 启用色彩
                    .build();

            // 2. 初始化阅读器（带命令补全和历史记录）
            LineReader reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .parser(new DefaultParser())
                    // 命令补全：按Tab补全COMMANDS中的命令
                    .completer(new StringsCompleter(COMMANDS))
                    .build();

            // 3. 自定义提示符（绿色的 "XMTL>"）
            String prompt = new AttributedStringBuilder()
                    .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.MAGENTA))
                    .append("XMTL> ")
                    .toAnsi();

            // 4. 交互循环
            String line;
            while (true) {
                try {
                    line = reader.readLine(prompt);
                    if (line == null || "exit".equals(line.trim())) {
                        break;
                    }
                    handleCommand(line.trim());
                } catch (UserInterruptException e) {
                    break;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static void parseActuator(List<CommandEntity> commandEntities) {
        //        获取顶级命令提示器
        COMMANDS = commandEntities.stream().filter(item -> item.getChildren() == null).map(CommandEntity::getName).collect(Collectors.toList());
        COMMANDSMAPPING = new ArrayList<>(commandEntities);
    }
    @Command(name = "help", description = "显示帮助信息", args = 0)
    public void help() {
        System.out.println("帮助信息: ");
        for (CommandEntity commandEntity : COMMANDSMAPPING) {
            System.out.printf(" - %s: %s%n", commandEntity.getName(), commandEntity.getDescription());
        }
    }
    @Command(name = "exit", description = "退出程序", args = 0)
    public void exit() {
        System.exit(0);
    }
    private static void handleCommand(String line) {
        boolean flag = false;
        String[] s = line.split(" ");
        for (CommandEntity commandEntity : COMMANDSMAPPING) {
            if (s[0].equals(commandEntity.getName())) {
                try {
                    Object o = commandEntity.getClazz().getDeclaredConstructor().newInstance();
                    Command annotation = commandEntity.getMethod().getAnnotation(Command.class);
                    if (annotation.args() == 0) {
                        commandEntity.getMethod().invoke(o);
                    } else {
                        commandEntity.getMethod().invoke(o, line, line.substring(commandEntity.getName().lastIndexOf(s[0])));
                    }
                    flag = true;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (!flag) {
            System.out.println("未知命令: " + line + ", 输入 help 查看帮助信息");
        }
    }
}
