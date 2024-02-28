package ru.nsu.fit.dymock;

import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Deque;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import java.lang.reflect.Array;

public class Interceptor {
    private final Deque<Stick> callDetails;

    public Interceptor(Deque<Stick> callDetails) {
        this.callDetails = callDetails;
    }

    @RuntimeType
    public Object invoke(@Origin Method invokedMethod,
                         @AllArguments Object[] arguments) {
        String methodName = invokedMethod.getName();
        System.out.println(methodName + " was involved");
        Stick result = StreamSupport.stream(Spliterators.spliteratorUnknownSize(callDetails.descendingIterator(), 0), false)
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
