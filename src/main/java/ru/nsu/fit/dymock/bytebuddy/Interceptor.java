package ru.nsu.fit.dymock.bytebuddy;

import net.bytebuddy.implementation.bind.annotation.*;
import ru.nsu.fit.dymock.matchers.Stick;

import java.lang.reflect.Method;

import java.lang.reflect.Array;
import java.util.*;

public class Interceptor<T> {
    private final Map<String, MethodInterceptionInfo> mapping = new HashMap<>();
    private int countCalls = 0;

    public final Class<T> mocked;

    public Interceptor(Class<T> mocked) {
        this.mocked = mocked;
    }

    @RuntimeType
    public Object invoke(@Origin Method invokedMethod,
                         @AllArguments Object[] arguments) {
        countCalls++;
        String name = invokedMethod.getName();
        MethodInterceptionInfo interceptionInfo = mapping.get(name);
        if (interceptionInfo != null) {
            interceptionInfo.incrementMethodCallCount();
            Stick stick = mapping.get(name).getSuitableStick(arguments);
            if (stick != null) {
                interceptionInfo.incrementLocalStick(stick);
                return stick.getResult();
            }
        }
        var returnType = invokedMethod.getReturnType();
        if(!returnType.equals(Void.TYPE)){
            return getDefaultValue(returnType);
        }
        return null;
    }

    public int getCountCalls() {
        return countCalls;
    }

    private static <T> T getDefaultValue(Class<T> clazz) {
        return (T) Array.get(Array.newInstance(clazz, 1), 0);
    }
 
    public void addStick(Stick stick) {
        String name = stick.getMethodName();
        MethodInterceptionInfo info = mapping.get(name);
        if (info == null)
            mapping.put(name, new MethodInterceptionInfo(new ArrayList<>(List.of(stick))));
        else
            info.addStick(stick);
    }
    public int getLocalCountCalls(Stick stick) {
        return mapping.get(stick.getMethodName()).getLocalCallCount(stick);
    }
    public int getMethodCountCalls(String methodName) {
        return mapping.get(methodName).getMethodCallCount();
    }
}
