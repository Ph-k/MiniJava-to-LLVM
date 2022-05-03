import java.util.Map;
import java.util.HashMap;

public class ClassData {
    private Map<String, MethodData> methodMap = new HashMap<String, MethodData>();
    private Map<String, String> variableMap = new HashMap<String, String>();

    public MethodData addMethod(String methodName, String returnType){
        MethodData newMethodData = new MethodData(methodName,returnType);
        methodMap.put(methodName, newMethodData);
        return newMethodData;
    }

    public MethodData findMethod(String methodName){
        return methodMap.get(methodName);
    }

    public boolean addVariable(String variableName, String variableType){
        return variableMap.put(variableName, variableType) == null ? true : false ;
    }

    public String findVariable(String variableName){
        return variableMap.get(variableName);
    }

    public void print(){
        System.out.println("\tVariables:");
        for (Map.Entry<String,String> entry : variableMap.entrySet()){
            System.out.println("\t\t" + entry.getKey() + " : " + entry.getValue());
        }
        System.out.println("\tMethods:");
        for (Map.Entry<String,MethodData> entry : methodMap.entrySet()){
            entry.getValue().print();
        }
    }
}
