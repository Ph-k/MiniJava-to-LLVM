import java.util.Map;
import java.util.LinkedHashMap;

// For classes that do NOT extend
public class ClassData {
    protected Map<String, MethodData> methodMap = new LinkedHashMap<String, MethodData>();
    protected Map<String, tupleTypeOffset> variableMap = new LinkedHashMap<String, tupleTypeOffset>();
    protected int currentMethodOffset = 0, currentVariableOffset = 0;
    protected String name;

    ClassData(String givenName){
        name = givenName;
    }

    public String getName(){
        return name;
    }

    public int getMethodOffset(){
        return currentMethodOffset;
    }

    public int getVariableOffset(){
        return currentVariableOffset;
    }

    protected void methodOffsetIncreaser(){
        currentMethodOffset += 8;
    }

    protected void variableOffsetIncreaser(String type){
        if( type.equals("int") ){ // int case
            currentVariableOffset += 4;
        }else if( type.equals("boolean") ){ // boolean case
            currentVariableOffset += 1;
        }else{ // anything else, arrays, object references etc are considerred pointer-sized
            currentVariableOffset += 8;
        }
    }

    public MethodData addMethod(String methodName, String returnType) throws Exception{
        MethodData newMethodData = new MethodData(methodName,returnType,currentMethodOffset, false);

        // Adding the method only if another with the same name has not been declared
        if( methodMap.get(methodName) == null ){
            methodOffsetIncreaser();
            methodMap.put(methodName, newMethodData);
        }else{
            throw new TypeCheckingException("Redeclaration of method!");
        }

        return newMethodData;
    }

    public MethodData findMethod(String methodName){
        return methodMap.get(methodName);
    }

    public boolean addVariable(String variableName, String variableType) throws Exception{
        int variableOffset = currentVariableOffset;

        // Adding the class only if another with the same name has not been declared
        if( variableMap.get(variableName) == null ){
            variableMap.put(variableName,  new tupleTypeOffset(variableType,variableOffset));
            variableOffsetIncreaser(variableType);
        }else{
            throw new TypeCheckingException("Redeclaration of variable!");
        }
        return true;
    }

    public String findVariable(String variableName){
        tupleTypeOffset typeNoffset = variableMap.get(variableName);
        return (typeNoffset != null)? typeNoffset.variableType : null ;
    }

    public boolean checkParrentType(String type){
        return type.equals(name);
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

    public void printOffsets() {
        System.out.println("--Variables---");
        for (Map.Entry<String,tupleTypeOffset> entry : variableMap.entrySet()){
            System.out.println(name + "." + entry.getKey() + " : " + entry.getValue().offset);
        }
        System.out.println("---Methods---");
        for (Map.Entry<String,MethodData> entry : methodMap.entrySet()){
            if(!entry.getValue().overrides())
                entry.getValue().printOffsets(name);
        }
    }

    // custom tuple class to save both the type of variable and it's offset
    protected class tupleTypeOffset {
        protected String variableType;
        protected int offset;
    
        tupleTypeOffset(String givenVariableType, int givenOffset){
            variableType = givenVariableType;
            offset = givenOffset;
        }
    }

    public boolean methodsCheck() throws Exception{
        throw new TypeCheckingException("You called a ExtendedClassData method from a simple ClassData!");
    }

}

