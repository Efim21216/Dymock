package ru.nsu.fit.dymock.bytebuddy;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import ru.nsu.fit.dymock.matchers.Stick;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.lang.reflect.Array;

public class StaticInterceptor {
    private static final Map<String, MethodInterceptionInfo> mapping = new HashMap<>();

    @Advice.OnMethodEnter(skipOn = Advice.OnDefaultValue.class)
    public static Object onMethodBegin() {
        return null;
    }

    @Advice.OnMethodExit
    public static Object onMethodEnd(@Advice.Return(readOnly = false, typing = Assigner.Typing.DYNAMIC) Object value,
                                     @Advice.Origin Method method,
                                     @Advice.AllArguments Object[] arguments
    ) {
        String name = method.getName();
        MethodInterceptionInfo interceptionInfo = StaticInterceptor.getRules().get(name);
        if (interceptionInfo != null) {
            Stick stick = interceptionInfo.getSuitableStick(arguments);
            if (stick != null) {
                stick.incrementCountCalls();
                value = stick.getResult();
                return null;
            }
        }
        var returnType = method.getReturnType();
        if(!returnType.equals(Void.TYPE)){
            value = getDefaultValue(returnType);
            return null;
        }
        return null;
    }

    private static <T> T getDefaultValue(Class<T> clazz) {
        return (T) Array.get(Array.newInstance(clazz, 1), 0);
    }

    public static void addStick(Stick stick, Class<?> clazz) {
        String name = stick.getMethodName();
        MethodInterceptionInfo info = mapping.get(name);
        if (info == null)
            info = mapping.put(name, new MethodInterceptionInfo(new ArrayList<>(Arrays.asList(stick))));
        else
            info.getSticks().add(stick);
    }
    public static Map<String, MethodInterceptionInfo> getRules() {
        return mapping;
    }
}
