package ru.nsu.fit.dymock.bytebuddy;

import ru.nsu.fit.dymock.matchers.MethodSignature;
import ru.nsu.fit.dymock.matchers.PartialStick;
import ru.nsu.fit.dymock.matchers.Stick;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.lang.reflect.Parameter;

/*
 * Contains call information of sticks specific to some mock
 */
public class MethodInterceptionInfo {
    private final List<Stick> sticks;
    private final List<PartialStick> partialSticks;
    public final Map<MethodSignature, Integer> signatureCalls = new HashMap<>();
    private int countCalls = 0;
    private final String methodName;


    public MethodInterceptionInfo(List<Stick> sticks, List<PartialStick> partialSticks, String methodName) {
        this.sticks = new LinkedList<>();
        this.partialSticks = new LinkedList<>();
        this.methodName = methodName;
        this.sticks.addAll(sticks);
        this.partialSticks.addAll(partialSticks);
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
    public void incrementSignatureCalls(Object[] arguments) {
        MethodSignature signature = new MethodSignature(methodName, arguments);
        if (signatureCalls.containsKey(signature))
            signatureCalls.put(signature, signatureCalls.get(signature) + 1);
        else
            signatureCalls.put(signature, 1);
    }
    public int getSignatureCalls(Class<?>[] args) {
        Integer result = signatureCalls.get(new MethodSignature(methodName, args));
        if (result == null)
            return 0;
        return result;
    }
}
