package top.xinsin.command;

import top.xinsin.annotation.Command;

public class TestCommand {
    @Command(name = "test", description = "A test command")
    public String test(String arg0, String arg1) {
        System.out.println(arg1);
        return "TestCommand executed with arg: " + arg0 + ")" + arg1;
    }
}
