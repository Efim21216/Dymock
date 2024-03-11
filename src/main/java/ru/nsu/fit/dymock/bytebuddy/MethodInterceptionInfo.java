package ru.nsu.fit.dymock.bytebuddy;

import ru.nsu.fit.dymock.matchers.Stick;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/*
 * Contains call information of sticks specific to some mock
 */
public class MethodInterceptionInfo {
    private final List<Stick> sticks;
    private int countCalls = 0;


    public MethodInterceptionInfo(List<Stick> sticks) {
        this.sticks = new LinkedList<>();
        for (Stick stick : sticks) {
            this.sticks.add(stick);
        }
    }

    public void incrementLocalStick(Stick stick){
        sticks.get(sticks.indexOf(stick)).incrementCountCalls();
    }
    public void incrementMethodCallCount() {
        countCalls++;
    }

    public Stick getSuitableStick(Object[] arguments) {
        List<Stick> result = sticks.stream()
                .filter(stick -> stick.matchesLeaves(arguments))
                .collect(Collectors.toList());
        if (result.size() == 0)
            return null;
        return result.get(result.size() - 1);
    }

    public int getLocalCallCount(Stick stick){
        return sticks.get(sticks.indexOf(stick)).getCountCalls();
    }

    public int getMethodCallCount(){
        return countCalls;
    }

    public void addStick(Stick stick){
        sticks.add(stick);
    }
}
