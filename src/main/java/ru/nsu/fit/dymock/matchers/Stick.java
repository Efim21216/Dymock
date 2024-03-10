package ru.nsu.fit.dymock.matchers;

import ru.nsu.fit.dymock.Dymock;

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

    public String getMethodName() {
        return methodName;
    }
    public Object getResult() {
        return result;
    }

    public boolean bask() {
        if(countCalls > 0){
            return true;
        }
        return false;
    }

    public boolean bask(Dymock.ExactBasker ebasker) {
        if(countCalls == ebasker.getExact()){
            return true;
        }
        return false;
    }

    public boolean bask(Dymock.LimitBasker lbasker) {
        if(lbasker.getLow() < countCalls 
            && countCalls < lbasker.getHigh()){
            return true;
        }
        return false;
    }
}
