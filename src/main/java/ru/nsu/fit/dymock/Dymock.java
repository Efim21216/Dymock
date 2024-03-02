package ru.nsu.fit.dymock;

import net.bytebuddy.agent.ByteBuddyAgent;

import java.util.List;

public class Dymock {
    private static final MockMaker maker = new MockMakerByteBuddy();
    private static Boolean isAgentInstalled = false;
    public static <T> T burn(Class<T> classToMock) {
        return maker.createMock(classToMock);
    }
    public static <T> Intercepted burnDown(Class<T> classToMock) {
        if (!isAgentInstalled) {
            ByteBuddyAgent.install();
            isAgentInstalled = true;
        }
        return maker.createStaticMock(classToMock);
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
