import java.util.Map;
import java.util.HashMap;

public class SymbolTable {
    private Map<String, ClassData> classMap = new HashMap<String, ClassData>();

    public ClassData addClass(String className, ClassData parrentClassRef) throws Exception{
        ClassData newClassData;
        if(parrentClassRef == null){
            newClassData = new ClassData(className);
        }else{
            newClassData = new ExtendedClassData(className, parrentClassRef);
        }

        // Adding the class only if another with the same name has not been declared
        if( classMap.get(className) == null ){
            classMap.put(className, newClassData);
        }else{
            throw new Exception("Redeclaration of class!");
        }

        return newClassData;
    }

    public ClassData findClass(String className){
        return classMap.get(className);
    }

    public void printClasses(){
        for (Map.Entry<String,ClassData> entry : classMap.entrySet()){
            System.out.println("Class " + entry.getKey());
            entry.getValue().print();
        }
    }

    public String findVarType(ClassData classRef, MethodData methodRef, String varName){
        // First we check if we have a number or a boolean and nor a variable
        if( varName.matches("[0-9]+") || varName.equals("int") ){
            // if the varName has only numbers, it's not a variable but an int
            return "int";
        }else if( varName.equals("true") || varName.equals("false") || varName.equals("boolean") ){
            // if the varName is true or false, it's not a variable but a bool
            return "boolean";
        }

        // Otherwise we find the type of the variable
        String type;
        if( methodRef != null && ( (type = methodRef.findArngNVariable(varName)) != null )){
            return type;            
        }else if( (type = classRef.findVariable(varName)) != null ){
            return type;
        }

        return varName;
    }

}
