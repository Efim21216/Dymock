package ru.nsu.fit.dymock;

import java.lang.reflect.Method;

public interface MockInterceptor {
    Object invoke(Object mock, Method invokedMethod, Object[] arguments);
}
