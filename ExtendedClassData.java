import java.util.Map;

// It has all the functionality a SimpleClassData, plus the extend functionality
public class ExtendedClassData extends ClassData {
    private ClassData parrentClassRef;

    ExtendedClassData(ClassData givenParrentClassRef){
        parrentClassRef = givenParrentClassRef;
        currentMethodOffset = givenParrentClassRef.getMethodOffset();
        currentVariableOffset = givenParrentClassRef.getVariableOffset();
    }

    @Override
    public MethodData addMethod(String methodName, String returnType) throws Exception{
        MethodData parrentClassMethod = parrentClassRef.findMethod(methodName);
        if( parrentClassMethod ==  null){
            // if this method does not override any of the parents methods, we can call the simple addMethod.
            return super.addMethod(methodName, returnType);
        }
        // Otherwise we have to set the offset of the function based on the parrent offset
        MethodData newMethodData = new MethodData(methodName,returnType,parrentClassMethod.getOffset());

        // Adding the method only if another with the same name has not been declared
        if( methodMap.get(methodName) == null ){
            methodMap.put(methodName, newMethodData);
        }else{
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
                    System.out.println("On function: " + entry.getKey());
                    throw new Exception("Overloading not allowed!");
                    //return false;
                }
            }
        }
        return true;
    }

}
