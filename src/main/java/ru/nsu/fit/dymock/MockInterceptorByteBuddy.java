package ru.nsu.fit.dymock;

import java.lang.reflect.Method;
import java.util.List;

public class MockInterceptorByteBuddy implements MockInterceptor {
    private final List<CallDetails> callDetails;

    public MockInterceptorByteBuddy(List<CallDetails> callDetails) {
        this.callDetails = callDetails;
    }
    @Override
    public Object invoke(Object mock, Method invokedMethod, Object[] arguments) {

        String methodName = invokedMethod.getName();

        CallDetails invocationDetails = new CallDetails(methodName, arguments, mock.getClass());

        if (!callDetails.contains(invocationDetails)) {
            callDetails.add(invocationDetails);
            return invokedMethod.getDefaultValue();

        } else {

            CallDetails recordedBehaviour = callDetails.get(callDetails.indexOf(invocationDetails));
            return recordedBehaviour.getResult();

        }
    }
}
