package ru.nsu.fit.dymock;

public class Stick {
    private String methodName;

    private Object[] arguments;

    private final Object result;

    public Stick(String methodName, Object[] arguments, Object result) {
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
