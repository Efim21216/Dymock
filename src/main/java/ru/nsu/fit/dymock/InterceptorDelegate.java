package ru.nsu.fit.dymock;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.Method;

public class InterceptorDelegate {
    @RuntimeType
    public Object intercept(@AllArguments Object[] allArguments,
                            @Origin Method method,
                            @This Object mock) {
        System.out.println(method.getName() + " was involved on object " + mock.getClass());
        return method.getDefaultValue();
    }
}
