package ru.nsu.fit.dymock.matchers;

public class Leaf {
    private static Leaf INSTANCE = new Leaf();

    private class GreenLeaf extends LeafMatcher{
        public GreenLeaf(){}
        public GreenLeaf(Class<?> argType){
            super(argType);
        }
        @Override
        public boolean matches(Object actual) {
            return super.matches(actual);
        }
    }

    private class YellowLeaf extends LeafMatcher{
        private final Object wanted;

        @Override
        public boolean matches(Object actual) {
            return super.matches(actual) && wanted.equals(actual);
        }
    
        public YellowLeaf(Object wanted){
            this.wanted = wanted;
        }
    
        public YellowLeaf(Object wanted, Class<?> argType){
            super(argType);
            this.wanted = wanted;
        }
        
    }

    private class RedLeaf extends LeafMatcher{
        private final Object wanted;

        @Override
        public boolean matches(Object actual) {
            return super.matches(actual) && wanted == actual;
        }
    
        public RedLeaf(Object wanted){
            this.wanted = wanted;
        }        
        public RedLeaf(Object wanted, Class<?> argType){
            super(argType);
            this.wanted = wanted;
        }
    }

    public static LeafMatcher green(){
        return INSTANCE.new GreenLeaf();
    }

    public static LeafMatcher green(Class<?> clazz){
        return INSTANCE.new GreenLeaf(clazz);
    }

    public static LeafMatcher yellow(Object wanted){
        return INSTANCE.new YellowLeaf(wanted);
    }

    public static LeafMatcher yellow(Object wanted, Class<?> clazz){
        return INSTANCE.new YellowLeaf(wanted, clazz);
    }

    public static LeafMatcher red(Object wanted){
        return INSTANCE.new RedLeaf(wanted);
    }

    public static LeafMatcher red(Object wanted, Class<?> clazz){
        return INSTANCE.new RedLeaf(wanted, clazz);
    }
}
