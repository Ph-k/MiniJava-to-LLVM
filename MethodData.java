import java.util.Map;
import java.util.HashMap;

public class MethodData {
    private Map<String, String> argumentsMap = new HashMap<String, String>();
    private Map<String, String> variableMap = new HashMap<String, String>();
    private String returnType, name;

    MethodData(String givenName, String givenReturnType){
        returnType = givenReturnType;
        name = givenName;
    }

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

    public void print() {
        System.out.print("\t\t" + returnType + " " + name + "(");
        if( argumentsMap.isEmpty() == false){
            for (Map.Entry<String,String> entry : argumentsMap.entrySet()){
                System.out.print(entry.getKey() + " : " + entry.getValue() + ", ");
            }
        }
        System.out.println(")");
        if( variableMap.isEmpty() == false ){
            System.out.println("\t\t\tVariables:");
            for (Map.Entry<String,String> entry : variableMap.entrySet()){
                System.out.println("\t\t\t\t" + entry.getKey() + " : " + entry.getValue());
            }
        }
    }
}
