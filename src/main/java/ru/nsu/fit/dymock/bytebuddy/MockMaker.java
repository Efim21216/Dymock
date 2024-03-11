package ru.nsu.fit.dymock.bytebuddy;


public interface MockMaker {
    <T> T createMock(Class<T> classToMock);
    <T> Intercepted<T> createStaticMock(Class<T> classToMock);
}
