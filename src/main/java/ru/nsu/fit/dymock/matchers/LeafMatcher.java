package ru.nsu.fit.dymock.matchers;

public class LeafMatcher {
    protected Class<?> argType = null;
    public LeafMatcher(){};
    public LeafMatcher(Class<?> argType){
        this.argType = argType;
    }

    public boolean matches(Object actual){
        if(this.argType == null) return true;
        else return this.argType.isInstance(actual);
    }
}
