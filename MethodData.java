import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class MethodData {
    private Map<String, String> argumentsMap = new LinkedHashMap<String, String>();
    private Map<String, String> variableMap = new HashMap<String, String>();
    private String returnType, name;
    private int offset;

    MethodData(String givenName, String givenReturnType, int givenOffest){
        returnType = givenReturnType;
        name = givenName;
        offset = givenOffest;
    }

    public boolean addArgument(String argName, String argType) throws Exception{
        if(argumentsMap.get(argName) == null){
            return argumentsMap.put(argName, argType) == null ? true : false ;
        }else{
            System.out.println("For arg: " + argName);
            throw new Exception("Redefinition of argument!");
        }
    }

    public String findArgument(String argName){
        return argumentsMap.get(argName);
    }

    public boolean addVariable(String variableName, String variableType) throws Exception{
        if(variableMap.get(variableName) == null && argumentsMap.get(variableName) == null){
            return variableMap.put(variableName, variableType) == null ? true : false ;
        }else{
            throw new Exception("Redefinition of variable!");
        }
    }

    public String findVariable(String variableName){
        return variableMap.get(variableName);
    }

    public String findArngNVariable(String varName) {
        String varType = findVariable(varName);
        return (varType != null) ?  varType : findArgument(varName);
    }

    public int getOffset() {return offset;}

    public boolean argsEquals(MethodData givenMethodData){
        if(this.argumentsMap.size() != givenMethodData.argumentsMap.size()) return false;  
        int index = 0;
        Object[] parrentAgrs = givenMethodData.argumentsMap.values().toArray();
        for (Map.Entry<String, String> entry : this.argumentsMap.entrySet()) {
            if (parrentAgrs[index] != entry.getValue()){
                return false;
            }
            index++;
        }
        return true;
    }

    public void print() {
        System.out.print("\t\t" + offset + " : " + returnType + " " + name + "(");
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
