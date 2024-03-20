package ru.nsu.fit.dymock;

import net.bytebuddy.agent.ByteBuddyAgent;
import ru.nsu.fit.dymock.bytebuddy.*;
import ru.nsu.fit.dymock.matchers.Stick;

import java.lang.reflect.Modifier;
public class Dymock {
    private static final Dymock INSTANCE = new Dymock();

    private static final MockMaker maker = new MockMakerByteBuddy();
    private static Boolean isAgentInstalled = false;
    public static <T> T burn(Class<T> classToMock) {
        return maker.createMock(classToMock, false, checkIsFinal(classToMock));
    }
    public static <T> T spy(Class<T> classToSpy) {
        return maker.createMock(classToSpy, true, checkIsFinal(classToSpy));
    }
    private static <T> boolean checkIsFinal(Class<T> classToMock) {
        boolean isFinal = Modifier.isFinal(classToMock.getModifiers());
        if (!isAgentInstalled && isFinal) {
            ByteBuddyAgent.install();
            isAgentInstalled = true;
        }
        return isFinal;
    }
    public static <T> Intercepted<T> burnDown(Class<T> classToMock) {
        if (!isAgentInstalled) {
            ByteBuddyAgent.install();
            isAgentInstalled = true;
        }
        return maker.createStaticMock(classToMock, false);
    }
    public static <T> Intercepted<T> spyStatic(Class<T> classToMock) {
        if (!isAgentInstalled) {
            ByteBuddyAgent.install();
            isAgentInstalled = true;
        }
        return maker.createStaticMock(classToMock, true);
    }

    public static boolean ignited(Object mock) {
        if (mock instanceof InterceptionAccessor
            && ((InterceptionAccessor<?>) mock).getInterceptor().getCountCalls() > 0) {
            return true;
        }
        if (mock instanceof Intercepted) {
            return StaticInterceptor.getClassRules(((Intercepted<?>) mock).getClazz())
                    .getClassCountCalls() > 0;
        }
        StaticInterceptionInfo info = FinalInterceptor.getObjectRules(mock);
        if (info == null)
            return false;
        int calls = info.getClassCountCalls();
        return calls > 0;
    }
    public static boolean ignited(Object mock, Basker basker) {
        if (mock instanceof InterceptionAccessor){
            int calls = ((InterceptionAccessor<?>) mock).getInterceptor().getCountCalls();
            return basker.fits(calls);
        }
        if (mock instanceof Intercepted) {
            int calls = StaticInterceptor.getClassRules(((Intercepted<?>) mock).getClazz()).getClassCountCalls();
            return basker.fits(calls);
        }
        StaticInterceptionInfo info = FinalInterceptor.getObjectRules(mock);
        if (info == null)
            return false;
        int calls = info.getClassCountCalls();
        return basker.fits(calls);
    }
    public static boolean ignited(Object mock, Stick stick) {
        if (mock instanceof InterceptionAccessor) {
            return ((InterceptionAccessor<?>) mock).getInterceptor().getLocalCountCalls(stick) > 0;
        }
        if (mock instanceof Intercepted) {
            return StaticInterceptor.getClassRules(((Intercepted<?>) mock)
                    .getClazz()).getLocalCountCalls(stick) > 0;
        }
        StaticInterceptionInfo info = FinalInterceptor.getObjectRules(mock);
        if (info == null)
            return false;
        int calls = info.getLocalCountCalls(stick);
        return calls > 0;
    }
    public static boolean ignited(Object mock, Stick stick, Basker basker) {
        if (mock instanceof InterceptionAccessor){
            int calls = ((InterceptionAccessor<?>) mock).getInterceptor().getLocalCountCalls(stick);
            return basker.fits(calls);
        }
        if (mock instanceof Intercepted) {
            int calls = StaticInterceptor.getClassRules(((Intercepted<?>) mock).getClazz()).getLocalCountCalls(stick);
            return basker.fits(calls);
        }
        StaticInterceptionInfo info = FinalInterceptor.getObjectRules(mock);
        if (info == null)
            return false;
        int calls = info.getLocalCountCalls(stick);
        return basker.fits(calls);
    }
    public static boolean ignited(Object mock, String methodName) {
        if (mock instanceof InterceptionAccessor) {
            return ((InterceptionAccessor<?>) mock).getInterceptor().getMethodCountCalls(methodName) > 0;
        }
        if (mock instanceof Intercepted) {
            return StaticInterceptor.getClassRules(((Intercepted<?>) mock)
                    .getClazz()).getMethodCountCalls(methodName) > 0;
        }
        StaticInterceptionInfo info = FinalInterceptor.getObjectRules(mock);
        if (info == null)
            return false;
        int calls = info.getMethodCountCalls(methodName);
        return calls > 0;
    }
    public static boolean ignited(Object mock, String methodName, Basker basker) {
        if (mock instanceof InterceptionAccessor){
            int calls = ((InterceptionAccessor<?>) mock).getInterceptor().getMethodCountCalls(methodName);
            return basker.fits(calls);
        }
        if (mock instanceof Intercepted) {
            int calls = StaticInterceptor.getClassRules(((Intercepted<?>) mock).getClazz()).getMethodCountCalls(methodName);
            return basker.fits(calls);
        }
        StaticInterceptionInfo info = FinalInterceptor.getObjectRules(mock);
        if (info == null)
            return false;
        int calls = info.getMethodCountCalls(methodName);
        return basker.fits(calls);
    }
    public static boolean ignited(Object mock, String methodName, Class<?>... arguments) {
        if (mock instanceof InterceptionAccessor){
            return ((InterceptionAccessor<?>) mock).getInterceptor().getSignatureCountCalls(methodName, arguments) > 0;
        }
        if (mock instanceof Intercepted) {
            int calls = StaticInterceptor.getClassRules(((Intercepted<?>) mock).getClazz())
                    .getSignatureCountCalls(methodName, arguments);
            return calls > 0;
        }
        StaticInterceptionInfo info = FinalInterceptor.getObjectRules(mock);
        if (info == null)
            return false;
        return info.getSignatureCountCalls(methodName, arguments) > 0;
    }
    public static boolean ignited(Object mock, String methodName, Basker basker, Class<?>... arguments) {
        if (mock instanceof InterceptionAccessor){
            int calls = ((InterceptionAccessor<?>) mock).getInterceptor().getSignatureCountCalls(methodName, arguments);
            return basker.fits(calls);
        }
        if (mock instanceof Intercepted) {
            int calls = StaticInterceptor.getClassRules(((Intercepted<?>) mock).getClazz())
                    .getSignatureCountCalls(methodName, arguments);
            return basker.fits(calls);
        }
        StaticInterceptionInfo info = FinalInterceptor.getObjectRules(mock);
        if (info == null)
            return false;
        int calls = info.getSignatureCountCalls(methodName, arguments);
        return basker.fits(calls);
    }

    public class ExactBasker implements Basker {
        private final int exact;

        public ExactBasker(int exact){
            this.exact = exact;
        }
        @Override
        public boolean fits(int value){
            return value == this.exact;
        }
    }

    public class LimitBasker implements Basker {
        private int low = Integer.MIN_VALUE;
        private int high = Integer.MAX_VALUE;

        public LimitBasker atLeast(int low){
            this.low = low;
            return this;
        }

        public LimitBasker atMost(int high){
            this.high = high;
            return this;
        }
        @Override
        public boolean fits(int value){
            return this.low < value && value < this.high;
        }
    }
    public interface Basker {
        boolean fits(int value);
    }

    public static ExactBasker exactly(int value){
        return INSTANCE.new ExactBasker(value);
    }

    public static LimitBasker atMost(int high) {
        LimitBasker basker = INSTANCE.new LimitBasker();
        basker.high = high;
        return basker;
    }
    public static LimitBasker atLeast(int low) {
        LimitBasker basker = INSTANCE.new LimitBasker();
        basker.low = low;
        return basker;
    }
    public static LimitBasker inRange(int low, int high) {
        LimitBasker basker = INSTANCE.new LimitBasker();
        basker.low = low;
        basker.high = high;
        return basker;
    }
}
