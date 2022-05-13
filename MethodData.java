import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

// This class saved all the data associated with a method
public class MethodData {

    // Personally, the easiest way to assosiate arguments types with arguments names, and be able to have for the example the type of the 5th argument was two lists
    // one for the name of the argument, and one for the types of the argument.
    // Given that in a function arguments are usually not that many, this implimantion should not suffer on performance. And it can be better fited in my program
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
            return argumentNames.size() == 0;
        }
    }

    ArgumentsMap argumentsMap = new ArgumentsMap();

    // The assosiation between name and type of variable is done on a simple HashMap. Since we do not care about insertion order
    private Map<String, String> variableMap = new HashMap<String, String>();
    private String returnType, name;
    private int offset;
    private boolean override; // True if function overides another

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
            return argumentsMap.put(argName, argType);
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

    // This funcyion searches for an arument, if it fails, it searches for a variable
    public String findArngNVariable(String varName) {
        String varType = findVariable(varName);
        return (varType != null) ?  varType : findArgument(varName);
    }

    // Returns Nth argument
    public String findNArng(int index) {
        return argumentsMap.get(index);
    }

    public String getReturnType(){
        return returnType;
    }

    public int getOffset() {return offset;}

    // Checks if the given method has the same argumets, number & types. Used when overiding
    public boolean argsEquals(MethodData givenMethodData){
        if(this.argumentsMap.size() != givenMethodData.argumentsMap.size()) return false;  
       
        for (int i = 0; i<this.argumentsMap.size(); i++) {
            if( !argumentsMap.get(i).equals( givenMethodData.argumentsMap.get(i) ) ){
                return false;
            }
        }
        return true;
    }

    // Method to print the arguments and the variables in DETAIL
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


    // Method to print the offsets of variables
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
