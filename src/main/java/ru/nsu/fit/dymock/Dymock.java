package ru.nsu.fit.dymock;

import net.bytebuddy.agent.ByteBuddyAgent;
import ru.nsu.fit.dymock.bytebuddy.Intercepted;
import ru.nsu.fit.dymock.bytebuddy.InterceptionAccessor;
import ru.nsu.fit.dymock.bytebuddy.MockMaker;
import ru.nsu.fit.dymock.bytebuddy.MockMakerByteBuddy;
import ru.nsu.fit.dymock.matchers.LeafMatcher;
import ru.nsu.fit.dymock.matchers.Stick;

import java.lang.reflect.Method;
import java.util.List;

public class Dymock {
    private static final MockMaker maker = new MockMakerByteBuddy();
    private static Boolean isAgentInstalled = false;
    public static <T> T burn(Class<T> classToMock) {
        return maker.createMock(classToMock);
    }
    public static <T> Intercepted<T> burnDown(Class<T> classToMock) {
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
    public static <T> Stick wetStick(Class<T> mockedClass, String methodName,
                                     List<LeafMatcher> matchers, Throwable throwable) {
        return null;
    }
    public static boolean ignited(Object mock) {
        if (mock instanceof InterceptionAccessor) {
            System.out.println(((InterceptionAccessor) mock).getInterceptor().getCountCalls());
            return true;
        }
        return false;
    }
    public static boolean ignited(Object mock, String methodName, Class<?>... arguments) throws NoSuchMethodException {
        Method method = mock.getClass().getMethod(methodName, arguments);
        if (mock instanceof InterceptionAccessor) {
            System.out.println(((InterceptionAccessor) mock).getInterceptor().getCountCallsMethod(method));
            return true;
        }
        return false;
    }
}
