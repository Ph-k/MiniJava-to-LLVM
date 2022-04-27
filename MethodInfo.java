import java.util.Map;
import java.util.HashMap;

public class MethodInfo {
    private Map<String, String> argumentsMap = new HashMap<String, String>();
    private Map<String, String> variableMap = new HashMap<String, String>();

    public boolean addArgument(String argName, String argType){
        return argumentsMap.put(argName, argType) == null ? true : false ;
    }

    public String findArgument(String argName){
        return argumentsMap.get(argName);
    }

    public boolean addVariable(String variableName, String variableType){
        return variableMap.put(variableName, variableType) == null ? true : false ;
    }

    public String findVariable(String variableName){
        return variableMap.get(variableName);
    }
}
