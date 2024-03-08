package ru.nsu.fit.dymock.bytebuddy;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import ru.nsu.fit.dymock.matchers.Stick;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class StaticInterceptor {
    private static final Map<Method, MethodInterceptionInfo> mapping = new HashMap<>();

    @Advice.OnMethodEnter(skipOn = Advice.OnDefaultValue.class)
    public static Object onMethodBegin() {
        return null;
    }

    @Advice.OnMethodExit
    public static Object onMethodEnd(@Advice.Return(readOnly = false, typing = Assigner.Typing.DYNAMIC) Object value,
                                     @Advice.Origin Method method,
                                     @Advice.AllArguments Object[] arguments
    ) {
        MethodInterceptionInfo interceptionInfo = StaticInterceptor.getRules().get(method);
        if (interceptionInfo == null) {
            MethodInterceptionInfo info = new MethodInterceptionInfo(new LinkedList<>());
            info.incrementCountCalls();
            StaticInterceptor.getRules().put(method, info);
            return value;
        }
        interceptionInfo.incrementCountCalls();
        Stick result = interceptionInfo.getSuitableStick(arguments);
        if (result == null)
            return value;
        result.incrementCountCalls();
        value = result.getResult();
        return value;
    }
    public static void addStick(Stick stick, Class<?> clazz) {
        Method method = stick.getMethod(clazz);
        MethodInterceptionInfo info = mapping.get(method);
        if (info == null)
            mapping.put(method, new MethodInterceptionInfo(new LinkedList<>()));
        else
            info.getSticks().add(stick);
    }
    public static Map<Method, MethodInterceptionInfo> getRules() {
        return mapping;
    }
}
