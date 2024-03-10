package ru.nsu.fit.dymock;

import net.bytebuddy.agent.ByteBuddyAgent;
import ru.nsu.fit.dymock.bytebuddy.Intercepted;
import ru.nsu.fit.dymock.bytebuddy.InterceptionAccessor;
import ru.nsu.fit.dymock.bytebuddy.MockMaker;
import ru.nsu.fit.dymock.bytebuddy.MockMakerByteBuddy;
import ru.nsu.fit.dymock.matchers.Stick;
import ru.nsu.fit.dymock.matchers.LeafMatcher;

import java.util.List;

public class Dymock {
    private static Dymock INSTANCE = new Dymock();

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
        if (mock instanceof InterceptionAccessor
            && ((InterceptionAccessor) mock).getInterceptor().getCountCalls() > 0) {
            return true;
        }
        return false;
    }
    public static boolean ignited(Object mock, ExactBasker ebasker) {
        if (mock instanceof InterceptionAccessor){
            int calls = ((InterceptionAccessor) mock).getInterceptor().getCountCalls();
            if(ebasker.fits(calls)) {
            return true;
            }
        }
        return false;
    }
    public static boolean ignited(Object mock, LimitBasker lbasker) {
        if (mock instanceof InterceptionAccessor){
            int calls = ((InterceptionAccessor) mock).getInterceptor().getCountCalls();
            if(lbasker.fits(calls)) {
                return true;
            } 
        }
        return false;
    }
    public static boolean ignited(Object mock, Stick stick) {
        if (mock instanceof InterceptionAccessor) {
            if(((InterceptionAccessor) mock).getInterceptor().getLocalCountCalls(stick) > 0){
                return true;
            }
        }
        return false;
    }
    public static boolean ignited(Object mock, Stick stick, ExactBasker ebasker) {
        if (mock instanceof InterceptionAccessor){
            int calls = ((InterceptionAccessor) mock).getInterceptor().getLocalCountCalls(stick);
            if(ebasker.fits(calls)) {
            return true;
            }
        }
        return false;
    }
    public static boolean ignited(Object mock, Stick stick, LimitBasker lbasker) {
        if (mock instanceof InterceptionAccessor){
            int calls = ((InterceptionAccessor) mock).getInterceptor().getLocalCountCalls(stick);
            if(lbasker.fits(calls)) {
                return true;
            } 
        }
        return false;
    }
    public static boolean ignited(Object mock, String methodName) {
        if (mock instanceof InterceptionAccessor) {
            if(((InterceptionAccessor) mock).getInterceptor().getMethodCountCalls(methodName) > 0){
                return true;
            }
        }
        return false;
    }
    public static boolean ignited(Object mock, String methodName, ExactBasker ebasker) {
        if (mock instanceof InterceptionAccessor){
            int calls = ((InterceptionAccessor) mock).getInterceptor().getMethodCountCalls(methodName);
            if(ebasker.fits(calls)) {
            return true;
            }
        }
        return false;
    }
    public static boolean ignited(Object mock, String methodName, LimitBasker lbasker) {
        if (mock instanceof InterceptionAccessor){
            int calls = ((InterceptionAccessor) mock).getInterceptor().getMethodCountCalls(methodName);
            if(lbasker.fits(calls)) {
                return true;
            } 
        }
        return false;
    }

    public class ExactBasker{
        private final int exact;

        public ExactBasker(int exact){
            this.exact = exact;
        }

        public boolean fits(int value){
            return value == this.exact;
        }
    }

    public class LimitBasker{
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

        public boolean fits(int value){
            return this.low < value && value < this.high;
        }
    }

    public static ExactBasker exactly(int value){
        return INSTANCE.new ExactBasker(value);
    }

    public static LimitBasker limited(){
        return INSTANCE.new LimitBasker();
    }
}
