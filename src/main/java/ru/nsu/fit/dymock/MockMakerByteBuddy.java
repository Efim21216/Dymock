package ru.nsu.fit.dymock;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import org.objenesis.ObjenesisStd;
import static net.bytebuddy.matcher.ElementMatchers.any;

public class MockMakerByteBuddy implements MockMaker {

    private final ObjenesisStd objenesis = new ObjenesisStd();

    @Override
    public <T> T createMock(Class<T> classToMock, MockInterceptor handler) {
        ByteBuddy byteBuddy = new ByteBuddy();

        Class<? extends T> classWithInterceptor = byteBuddy.subclass(classToMock)
                .method(any())
                .intercept(MethodDelegation.to(InterceptorDelegate.class))
                .make()
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER).getLoaded();

        return objenesis.newInstance(classWithInterceptor);
    }
}
