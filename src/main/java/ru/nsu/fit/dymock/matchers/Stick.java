package ru.nsu.fit.dymock.matchers;

import ru.nsu.fit.dymock.Dymock;

public class Stick {
    private final String methodName;

    private final LeafMatcher[] leaves;

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
        return countCalls > 0;
    }

    public boolean bask(Dymock.Basker basker) {
        return basker.fits(countCalls);
    }

}
