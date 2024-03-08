package ru.nsu.fit.dymock.bytebuddy;

import ru.nsu.fit.dymock.matchers.Stick;

import java.util.List;
import java.util.stream.Collectors;

public class MethodInterceptionInfo {
    private final List<Stick> sticks;
    private int countCalls = 0;

    public MethodInterceptionInfo(List<Stick> sticks) {
        this.sticks = sticks;
    }

    public List<Stick> getSticks() {
        return sticks;
    }
    public void incrementCountCalls() {
        countCalls++;
    }

    public int getCountCalls() {
        return countCalls;
    }
    public Stick getSuitableStick(Object[] arguments) {
        List<Stick> result = sticks.stream()
                .filter(stick -> stick.matchesLeaves(arguments))
                .collect(Collectors.toList());
        if (result.size() == 0)
            return null;
        return result.get(result.size() - 1);
    }
}
