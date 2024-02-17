package ru.nsu.fit.dymock;

import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.List;

public class MockInterceptorByteBuddy{
    private final List<Stick> callDetails;

    public MockInterceptorByteBuddy(List<Stick> callDetails) {
        this.callDetails = callDetails;
    }

    @RuntimeType
    public Object invoke(@Origin Method invokedMethod,
                         @AllArguments Object[] arguments) {
        String methodName = invokedMethod.getName();
        System.out.println(methodName + " was involved");
        Stick result = callDetails.stream().filter(stick -> stick.getMethodName()
                .equals(methodName)).findAny().orElse(null);
        if (result != null) {
            return result.getResult();

        }
        return invokedMethod.getDefaultValue();
    }
}
