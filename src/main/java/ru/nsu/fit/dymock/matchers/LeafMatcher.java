package ru.nsu.fit.dymock.matchers;

public class LeafMatcher {
    private final Class<?> argType;
    public LeafMatcher(Class<?> argType){
        this.argType = argType;
    }
    public LeafMatcher(){
        this.argType = Object.class;
    }

    public boolean matches(Object actual){
        return this.argType.isInstance(actual);
    }
}
