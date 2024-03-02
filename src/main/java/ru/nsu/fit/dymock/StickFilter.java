package ru.nsu.fit.dymock;

import java.util.function.Predicate;

public class StickFilter implements Predicate<Stick> {
    private final String methodName;
    private final Object[] arguments;

    public StickFilter(String methodName, Object[] arguments) {
        this.methodName = methodName;
        this.arguments = arguments;
    }

    @Override
    public boolean test(Stick stick) {
        return stick.getMethodName().equals(methodName)
                && stick.matchesLeaves(arguments);
    }
}
