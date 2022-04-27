import java.util.Map;
import java.util.HashMap;

public class ClassInfo {
    private Map<String, MethodInfo> methodMap = new HashMap<String, MethodInfo>();
    private Map<String, String> variableMap = new HashMap<String, String>();

    public boolean addMethod(String methodName){
        return methodMap.put(methodName, new MethodInfo()) == null ? true : false ;
    }

    public MethodInfo findMethod(String methodName){
        return methodMap.get(methodName);
    }

    public boolean addVariable(String variableName, String variableType){
        return variableMap.put(variableName, variableType) == null ? true : false ;
    }

    public String findVariable(String variableName){
        return variableMap.get(variableName);
    }


}
