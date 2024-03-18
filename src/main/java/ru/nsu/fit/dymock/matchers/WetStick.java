package ru.nsu.fit.dymock.matchers;

public class WetStick extends Stick {
    public WetStick(String methodName, Throwable result, LeafMatcher... arguments) {
        super(methodName, result, arguments);
    }
    public WetStick(String methodName, Throwable result) {
        super(methodName, result);
    }
    public Throwable getResult() {
        Object result = super.getResult();
        if (result instanceof Throwable)
            return (Throwable) result;
        throw new IllegalStateException("Result wet stick must be throwable");
    }
}
