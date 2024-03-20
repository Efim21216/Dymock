package ru.nsu.fit.dymock.matchers;

import java.util.Arrays;
import java.util.Objects;

public class MethodSignature {
    private String methodName;
    private Class<?>[] arguments;

    public MethodSignature(String methodName, Object[] arguments) {
        this.methodName = methodName;
        this.arguments = new Class<?>[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            this.arguments[i] = arguments[i].getClass();
        }
    }

    public MethodSignature(String methodName, Class<?>[] arguments) {
        this.methodName = methodName;
        this.arguments = arguments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodSignature that = (MethodSignature) o;
        return methodName.equals(that.methodName) && match(that.arguments);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(methodName);
        result = 31 * result + Arrays.hashCode(arguments);
        return result;
    }

    private boolean match(Class<?>[] args) {
        if (args.length != arguments.length)
            return false;
        for (int i = 0; i < args.length; i++) {
            if (!args[i].equals(arguments[i])) {
                return false;
            }
        }
        return true;
    }
}
