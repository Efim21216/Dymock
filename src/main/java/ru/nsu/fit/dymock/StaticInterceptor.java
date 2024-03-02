package ru.nsu.fit.dymock;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StaticInterceptor {
    private static final Map<String, List<Stick>> rules = new HashMap<>();

    @Advice.OnMethodEnter(skipOn = Advice.OnDefaultValue.class)
    public static Object onMethodBegin() {
        return null;
    }

    @Advice.OnMethodExit
    public static Object onMethodEnd(@Advice.Return(readOnly = false, typing = Assigner.Typing.DYNAMIC) Object value,
                                     @Advice.Origin Object origin,
                                     @Advice.AllArguments Object[] arguments
    ) {
        String[] parts = origin.toString().split(" ");
        String methodName = parts[parts.length - 1];
        if (!StaticInterceptor.getRules().containsKey(methodName)) {
            StaticInterceptor.getRules().put(methodName, new LinkedList<>());
            return value;
        }

        List<Stick> suitable = StaticInterceptor.getRules().get(methodName).stream()
                .filter(new StickFilter(methodName, arguments)).collect(Collectors.toList());
        if (suitable.size() == 0)
            return value;
        value = suitable.get(suitable.size() - 1).getResult();
        return value;
    }
    public static void addRule(Stick stick) {
        rules.computeIfAbsent(stick.getMethodName(), k -> new LinkedList<>());
        rules.get(stick.getMethodName()).add(stick);
    }
    public static Map<String, List<Stick>> getRules() {
        return rules;
    }
}
