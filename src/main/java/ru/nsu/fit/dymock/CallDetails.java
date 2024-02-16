package ru.nsu.fit.dymock;

public class CallDetails {

    private Object mock;

    private String methodName;

    private Object[] arguments;

    private Object result;

    public CallDetails(String methodName, Object[]arguments, Object mock) {
        this.methodName = methodName;
        this.arguments = arguments;
        this.mock = mock;
    }

    public void thenReturn(Object result) {
        this.result = result;
    }

    public Object getResult() {
        return result;
    }

}
