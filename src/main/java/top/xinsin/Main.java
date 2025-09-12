package top.xinsin;

import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        System.out.println(System.getenv("MICROSOFT_CLIENT_ID"));
        System.out.println(System.getenv("MICROSOFT_CLIENT_SECRET"));
        System.out.println(System.getenv("CURSEFORGE_API_KEY"));
        Thread.getAllStackTraces().forEach((key, value) -> {
            System.out.println(key.getName());
        });
    }
}
