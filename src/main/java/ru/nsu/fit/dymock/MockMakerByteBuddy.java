package ru.nsu.fit.dymock;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import org.objenesis.ObjenesisStd;

import java.util.LinkedList;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class MockMakerByteBuddy implements MockMaker {

    private final ObjenesisStd objenesis = new ObjenesisStd();

    @Override
    public <T> T createMock(Class<T> classToMock) {
        ByteBuddy byteBuddy = new ByteBuddy();
        List<Stick> callMatchers = new LinkedList<>();
        Interceptor interceptor = new Interceptor(callMatchers);
        Class<? extends T> classWithInterceptor = byteBuddy.subclass(classToMock)
                .method(not(isDeclaredBy(Object.class)))
                .intercept(MethodDelegation
                        .to(interceptor))
                .defineField("interceptor", Interceptor.class, Visibility.PRIVATE)
                .implement(InterceptionAccessor.class).intercept(FieldAccessor.ofBeanProperty())
                .make()
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER).getLoaded();

        T mock = objenesis.newInstance(classWithInterceptor);
        ((InterceptionAccessor) mock).setInterceptor(interceptor);
        return mock;
    }
}
