package ru.nsu.fit.dymock;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import org.objenesis.ObjenesisStd;

import java.util.LinkedList;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.any;

public class MockMakerByteBuddy implements MockMaker {

    private final ObjenesisStd objenesis = new ObjenesisStd();

    @Override
    public <T> T createMock(Class<T> classToMock) {
        ByteBuddy byteBuddy = new ByteBuddy();
        List<Stick> callMatchers = new LinkedList<>();
        Class<? extends T> classWithInterceptor = byteBuddy.subclass(classToMock)
                .method(any())
                .intercept(MethodDelegation
                        .to(new MockInterceptorByteBuddy(callMatchers)))
                .make()
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER).getLoaded();

        T mock = objenesis.newInstance(classWithInterceptor);
        BonfireBuilder.setSticks(callMatchers);
        return mock;
    }
}
