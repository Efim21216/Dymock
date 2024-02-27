package ru.nsu.fit.dymock;

import net.bytebuddy.implementation.bind.annotation.*;
import net.bytebuddy.implementation.bytecode.constant.ClassConstant;

import java.lang.reflect.Method;
import java.util.List;
import java.lang.reflect.Array;

public class Interceptor {
    private final List<Stick> callDetails;

    public Interceptor(List<Stick> callDetails) {
        this.callDetails = callDetails;
    }

    @RuntimeType
    public Object invoke(@Origin Method invokedMethod,
                         @AllArguments Object[] arguments) {
        String methodName = invokedMethod.getName();
        System.out.println(methodName + " was involved");
        Stick result = callDetails.stream()
                                    .filter(
                                        stick -> stick.getMethodName().equals(methodName) 
                                        && stick.matchesLeaves(arguments))
                                    .findAny().orElse(null);
        if (result != null) {
            return result.getResult();
        }

        var returnType = invokedMethod.getReturnType();
        if(!returnType.equals(Void.TYPE)){
            return getDefaultValue(returnType);
        }

        return null;
    }

    private static <T> T getDefaultValue(Class<T> clazz) {
        return (T) Array.get(Array.newInstance(clazz, 1), 0);
    }
 
    public void addStick(Stick stick) {
        callDetails.add(stick);
    }
}
