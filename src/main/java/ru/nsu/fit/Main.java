package ru.nsu.fit;


import ru.nsu.fit.testclasses.SayHello;

import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ChangeClass changer = new ChangeClass();
        System.out.println(changer.change(SayHello.class, "sayHello").sayHello());
    }

}