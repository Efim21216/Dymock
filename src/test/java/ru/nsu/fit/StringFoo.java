package ru.nsu.fit;

public class StringFoo {
    public int echoInt(String s){
        return Integer.parseInt(s);
    }
    public int echoInt(int a, String s){
        return a + Integer.parseInt(s);
    }
}
