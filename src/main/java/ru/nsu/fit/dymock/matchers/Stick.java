package ru.nsu.fit.dymock.matchers;


import java.lang.reflect.Method;

public class Stick {
    private String methodName;

    private LeafMatcher[] leaves;

    private final Object result;
    private int countCalls = 0;

    public Stick(String methodName, LeafMatcher[] arguments, Object result) {
        this.methodName = methodName;
        this.leaves = arguments;
        this.result = result;
    }
    public boolean matchesLeaves(Object[] arguments){
        if(arguments.length != leaves.length){
            return false;
        }
        for(int i = 0; i < leaves.length; i++){
            if(!leaves[i].matches(arguments[i])){
                return false;
            }
        }
        
        return true;
    }

    public int getCountCalls() {
        return countCalls;
    }
    public void incrementCountCalls() {
        countCalls++;
    }
    public boolean bask() {
        System.out.println(countCalls);
        return true;
    }

    public Method getMethod(Class<?> clazz) {
        //TODO
        return null;
    }
    public Object getResult() {
        return result;
    }

}
