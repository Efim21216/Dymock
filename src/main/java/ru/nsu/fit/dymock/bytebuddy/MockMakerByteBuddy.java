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
    public <T> T createMock(Class<T> classToMock, boolean isSpy, boolean isFinal) {
        if (isFinal) {
            throw new IllegalStateException("Can't mock final classes like this: use createFinalMock");
        }
        ByteBuddy byteBuddy = new ByteBuddy();
        Interceptor<T> interceptor = new Interceptor<>(classToMock, isSpy);
        Class<? extends T> classWithInterceptor = byteBuddy
                .subclass(classToMock)
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
    public <T> FinalIntercepted<T> createFinalMock(Class<T> classToMock, boolean isSpy) {
        Class<? extends T> mockedClass = new ByteBuddy()
                .redefine(classToMock)
                .visit(Advice.to(FinalInterceptor.class).on(not(isDeclaredBy(Object.class)).and(not(isConstructor()))))
                .make()
                .load(classToMock.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent()).getLoaded();
        T mock = objenesis.newInstance(mockedClass);
        FinalInterceptor.addIntercepted(mock, isSpy);
        return new FinalIntercepted<T>(classToMock, mock);
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
