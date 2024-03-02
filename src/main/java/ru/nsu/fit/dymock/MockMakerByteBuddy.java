package ru.nsu.fit.dymock;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import org.objenesis.ObjenesisStd;

import java.util.ArrayDeque;
import java.util.Deque;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class MockMakerByteBuddy implements MockMaker {

    private final ObjenesisStd objenesis = new ObjenesisStd();

    @Override
    public <T> T createMock(Class<T> classToMock) {
        ByteBuddy byteBuddy = new ByteBuddy();
        Deque<Stick> callMatchers = new ArrayDeque<>();
        Interceptor interceptor = new Interceptor(callMatchers);
        Class<? extends T> classWithInterceptor = byteBuddy.subclass(classToMock)
                .method(not(isDeclaredBy(Object.class)))
                .intercept(MethodDelegation
                        .toField("interceptor"))
                .defineField("interceptor", Interceptor.class, Visibility.PRIVATE)
                .implement(InterceptionAccessor.class).intercept(FieldAccessor.ofBeanProperty())
                .make()
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER).getLoaded();

        T mock = objenesis.newInstance(classWithInterceptor);
        ((InterceptionAccessor) mock).setInterceptor(interceptor);
        return mock;
    }

    @Override
    public <T> Intercepted createStaticMock(Class<T> classToMock) {
        new ByteBuddy()
                .redefine(classToMock)
                .visit(Advice.to(StaticInterceptor.class).on(not(isDeclaredBy(Object.class)).and(isStatic())))
                .make()
                .load(classToMock.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        return new Intercepted(classToMock.getCanonicalName());
    }
}
