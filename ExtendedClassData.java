import java.util.Map;

// It has all the functionality a SimpleClassData, plus the extend functionality
public class ExtendedClassData extends ClassData {
    private ClassData parrentClassRef;

    ExtendedClassData(String className, ClassData givenParrentClassRef, boolean ExtendsMain){
        super(className);
        parrentClassRef = givenParrentClassRef;
        currentMethodOffset = ( ExtendsMain == false ? givenParrentClassRef.getMethodOffset() : 0 );
        currentVariableOffset = givenParrentClassRef.getVariableOffset();
    }

    public MethodData findMethod(String methodName){
        MethodData method = methodMap.get(methodName);
        // Searching to parrent if the method does not exist in child
        if(method != null){
            return method;
        }else
            return parrentClassRef.findMethod(methodName);
    }

    public String findVariable(String variableName){
        tupleTypeOffset typeNoffset = variableMap.get(variableName);
        // Searching to parrent if the method does not exist in child
        if( typeNoffset != null){ 
            return typeNoffset.variableType;
        }else
            return parrentClassRef.findVariable(variableName);
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
            throw new Exception("Redeclaration of method!");
        }

        return newMethodData;
    }

    @Override
    public boolean methodsCheck() throws Exception{
        MethodData parrentMethod;
        for (Map.Entry<String,MethodData> entry : methodMap.entrySet()){
            parrentMethod = parrentClassRef.methodMap.get(entry.getKey());
            if(  parrentMethod != null ){
                if( entry.getValue().argsEquals(parrentMethod) == false){
                    System.out.println("On method: " + entry.getKey());
                    throw new Exception("Overloading not allowed (different arguments)!");
                    //return false;
                }else if ( entry.getValue().getReturnType().equals(parrentMethod.getReturnType()) == false){
                    System.out.println("On method: " + entry.getKey());
                    throw new Exception("Overloading not allowed (different return type)!");
                }
            }
        }
        return true;
    }

    public boolean checkParrentType(String type){
        return name.equals(type) || parrentClassRef.checkParrentType(type);
    }

}
