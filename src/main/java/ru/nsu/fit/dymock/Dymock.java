package ru.nsu.fit.dymock;

import java.util.List;

public class Dymock {
    private static final MockMaker maker = new MockMakerByteBuddy();
    public static <T> T burn(Class<T> classToMock) {
        return maker.createMock(classToMock);
    }
    public static <T, R> Stick stick(Class<T> mockedClass, String methodName,
                                  List<LeafMatcher> matchers, R returnValue) {
        return null;
    }
    public static <T, R> Stick wetStick(Class<T> mockedClass, String methodName,
                                     List<LeafMatcher> matchers, Throwable throwable) {
        return null;
    }
    public static <T> boolean ignited() {
        return true;
    }
}