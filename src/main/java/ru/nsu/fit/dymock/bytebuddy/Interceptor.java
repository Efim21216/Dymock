package ru.nsu.fit.dymock.bytebuddy;

import net.bytebuddy.implementation.bind.annotation.*;
import ru.nsu.fit.dymock.matchers.PartialStick;
import ru.nsu.fit.dymock.matchers.Stick;
import ru.nsu.fit.dymock.matchers.WetStick;

import java.lang.reflect.Method;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.Callable;

public class Interceptor<T> {
    private final Map<String, MethodInterceptionInfo> mapping = new HashMap<>();
    private int countCalls = 0;

    public final Class<T> mocked;
    public final boolean isSpy;

    public Interceptor(Class<T> mocked, boolean isSpy) {
        this.mocked = mocked;
        this.isSpy = isSpy;
    }
    //for interfaces
    @BindingPriority(1)
    @RuntimeType
    public Object invoke(@Origin Method invokedMethod,
                         @AllArguments Object[] arguments) throws Throwable {
        return intercept(invokedMethod, null, arguments);
    }
    @BindingPriority(2)
    @RuntimeType
    public Object invoke(@Origin Method invokedMethod, @SuperCall Callable<?> originalCall,
                         @AllArguments Object[] arguments) throws Throwable {
        return intercept(invokedMethod, originalCall, arguments);
    }
    public Object intercept(Method invokedMethod, Callable<?> originalCall,
                            Object[] arguments) throws Throwable {
        countCalls++;
        String name = invokedMethod.getName();
        MethodInterceptionInfo interceptionInfo = mapping.get(name);
        if(interceptionInfo == null){
            var info = new MethodInterceptionInfo(new ArrayList<>(), new ArrayList<>());
            info.incrementMethodCallCount();
            mapping.put(name, info);
        }
        else {
            interceptionInfo.incrementMethodCallCount();
            Stick stick = mapping.get(name).getSuitableStick(arguments);
            if (stick != null) {
                interceptionInfo.incrementLocalStick(stick);
                if (stick instanceof WetStick) {
                    throw ((WetStick) stick).getResult();
                }
                return stick.getResult();
            }
            PartialStick partialStick = mapping.get(name).getSuitablePartialStick(invokedMethod.getParameters(), arguments);
            if (partialStick != null) {
                interceptionInfo.incrementLocalStick(partialStick);
                return partialStick.getResult();
            }
        }
        if (originalCall != null && isSpy)
            return originalCall.call();
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
            mapping.put(name, new MethodInterceptionInfo(new ArrayList<>(List.of(stick)), new ArrayList<>()));
        else
            info.addStick(stick);
    }
    public void addPartialStick(PartialStick stick) {
        String name = stick.getMethodName();
        MethodInterceptionInfo info = mapping.get(name);
        if (info == null)
            mapping.put(name, new MethodInterceptionInfo(new ArrayList<>(), new ArrayList<>(List.of(stick))));
        else
            info.addPartialStick(stick);
    }
    public int getLocalCountCalls(Stick stick) {
        var info = mapping.get(stick.getMethodName());
        if(info == null){
            return 0;
        }
        return info.getLocalCallCount(stick);
    }
    public int getMethodCountCalls(String methodName) {
        var info = mapping.get(methodName);
        if(info == null){
            return 0;
        }
        return mapping.get(methodName).getMethodCallCount();
    }
}
