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
    private static final Map<Class<?>, Map<String, MethodInterceptionInfo>> classMap = new HashMap<>();

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
        MethodInterceptionInfo interceptionInfo = StaticInterceptor.getClassRules(method.getDeclaringClass()).get(name);
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

    public static <T> T getDefaultValue(Class<T> clazz) {
        return (T) Array.get(Array.newInstance(clazz, 1), 0);
    }

    public static void addStick(Stick stick, Class<?> clazz) throws IllegalStateException{
        String name = stick.getMethodName();
        Map<String, MethodInterceptionInfo> clazzSticks = classMap.get(clazz);
        if(clazzSticks == null){
            throw new IllegalStateException("Can't add sticks to an unmocked class");
        }

        MethodInterceptionInfo info = clazzSticks.get(name);
        if (info == null)
            info = clazzSticks.put(name, new MethodInterceptionInfo(new ArrayList<>(Arrays.asList(stick))));
        else
            info.getSticks().add(stick);
    }

    public static Map<String, MethodInterceptionInfo> getClassRules(Class<?> clazz) {
        return classMap.get(clazz);
    }

    public static void addIntercepted(Class<?> clazz){
        classMap.put(clazz, new HashMap<>());
    }
}
