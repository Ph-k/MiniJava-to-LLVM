import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class MethodData {
    ArrayList<String> argumentNames = new ArrayList<String>();
    ArrayList<String> argumentTypes = new ArrayList<String>();
    private class ArgumentsMap{
        ArrayList<String> argumentNames = new ArrayList<String>();
        ArrayList<String> argumentTypes = new ArrayList<String>();
        public boolean put(String argName, String argType){
            argumentNames.add(argName);
            argumentTypes.add(argType);
            return true;
        }

        public String get(String argName){
            int index = argumentNames.indexOf(argName);
            if( index != -1 ){
                return argumentTypes.get(index);
            }else return null;
        }

        public String get(int argIndex){
            String arg;
            try{
                arg = argumentTypes.get(argIndex);
            }catch (Exception e) {
                arg = null;
            }

            return arg;
        }

        public String getName(int argIndex){
            String arg;
            try{
                arg = argumentNames.get(argIndex);
            }catch (Exception e) {
                arg = null;
            }

            return arg;
        }

        public int size(){
            return argumentNames.size();
        }

        public boolean isEmpty(){
            return argumentNames.size() == 0 ? true : false;
        }
    }  
    ArgumentsMap argumentsMap = new ArgumentsMap();
    //private Map<String, String> argumentsMap = new LinkedHashMap<String, String>();
    private Map<String, String> variableMap = new HashMap<String, String>();
    private String returnType, name;
    private int offset;
    private boolean override;

    MethodData(String givenName, String givenReturnType, int givenOffest, boolean givenOverride){
        returnType = givenReturnType;
        name = givenName;
        offset = givenOffest;
        override = givenOverride;
    }

    public boolean overrides(){
        return override;
    }

    public boolean addArgument(String argName, String argType) throws Exception{
        if(argumentsMap.get(argName) == null){
            return argumentsMap.put(argName, argType)/* == null ? true : false*/ ;
        }else{
            System.out.println("For arg: " + argName);
            throw new TypeCheckingException("Redefinition of argument: " + argName + "!");
        }
    }

    public String findArgument(String argName){
        return argumentsMap.get(argName);
    }

    public boolean addVariable(String variableName, String variableType) throws Exception{
        if(variableMap.get(variableName) == null && argumentsMap.get(variableName) == null){
            return variableMap.put(variableName, variableType) == null ? true : false ;
        }else{
            throw new TypeCheckingException("Redefinition of variable: " + variableName + "!");
        }
    }

    public String findVariable(String variableName){
        return variableMap.get(variableName);
    }

    public String findArngNVariable(String varName) {
        String varType = findVariable(varName);
        return (varType != null) ?  varType : findArgument(varName);
    }

    public String findNArng(int index) {
        return argumentsMap.get(index);
    }

    public String getReturnType(){
        return returnType;
    }

    public int getOffset() {return offset;}

    public boolean argsEquals(MethodData givenMethodData){
        if(this.argumentsMap.size() != givenMethodData.argumentsMap.size()) return false;  
       
        for (int i = 0; i<this.argumentsMap.size(); i++) {
            if( !argumentsMap.get(i).equals( givenMethodData.argumentsMap.get(i) ) ){
                return false;
            }
        }
        return true;
    }

    public void print() {
        System.out.print("\t\t" + offset + " : " + returnType + " " + name + "(");
        if( argumentsMap.isEmpty() == false){
            for (int i = 0; i<this.argumentsMap.size(); i++) {
                System.out.print(argumentsMap.getName(i) + " : " + argumentsMap.get(i) + ", ");
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

    public void printOffsets(String className) {
        System.out.println(className + "." + name + " : " + offset);
    }

    public String getName() {
        return name;
    }

    public int getArgsCount() {
        return this.argumentsMap.size();
    }

}
