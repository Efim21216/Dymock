package ru.nsu.fit.dymock.bytebuddy;

import net.bytebuddy.implementation.bind.annotation.*;
import ru.nsu.fit.dymock.matchers.Stick;

import java.lang.reflect.Method;
import java.util.*;

import java.lang.reflect.Array;

public class Interceptor<T> {
    private final Map<Method, MethodInterceptionInfo> mapping = new HashMap<>();
    private int countCalls = 0;

    public final Class<T> mocked;

    public Interceptor(Class<T> mocked) {
        this.mocked = mocked;
    }

    @RuntimeType
    public Object invoke(@Origin Method invokedMethod,
                         @AllArguments Object[] arguments) {
        countCalls++;
        MethodInterceptionInfo interceptionInfo = mapping.get(invokedMethod);
        if (interceptionInfo == null) {
            MethodInterceptionInfo info = new MethodInterceptionInfo(new LinkedList<>());
            info.incrementCountCalls();
            mapping.put(invokedMethod, info);
        } else {
            interceptionInfo.incrementCountCalls();
            Stick stick = mapping.get(invokedMethod).getSuitableStick(arguments);
            if (stick != null) {
                stick.incrementCountCalls();
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
        Method method = stick.getMethod(mocked);
        MethodInterceptionInfo info = mapping.get(method);
        if (info == null)
            mapping.put(method, new MethodInterceptionInfo(new LinkedList<>()));
        else
            info.getSticks().add(stick);
    }
    public int getCountCallsMethod(Method method) {
        return mapping.get(method).getCountCalls();
    }
}
