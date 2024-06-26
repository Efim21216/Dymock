package ru.nsu.fit.dymock.bytebuddy;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import ru.nsu.fit.dymock.matchers.PartialStick;
import ru.nsu.fit.dymock.matchers.Stick;
import ru.nsu.fit.dymock.matchers.WetStick;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Array;

public class StaticInterceptor {
    private static final Map<Class<?>, StaticInterceptionInfo> classMap = new HashMap<>();

    @Advice.OnMethodEnter(skipOn = Advice.OnDefaultValue.class)
    public static Object onMethodBegin(
            @Advice.Origin Method method,
            @Advice.AllArguments Object[] arguments,
            @Advice.Local("result") Object localVariable
    ) throws Throwable {
        String name = method.getName();
        StaticInterceptionInfo interceptionInfo = StaticInterceptor.getClassRules(method.getDeclaringClass());
        if (interceptionInfo == null)
            throw new IllegalStateException("Interception info for static is null");
        interceptionInfo.incrementClassCountCalls();
        interceptionInfo.incrementMethodCountCalls(name, arguments);
        Stick stick = interceptionInfo.getSuitableStick(name, arguments);
        if (stick != null) {
            interceptionInfo.incrementLocalCountCalls(stick);
            if (stick instanceof WetStick) {
                throw ((WetStick) stick).getResult();
            }
            localVariable = stick.getResult();
            return null;
        }        
        PartialStick partialStick = interceptionInfo.getSuitablePartialStick(name, method.getParameters(), arguments);
        if (partialStick != null) {
            interceptionInfo.incrementLocalCountCalls(partialStick);
            localVariable = partialStick.getResult();
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
                                     @Advice.Local("result") Object localVariable
    ) {
        if (localVariable != null) {
            value = localVariable;
            return value;
        }
        StaticInterceptionInfo interceptionInfo = StaticInterceptor.getClassRules(method.getDeclaringClass());
        if (interceptionInfo.isSpy())
            return value;
        var returnType = method.getReturnType();
        if(!returnType.equals(Void.TYPE))
            value = getDefaultValue(returnType);
        return value;
    }

    public static <T> T getDefaultValue(Class<T> clazz) {
        return (T) Array.get(Array.newInstance(clazz, 1), 0);
    }

    public static void addStick(Stick stick, Class<?> clazz) throws IllegalStateException{
        classMap.get(clazz).addStick(stick);
    }
    public static void addPartialStick(PartialStick stick, Class<?> clazz) throws IllegalStateException{
        classMap.get(clazz).addPartialStick(stick);
    }

    public static StaticInterceptionInfo getClassRules(Class<?> clazz) {
        return classMap.get(clazz);
    }

    public static void addIntercepted(Class<?> clazz, boolean isSpy){
        classMap.put(clazz, new StaticInterceptionInfo(isSpy));
    }
}
