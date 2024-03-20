package ru.nsu.fit.dymock.bytebuddy;

import ru.nsu.fit.dymock.matchers.PartialStick;
import ru.nsu.fit.dymock.matchers.Stick;

import java.util.*;
import java.lang.reflect.Parameter;

public class StaticInterceptionInfo {
    private final Map<String, MethodInterceptionInfo> mapping = new HashMap<>();
    private int classCountCalls = 0;
    private final boolean isSpy;

    public StaticInterceptionInfo(boolean isSpy) {
        this.isSpy = isSpy;
    }

    public boolean isSpy() {
        return isSpy;
    }

    public Map<String, MethodInterceptionInfo> getMapping() {
        return mapping;
    }

    public int getClassCountCalls() {
        return classCountCalls;
    }
    public int getMethodCountCalls(String methodName) {
        MethodInterceptionInfo info = mapping.get(methodName);
        if (info == null)
            throw new IllegalArgumentException("Method not found " + methodName);
        return info.getMethodCallCount();
    }
    public int getLocalCountCalls(Stick stick) {
        MethodInterceptionInfo info = mapping.get(stick.getMethodName());
        if (info == null)
            throw new IllegalArgumentException("Method not found " + stick.getMethodName());
        return info.getLocalCallCount(stick);
    }
    public void incrementClassCountCalls() {
        classCountCalls++;
    }
    public void incrementLocalCountCalls(Stick stick) {
        MethodInterceptionInfo info = mapping.get(stick.getMethodName());
        if (info != null) {
            info.incrementLocalStick(stick);
        }
    }
    public void incrementMethodCountCalls(String methodName) {
        MethodInterceptionInfo info = mapping.get(methodName);
        if(info == null){
            info = new MethodInterceptionInfo(new ArrayList<>(), new ArrayList<>());
            mapping.put(methodName, info);
        }
        info.incrementMethodCallCount();
    }
    public void addStick(Stick stick) {
        MethodInterceptionInfo info = mapping.get(stick.getMethodName());
        if (info == null)
            mapping.put(stick.getMethodName(), new MethodInterceptionInfo(new ArrayList<>(List.of(stick)), new ArrayList<>()));
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
    public Stick getSuitableStick(String methodName, Object[] arguments) {
        MethodInterceptionInfo info = mapping.get(methodName);
        if (info != null) {
            return info.getSuitableStick(arguments);
        }
        return null;
    }    

    public PartialStick getSuitablePartialStick(String methodName, Parameter[] parameters, Object[] arguments) {
        MethodInterceptionInfo info = mapping.get(methodName);
        if (info != null) {
            return info.getSuitablePartialStick(parameters, arguments);
        }
        return null;
    }
}
