package ru.nsu.fit.testclasses;

import java.util.Arrays;

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
    public String testArgs(Double a) {
        return a + ";";
    }
    public void testVoid(int a) {
        System.out.println(a);
    }
    public int vararg(int a, int... b) {
        return a + Arrays.stream(b).sum();
    }
}
