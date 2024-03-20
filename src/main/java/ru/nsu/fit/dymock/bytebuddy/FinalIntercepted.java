package ru.nsu.fit.dymock.bytebuddy;

public class FinalIntercepted<T> extends Intercepted{
    private T mock;

    public FinalIntercepted(Class<T> clazz, T mock) {
        super(clazz);
        this.mock = mock;
    }
    
    public T getMock(){
        return mock;
    }
}
