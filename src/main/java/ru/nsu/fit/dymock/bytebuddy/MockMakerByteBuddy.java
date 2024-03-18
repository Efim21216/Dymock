package ru.nsu.fit.dymock.bytebuddy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import org.objenesis.ObjenesisStd;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class MockMakerByteBuddy implements MockMaker {

    private final ObjenesisStd objenesis = new ObjenesisStd();

    @Override
    public <T> T createMock(Class<T> classToMock, boolean isSpy) {
        ByteBuddy byteBuddy = new ByteBuddy();
        Interceptor<T> interceptor = new Interceptor<>(classToMock, isSpy);
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
    public <T> Intercepted<T> createStaticMock(Class<T> classToMock, boolean isSpy) {
        new ByteBuddy()
                .redefine(classToMock)
                .visit(Advice.to(StaticInterceptor.class).on(not(isDeclaredBy(Object.class)).and(isStatic())))
                .make()
                .load(classToMock.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

        StaticInterceptor.addIntercepted(classToMock, isSpy);
        return new Intercepted<>(classToMock);
    }
}
