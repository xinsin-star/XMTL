package top.xinsin;

import top.xinsin.parser.XMTLApplication;

public class Main {
    public static void main(String[] args) {
//        System.out.println(System.getenv("MICROSOFT_CLIENT_ID"));
//        System.out.println(System.getenv("MICROSOFT_CLIENT_SECRET"));
//        System.out.println(System.getenv("CURSEFORGE_API_KEY"));
//        Thread.getAllStackTraces().forEach((key, value) -> {
//            System.out.println(key.getName());
//        });
        XMTLApplication.run(Main.class, args);
    }
}
