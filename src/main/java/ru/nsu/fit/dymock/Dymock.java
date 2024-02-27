package ru.nsu.fit.dymock;

import java.util.List;

public class Dymock {
    private static Dymock INSTANCE = new Dymock();

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

    private class GreenLeaf implements LeafMatcher{
        @Override
        public boolean matches(Object actual) {
            return true;
        }
    }

    private class YellowLeaf implements LeafMatcher{
        private final Object wanted;
    
        @Override
        public boolean matches(Object actual) {
            return wanted.equals(actual);
        }
    
        public YellowLeaf(Object wanted){
            this.wanted = wanted;
        }
        
    }

    private class RedLeaf implements LeafMatcher{
        private final Object wanted;
    
        @Override
        public boolean matches(Object actual) {
            return wanted == actual;
        }
    
        public RedLeaf(Object wanted){
            this.wanted = wanted;
        }
    }

    public static LeafMatcher green(){
        return INSTANCE.new GreenLeaf();
    }

    public static LeafMatcher yellow(Object wanted){
        return INSTANCE.new YellowLeaf(wanted);
    }

    public static LeafMatcher red(Object wanted){
        return INSTANCE.new RedLeaf(wanted);
    }
}
