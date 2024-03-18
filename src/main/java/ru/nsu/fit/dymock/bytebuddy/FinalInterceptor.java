package ru.nsu.fit.dymock.bytebuddy;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import ru.nsu.fit.dymock.matchers.Stick;
import ru.nsu.fit.dymock.matchers.WetStick;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class FinalInterceptor {
    private static final Map<Object, StaticInterceptionInfo> mockMap = new HashMap<>();
    @Advice.OnMethodEnter(skipOn = Advice.OnDefaultValue.class)
    public static Object onMethodBegin(
            @Advice.Origin Method method,
            @Advice.This Object invokedObject,
            @Advice.AllArguments Object[] arguments,
            @Advice.Local("result") Object localVariable
    ) throws Throwable {
        String name = method.getName();
        StaticInterceptionInfo interceptionInfo = FinalInterceptor.getObjectRules(invokedObject);
        if (interceptionInfo == null)
            return "default call";
        interceptionInfo.incrementClassCountCalls();
        interceptionInfo.incrementMethodCountCalls(name);
        Stick stick = interceptionInfo.getSuitableStick(name, arguments);
        if (stick != null) {
            interceptionInfo.incrementLocalCountCalls(stick);
            if (stick instanceof WetStick) {
                throw ((WetStick) stick).getResult();
            }
            localVariable = stick.getResult();
            return null;
        }
        localVariable = null;
        if (interceptionInfo.isSpy())
            return "default call";
        else
            return null;
    }

    @Advice.OnMethodExit
    public static Object onMethodEnd(@Advice.Return(readOnly = false, typing = Assigner.Typing.DYNAMIC) Object value,
                                     @Advice.Origin Method method,
                                     @Advice.This Object invokedObject,
                                     @Advice.Local("result") Object localVariable
    ) {
        if (localVariable != null) {
            value = localVariable;
            return value;
        }
        StaticInterceptionInfo interceptionInfo = FinalInterceptor.getObjectRules(invokedObject);
        if (interceptionInfo == null || interceptionInfo.isSpy())
            return value;
        var returnType = method.getReturnType();
        if(!returnType.equals(Void.TYPE))
            value = getDefaultValue(returnType);
        return value;
    }

    public static <T> T getDefaultValue(Class<T> clazz) {
        return (T) Array.get(Array.newInstance(clazz, 1), 0);
    }

    public static void addStick(Stick stick, Object mock) throws IllegalStateException{
        mockMap.get(mock).addStick(stick);
    }

    public static StaticInterceptionInfo getObjectRules(Object mock) {
        return mockMap.get(mock);
    }

    public static void addIntercepted(Object mock, boolean isSpy){
        mockMap.put(mock, new StaticInterceptionInfo(isSpy));
    }
}
