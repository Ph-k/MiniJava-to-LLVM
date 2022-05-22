import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;

// This class represents a class (that does NOT extend), and holds all the nessesery information about it.
// (If the class extends, a subclass of ClassData is used, the ExtendedClassData)
public class ClassData {
    // Map to associate the name of method to a referance of it's object
    protected Map<String, MethodData> methodMap = new LinkedHashMap<String, MethodData>();
    // Map to associate the name of a variable, to the offset and type of it
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

    public Map<String, MethodData> getMethodMap(){
        return methodMap;
    }

    protected void methodOffsetIncreaser(){
        currentMethodOffset += 8;
    }

    // Increases the offset according to the given type
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

        // Adding the variable only if another with the same name has not been declared
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

    // Method to print the data of the class in DETAIL
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

    // Method to print the data of the class as show in the examples of the project
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

    // A you may have noticed at the variableMap a variable is saved on a typle with it's type and offset
    // In need for a smiple tuple this custom to save both the type of variable and it's offset was created
    protected class tupleTypeOffset {
        protected String variableType;
        protected int offset;
    
        tupleTypeOffset(String givenVariableType, int givenOffset){
            variableType = givenVariableType;
            offset = givenOffset;
        }
    }

    // This function needs to be decleared here, because the checkParrentType on ExtendedClassData is recursive
    // The recursion of ExtendedClassData.checkParrentType stops here
    public boolean checkParrentType(String type){
        return type.equals(name);
    }

    public boolean methodsCheck() throws Exception{
        throw new Exception("You called a ExtendedClassData method from a simple ClassData!");
    }

    public ArrayList<ClassData> getParents(){
        return new ArrayList<ClassData>();
    }

    protected void addParents(ArrayList<ClassData> parentsList) {
        parentsList.add(this);
    }

    public int getNumberOfNonOverridingMethods() {
        int s=0;
        for (Map.Entry<String,MethodData> entry : methodMap.entrySet()){
            if(!entry.getValue().overrides())
                s++;
        }
        return s;
    }

    public ClassData findMethodClass(String methodName){
        return methodMap.get(methodName)!=null? this : null ;
    }

}

