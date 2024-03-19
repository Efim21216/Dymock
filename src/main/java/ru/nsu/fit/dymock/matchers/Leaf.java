package ru.nsu.fit.dymock.matchers;

public class Leaf {
    private static Leaf INSTANCE = new Leaf();

    private class GreenLeaf extends LeafMatcher{
        public GreenLeaf(){
            super(Object.class);
        }
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
            super(wanted.getClass());
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
            super(wanted.getClass());
            this.wanted = wanted;
        }        
        public RedLeaf(Object wanted, Class<?> argType){
            super(argType);
            this.wanted = wanted;
        }
    }
    
    private class FloatingLeaf extends LeafMatcher{
        private final Number wanted;
        private double THRESHOLD = .0001;

        public FloatingLeaf(Number wanted){
            super(Number.class);
            this.wanted = wanted;
        }

        public FloatingLeaf(Number wanted, double threshold){
            super(Number.class);
            this.wanted = wanted;
            this.THRESHOLD = threshold;
        }
        
        @Override
        public boolean matches(Object actual) {
            return super.matches(actual) && Math.abs(wanted.doubleValue() - ((Number)actual).doubleValue()) < this.THRESHOLD;
        }
    }
    
    public class LimitLeaf extends LeafMatcher{
        private double THRESHOLD = .0001;
        private double lo_limit = -Double.MAX_VALUE;
        private double hi_limit = Double.MAX_VALUE;

        public LimitLeaf(){
            super(Number.class);
        }

        public LimitLeaf(double threshold){
            super(Number.class);
            this.THRESHOLD = threshold;
        }

        public LimitLeaf from(double min){
            this.lo_limit = min;
            return this;
        }

        public LimitLeaf to(double max){
            this.hi_limit = max;
            return this;
        }
        
        @Override
        public boolean matches(Object actual) {
            double value = ((Number) actual).doubleValue();
            return super.matches(actual) 
            && value - this.lo_limit > this.THRESHOLD 
            && this.hi_limit - value > this.THRESHOLD;
        }
    }

    public class PartialLeaf{
        private String paramName;
        private LeafMatcher matcher;
        public PartialLeaf(String paramName, LeafMatcher matcher){
            this.paramName = paramName;
            this.matcher = matcher;
        }
        public boolean matches(String actualName, Object actual){
            if(!this.paramName.equals(actualName))
                return false;
            return matcher.matches(actual);
        }
        public String getParamName(){
            return this.paramName;
        }
        public LeafMatcher getMatcher(){
            return this.matcher;
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

    public static LeafMatcher fleaf(Number wanted){
        return INSTANCE.new FloatingLeaf(wanted);
    }

    public static LeafMatcher fleaf(Number wanted, double threshold){
        return INSTANCE.new FloatingLeaf(wanted, threshold);
    }

    public static LimitLeaf limit(){
        return INSTANCE.new LimitLeaf();
    }

    public static LimitLeaf limit(double threshold){
        return INSTANCE.new LimitLeaf(threshold);
    }

    public static PartialLeaf partial(String paramName, LeafMatcher matcher){
        return INSTANCE.new PartialLeaf(paramName, matcher);
    }
}
