package ru.nsu.fit.dymock;

public class Stick {
    private String methodName;

    private LeafMatcher[] arguments;

    private final Object result;

    public Stick(String methodName, LeafMatcher[] arguments, Object result) {
        this.methodName = methodName;
        this.arguments = arguments;
        this.result = result;
    }

    public String getMethodName() {
        return methodName;
    }

    public Object getResult() {
        return result;
    }

}
