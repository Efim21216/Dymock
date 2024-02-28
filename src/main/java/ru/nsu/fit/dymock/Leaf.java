package ru.nsu.fit.dymock;

public class Leaf {
    private static Leaf INSTANCE = new Leaf();

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
