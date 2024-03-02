package ru.nsu.fit.dymock;

public interface MockMaker {
    <T> T createMock(Class<T> classToMock);
    <T> Intercepted createStaticMock(Class<T> classToMock);
}
