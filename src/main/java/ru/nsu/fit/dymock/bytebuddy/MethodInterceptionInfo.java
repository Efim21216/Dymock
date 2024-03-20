package ru.nsu.fit.dymock.bytebuddy;

import ru.nsu.fit.dymock.matchers.PartialStick;
import ru.nsu.fit.dymock.matchers.Stick;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.lang.reflect.Parameter;

/*
 * Contains call information of sticks specific to some mock
 */
public class MethodInterceptionInfo {
    private final List<Stick> sticks;
    private final List<PartialStick> partialSticks;
    private int countCalls = 0;


    public MethodInterceptionInfo(List<Stick> sticks, List<PartialStick> partialSticks) {
        this.sticks = new LinkedList<>();
        this.partialSticks = new LinkedList<>();
        for (Stick stick : sticks) {
            this.sticks.add(stick);
        }
        for (PartialStick universalStick : partialSticks) {
            this.partialSticks.add(universalStick);
        }
    }

    public void incrementLocalStick(Stick stick){
        stick.incrementCountCalls();        
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

    public PartialStick getSuitablePartialStick(Parameter[] parameters, Object[] arguments) {
        List<PartialStick> result = partialSticks.stream()
                .filter(stick -> stick.matchesPartialLeaves(parameters, arguments))
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

    public void addPartialStick(PartialStick partialStick){
        partialSticks.add(partialStick);
    }
}
