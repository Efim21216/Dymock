package ru.nsu.fit.dymock.matchers;

import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Arrays;

import ru.nsu.fit.dymock.matchers.Leaf.PartialLeaf;

public class PartialStick extends Stick{
    private Map<String, LeafMatcher> leafMap;

    public PartialStick(String methodName, Object result, PartialLeaf... arguments){
        super(methodName, result);
        this.leafMap = new HashMap<>();
        for(PartialLeaf leaf : arguments){
            leafMap.put(leaf.getParamName(), leaf.getMatcher());
        }
    }

    @Override
    public boolean matchesLeaves(Object[] arguments){
        throw new UnsupportedOperationException("Can't match regular calls with partial stick");
    }

    public boolean matchesPartialLeaves(Parameter[] parameters, Object[] arguments){
        List<String> callNames = Arrays.asList(parameters).stream().map(Parameter::getName).toList();
        for(String parameterName : leafMap.keySet()){
            int i = callNames.indexOf(parameterName);
            
            //specified kwarg is not present
            if(i == -1)
                return false;

            //specified kwarg is not matched
            if(!leafMap.get(parameterName).matches(arguments[i]))
                return false;
        }
        return true;
    }
}
