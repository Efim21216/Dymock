package ru.nsu.fit.testclasses;

public class SayHello {
    public String sayHello() {
        return "Hello!";
    }
    public int returnInt() {
        return 1;
    }
    public String testArgs(Integer a) {
        return a + ";";
    }
    public void testVoid(int a) {
        System.out.println(a);
    }
}