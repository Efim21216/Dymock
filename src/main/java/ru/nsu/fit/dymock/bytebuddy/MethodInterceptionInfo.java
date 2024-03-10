package ru.nsu.fit.dymock.bytebuddy;

import ru.nsu.fit.dymock.matchers.Stick;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/*
 * Contains call information of sticks specific to some mock
 */
public class MethodInterceptionInfo {
    private final Map<Stick, Integer> sticksCounts;

    public MethodInterceptionInfo(List<Stick> sticks) {
        this.sticksCounts = new HashMap<>();
        for (Stick stick : sticks) {
            this.sticksCounts.put(stick, 0);
        }
    }

    public void incrementLocalStick(Stick stick){
        int count = this.sticksCounts.get(stick);
        this.sticksCounts.put(stick, count+1);
    }

    public Stick getSuitableStick(Object[] arguments) {
        List<Stick> result = sticksCounts.keySet().stream()
                .filter(stick -> stick.matchesLeaves(arguments))
                .collect(Collectors.toList());
        if (result.size() == 0)
            return null;
        return result.get(result.size() - 1);
    }

    public int getCountCount(Stick stick){
        return this.sticksCounts.get(stick);
    }

    public void addStick(Stick stick){
        this.sticksCounts.putIfAbsent(stick, 0);
    }
}
