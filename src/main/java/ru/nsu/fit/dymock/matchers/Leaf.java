package ru.nsu.fit.dymock.matchers;

import java.util.Arrays;

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
        private double lo_limit = -Double.MAX_VALUE;
        private double hi_limit = Double.MAX_VALUE;

        public LimitLeaf(){
            super(Number.class);
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
            && this.lo_limit <= value 
            && value <= this.hi_limit;
        }
    }

    private class CompositeOrLeaf extends LeafMatcher{
        private LeafMatcher[] matchers;
        public CompositeOrLeaf(LeafMatcher... matchers){
            super(Object.class);
            this.matchers = Arrays.copyOf(matchers, matchers.length);
        }

        @Override
        public boolean matches(Object actual) {
            if(matchers.length == 0)
                return true;

            for (LeafMatcher matcher : matchers) {
                if(matcher.matches(actual))
                    return true;
            }
            return false;
        }
    }

    private class CompositeAndLeaf extends LeafMatcher{
        private LeafMatcher[] matchers;
        public CompositeAndLeaf(LeafMatcher... matchers){
            super(Object.class);
            this.matchers = Arrays.copyOf(matchers, matchers.length);
        }

        @Override
        public boolean matches(Object actual) {
            if(matchers.length == 0)
                return true;

            for (LeafMatcher matcher : matchers) {
                if(!matcher.matches(actual))
                    return false;
            }
            return true;
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

    public static LeafMatcher any(){
        return INSTANCE.new GreenLeaf();
    }

    public static LeafMatcher any(Class<?> clazz){
        return INSTANCE.new GreenLeaf(clazz);
    }

    public static LeafMatcher eq(Object wanted){
        return INSTANCE.new YellowLeaf(wanted);
    }

    public static LeafMatcher eq(Object wanted, Class<?> clazz){
        return INSTANCE.new YellowLeaf(wanted, clazz);
    }

    public static LeafMatcher linkEq(Object wanted){
        return INSTANCE.new RedLeaf(wanted);
    }

    public static LeafMatcher linkEq(Object wanted, Class<?> clazz){
        return INSTANCE.new RedLeaf(wanted, clazz);
    }

    public static LeafMatcher fpEq(Number wanted){
        return INSTANCE.new FloatingLeaf(wanted);
    }

    public static LeafMatcher fpEq(Number wanted, double threshold){
        return INSTANCE.new FloatingLeaf(wanted, threshold);
    }

    /**
     * Create list that checks if argument is within (-inf, a], [a,b] or [b, inf) boundaries
     */
    public static LimitLeaf limit(){
        return INSTANCE.new LimitLeaf();
    }

    public static PartialLeaf partial(String paramName, LeafMatcher matcher){
        return INSTANCE.new PartialLeaf(paramName, matcher);
    }

    public static LeafMatcher or(LeafMatcher... matchers){
        return INSTANCE.new CompositeOrLeaf(matchers);
    }

    public static LeafMatcher and(LeafMatcher... matchers){
        return INSTANCE.new CompositeAndLeaf(matchers);
    }
}
