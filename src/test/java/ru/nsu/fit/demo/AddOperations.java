package ru.nsu.fit.demo;
import java.math.BigInteger;

public class AddOperations {
    static int add(int a, int b){
        return a + b;
    }
    static double add(double a, double b){
        return a + b;
    }
    static BigInteger add(BigInteger a, BigInteger b){
        return a.add(b);
    }
}
