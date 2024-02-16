package ru.nsu.fit.dymock;

import java.util.List;

public class Dymock {
    public static <T> T burn(Class<T> classToMock) {
        return null;
    }
    public static <T, R> Stick stick(Class<T> mockedClass, String methodName,
                                  List<Leaf> matchers, R returnValue) {
        return null;
    }
    public static <T, R> Stick wetStick(Class<T> mockedClass, String methodName,
                                     List<Leaf> matchers, R returnValue) {
        return null;
    }
    public static <T> void ignited() {}
}
