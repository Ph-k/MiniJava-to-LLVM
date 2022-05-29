import java.util.ArrayList;
import java.util.Map;

// It has the same functionality as the SimpleClassData
// But it overloads some of it's methods to add some functionality needed when a class extends
public class ExtendedClassData extends ClassData {
    // In addition to all the data held by the ClassData about the class, here we also need to save a referance to the parrent class
    private ClassData parrentClassRef;

    // This constructor create a ClassData, but in addition to the simple ClassData constructor, it sets the offsets given the offsets of the parrent class
    ExtendedClassData(String className, ClassData givenParrentClassRef, boolean ExtendsMain){
        super(className);
        parrentClassRef = givenParrentClassRef;
        // The method offsets are set after the parrents only if the class does NOT extend the main class, otherwise the offsets shall start from 0 again
        currentMethodOffset = ( ExtendsMain == false ? givenParrentClassRef.getMethodOffset() : 0 );
        currentVariableOffset = givenParrentClassRef.getVariableOffset();
    }

    @Override
    public MethodData findMethod(String methodName){
        MethodData method = methodMap.get(methodName);
        if(method != null){
            return method;
        }else{
            // If the requested method was not found, maybe a parrent class has it
            return parrentClassRef.findMethod(methodName);
        }
    }

    @Override
    public ClassData findMethodClass(String methodName){
        if(methodMap.get(methodName) != null){
            return this;
        }else{
            // If the requested method was not found, maybe a parrent class has it
            return parrentClassRef.findMethodClass(methodName);
        }
    }

    @Override
    public String findVariable(String variableName){
        tupleTypeOffset typeNoffset = variableMap.get(variableName);
        if( typeNoffset != null){ 
            return typeNoffset.variableType;
        }else{
            // If the requested variable was not found, mayby a parrent class has it
            return parrentClassRef.findVariable(variableName);
        }
    }

    @Override
    public int findVariableOffset(String variableName){
        tupleTypeOffset typeNoffset = variableMap.get(variableName);
        if( typeNoffset != null){ 
            return typeNoffset.offset;
        }else{
            // If the requested variable was not found, mayby a parrent class has it
            return parrentClassRef.findVariableOffset(variableName);
        }
    }

    @Override
    public MethodData addMethod(String methodName, String returnType) throws Exception{
        MethodData parrentClassMethod = parrentClassRef.findMethod(methodName);
        if( parrentClassMethod ==  null){
            // if this method does not override any of the parents methods, we can call the simple addMethod.
            return super.addMethod(methodName, returnType);
        }
        // Otherwise we have to set the offset of the function based on the parrent offset
        MethodData newMethodData = new MethodData(methodName,returnType,parrentClassMethod.getOffset(),true);

        // Adding the method only if another with the same name has not been declared
        if( methodMap.get(methodName) == null ){
            methodMap.put(methodName, newMethodData);
        }else{
            System.out.println("For method: " + methodName);
            throw new TypeCheckingException("Redeclaration of method!");
        }

        return newMethodData;
    }

    @Override
    public int getNumberOfNonOverridingMethods(boolean checkParrent) {
        int s=0;
        for (Map.Entry<String,MethodData> entry : methodMap.entrySet()){
            if(!entry.getValue().overrides())
                s++;
        }
        if(checkParrent)
            return s + parrentClassRef.getNumberOfNonOverridingMethods(true);
        else
            return s;
    }

    // This methods checks if all the overridings function are valid, since no overloading is allowed
    @Override
    public boolean methodsCheck() throws Exception{
        MethodData parrentMethod;
        for (Map.Entry<String,MethodData> entry : methodMap.entrySet()){
            parrentMethod = parrentClassRef.methodMap.get(entry.getKey());
            if(  parrentMethod != null ){
                if( entry.getValue().argsEquals(parrentMethod) == false){
                    // Frist checking the number, order, and types of argumetns
                    System.out.println("On method: " + entry.getKey());
                    throw new TypeCheckingException("Overloading not allowed (different arguments)!");
                }else if ( entry.getValue().getReturnType().equals(parrentMethod.getReturnType()) == false){
                    // Then we check the return type
                    System.out.println("On method: " + entry.getKey());
                    throw new TypeCheckingException("Overloading not allowed (different return type)!");
                }
            }
        }
        return true;
    }

    // Given the name of class, the function checks if this class is dirived from the given
    // (Used to check a type of an object which inherits)
    public boolean checkParrentType(String type){
        return name.equals(type) || parrentClassRef.checkParrentType(type);
    }

    public ArrayList<ClassData> getParents(){
        ArrayList<ClassData> parentsList = new ArrayList<ClassData>();
        parrentClassRef.addParents(parentsList);
        return parentsList;
    }

    protected void addParents(ArrayList<ClassData> parentsList){
        parrentClassRef.addParents(parentsList);
        parentsList.add(this);
    }
}
