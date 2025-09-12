package top.xinsin;

import lombok.SneakyThrows;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.jline.builtins.Completers;
import org.jline.reader.*;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class JLineDemo {
    // 支持的命令列表（用于补全）
    private static final List<String> COMMANDS = Arrays.asList("login", "logout", "help", "exit");

    public static void main(String[] args) {
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

            // 3. 自定义提示符（绿色的 "demo>"）
            String prompt = new AttributedStringBuilder()
                    .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN))
                    .append("demo> ")
                    .toAnsi();

            // 4. 交互循环
            String line;
            while (true) {
                try {
                    // 读取用户输入（支持Tab补全、↑/↓历史）
                    line = reader.readLine(prompt);
                    if (line == null || "exit".equals(line.trim())) {
                        break; // 退出
                    }

                    // 处理命令
                    handleCommand(line.trim());
                } catch (UserInterruptException e) {
                    // 处理 Ctrl+C（不退出，仅提示）
                    System.out.println("\n请输入 exit 退出程序");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            System.out.println("程序退出");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 命令处理逻辑
    private static void handleCommand(String command) throws InterruptedException {
        switch (command) {
            case "login":
                Thread.getAllStackTraces().forEach((key, value) -> {
                    System.out.println(key.getName());
                });
                System.out.println("执行登录逻辑...");
                break;
            case "logout":
                // 1. 创建进度条（总进度100，样式为方块，提示文本“下载中”）
                ProgressBar progressBar = new ProgressBarBuilder()
                        .setTaskName("下载中") // 任务名称
                        .setInitialMax(100) // 总进度
                        .setStyle(ProgressBarStyle.COLORFUL_UNICODE_BAR) // 样式：方块填充
                        .build();

                // 2. 模拟进度更新（每50ms更新1%）
                for (int i = 0; i < 100; i++) {
                    progressBar.step(); // 进度+1
                    TimeUnit.MILLISECONDS.sleep(50);
                }

                // 3. 关闭进度条（自动输出“完成”）
                progressBar.close();
                System.out.println("文件下载完成！");
                System.out.println("执行登出逻辑...");
                break;
            case "help":
                System.out.println("支持命令：login、logout、help、exit");
                break;
            default:
                System.out.printf("未知命令：%s，请输入 help 查看支持的命令%n", command);
        }
    }
}
