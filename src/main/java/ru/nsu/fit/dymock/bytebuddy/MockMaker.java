package ru.nsu.fit.dymock.bytebuddy;


public interface MockMaker {
    <T> T createMock(Class<T> classToMock, boolean isSpy, boolean isFinal);
    <T> Intercepted<T> createStaticMock(Class<T> classToMock, boolean isSpy);
    <T> FinalIntercepted<T> createFinalMock(Class<T> classToMock, boolean isSpy);
}
