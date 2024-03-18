package ru.nsu.fit.dymock.bytebuddy;

import ru.nsu.fit.dymock.matchers.Stick;

import java.util.*;

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
        if (info != null) {
            info.incrementMethodCallCount();
        }
    }
    public void addStick(Stick stick) {
        MethodInterceptionInfo info = mapping.get(stick.getMethodName());
        if (info == null)
            mapping.put(stick.getMethodName(), new MethodInterceptionInfo(new ArrayList<>(List.of(stick))));
        else
            info.addStick(stick);
    }
    public Stick getSuitableStick(String methodName, Object[] arguments) {
        MethodInterceptionInfo info = mapping.get(methodName);
        if (info != null) {
            return info.getSuitableStick(arguments);
        }
        return null;
    }
}
