package ru.nsu.fit.dymock.bytebuddy;


public interface MockMaker {
    <T> T createMock(Class<T> classToMock, boolean isSpy);
    <T> Intercepted<T> createStaticMock(Class<T> classToMock, boolean isSpy);
}
