import java.util.Map;
import java.util.HashMap;

public class ClassData {
    private Map<String, MethodData> methodMap = new HashMap<String, MethodData>();
    private Map<String, tupleTypeOffset> variableMap = new HashMap<String, tupleTypeOffset>();
    private int currentMethodOffset = 0, currentVariableOffset = 0;

    public int getMethodOffset(){
        return currentMethodOffset;
    }

    public int getVariableOffset(){
        return currentVariableOffset;
    }

    // Used if the class extends
    public void setOffsets(int givenCurrentMethodOffset, int givenCurrentVariableOffset){
        currentMethodOffset = givenCurrentMethodOffset;
        currentVariableOffset = givenCurrentVariableOffset;
    }

    private void methodOffsetIncreaser(){
        currentMethodOffset += 8;
    }

    private void variableOffsetIncreaser(String type){
        if( type.equals("int") ){ // int case
            currentVariableOffset += 4;
        }else if( type.equals("boolean") ){ // boolean case
            currentVariableOffset += 1;
        }else{ // anything else, arrays, object references etc are considerred pointer-sized
            currentVariableOffset += 8;
        }
    }

    public MethodData addMethod(String methodName, String returnType){
        MethodData newMethodData = new MethodData(methodName,returnType,currentMethodOffset);
        methodOffsetIncreaser();
        methodMap.put(methodName, newMethodData);
        return newMethodData;
    }

    public MethodData findMethod(String methodName){
        return methodMap.get(methodName);
    }

    public boolean addVariable(String variableName, String variableType){
        int variableOffset = currentVariableOffset;
        variableOffsetIncreaser(variableType);
        return variableMap.put(variableName, new tupleTypeOffset(variableType,variableOffset) ) == null ? true : false ;
    }

    public String findVariable(String variableName){
        return variableMap.get(variableName).variableType;
    }

    public void print(){
        System.out.println("\tVariables:");
        for (Map.Entry<String,tupleTypeOffset> entry : variableMap.entrySet()){
            System.out.println("\t\t" + entry.getValue().variableType + " " + entry.getKey() + " : " + entry.getValue().offset);
        }
        System.out.println("\tMethods:");
        for (Map.Entry<String,MethodData> entry : methodMap.entrySet()){
            entry.getValue().print();
        }
    }

    // custom tuple class to save both the type of variable and it's offset
    private class tupleTypeOffset {
        private String variableType;
        private int offset;
    
        tupleTypeOffset(String givenVariableType, int givenOffset){
            variableType = givenVariableType;
            offset = givenOffset;
        }
    }
}

