package ru.nsu.fit.demo;

public class CallCounting {
    public int echoInt() {
        return 42;
    }
    public int echoInt(int a) {
        return a;
    }
    public int echoInt(double a) {
        return (int) a;
    }
    public int echoInt(int a, int b) {
        return a + b;
    }
    public void doSomething() {
    }
}
