package ru.nsu.fit.dymock.matchers;

import ru.nsu.fit.dymock.Dymock;

public class Stick {
    private final String methodName;

    private LeafMatcher[] leaves;
    private final boolean needsMatching;

    private final Object result;
    private int countCalls = 0;

    public Stick(String methodName, LeafMatcher[] arguments, Object result) {
        this.methodName = methodName;
        this.leaves = arguments;
        this.needsMatching = true;
        this.result = result;
    }

    public Stick(String methodName, Object result) {
        this.methodName = methodName;
        this.needsMatching = false;
        this.result = result;
    }
    public boolean matchesLeaves(Object[] arguments){
        if(this.needsMatching == false){
            return true;
        }

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
