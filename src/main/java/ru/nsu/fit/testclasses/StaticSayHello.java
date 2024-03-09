package ru.nsu.fit.testclasses;

public class StaticSayHello {
    public static int sayHello() {
        System.out.println("IN HELLO");
        return 1;
    }
    public static String m() {
        return "Hello!";
    }
    public static String testArgs(int a) {
        return a + ";";
    }
    public static String testArgs(double a) {
        return a + ";";
    }
}
