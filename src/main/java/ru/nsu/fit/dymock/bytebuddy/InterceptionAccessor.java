package ru.nsu.fit.dymock.bytebuddy;

public interface InterceptionAccessor<T> {
    Interceptor<T> getInterceptor();
    void setInterceptor(Interceptor<T> interceptor);
}
