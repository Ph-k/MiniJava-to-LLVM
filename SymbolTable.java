import java.util.Map;
import java.util.LinkedHashMap;

// The symbol tables has all the class objects of the program saved, which in turn have the method classes and variables
public class SymbolTable {
    private Map<String, ClassData> classMap = new LinkedHashMap<String, ClassData>();
    private ClassData mainClassRef;

    public ClassData addClass(String className, ClassData parrentClassRef) throws Exception{
        ClassData newClassData;
        if(parrentClassRef == null){
            newClassData = new ClassData(className);
        }else{
            // If the class exetnds, we use an ExtendedClassData obejct
            newClassData = new ExtendedClassData(className, parrentClassRef, mainClassRef == parrentClassRef);
        }

        // Adding the class only if another with the same name has not been declared
        if( classMap.get(className) == null ){
            classMap.put(className, newClassData);
        }else{
            throw new TypeCheckingException("Redeclaration of class!");
        }

        return newClassData;
    }

    // Used to set a reference to the main class of the program, which needs special treatments in some places
    public void setMainClass(ClassData givenMainClassRef){
        mainClassRef = givenMainClassRef;
    }

    public ClassData findClass(String className){
        return classMap.get(className);
    }

    // Method to print the data of the class in DETAIL
    public void printClasses(){
        for (Map.Entry<String,ClassData> entry : classMap.entrySet()){
            System.out.println("Class " + entry.getKey());
            entry.getValue().print();
        }
    }

    // Method to print the data of the class as show in the examples of the project
    public void printOffsets(){
        for (Map.Entry<String,ClassData> entry : classMap.entrySet()){
            if( mainClassRef != entry.getValue() ){
                System.out.println("-----------Class " + entry.getKey()+"-----------");
                entry.getValue().printOffsets();
            }
        }
    }

    public String findVarType(ClassData classRef, MethodData methodRef, String varName){
        // First we check if we have a number or a boolean and thus, not a variable
        if( varName.matches("[0-9]+") || varName.equals("int") ){
            // if the varName has only numbers, it's not a variable but an int
            return "int";
        }else if( varName.equals("true") || varName.equals("false") || varName.equals("boolean") ){
            // if the varName is true or false, it's not a variable but a bool
            return "boolean";
        }/*else if( varName.equals("boolean[]") || varName.equals("int[]") ){ array types checked are checked differently
            // array type
            return varName;
        }*/

        // If we were actually given a variable, we find the type of it
        String type;
        if( methodRef != null && ( (type = methodRef.findArngNVariable(varName)) != null )){
            // If the function was called for a method, and the method has that variable
            return type;            
        }else if( (type = classRef.findVariable(varName)) != null ){
            // If the function was not called from a method, or the method did not have that variable, we check the variables of the current class
            return type;
        }

        return varName;
    }

    // Checks if a type is valid, including object reference "types"
    public boolean typeExists(String type){
        return type.equals("int") || type.equals("boolean") || type.equals("boolean[]") || type.equals("int[]") || ( classMap.get(type) != null );
    }

    // Given two types checks if they are the same
    public boolean typeEquality(String type1,String type2){
        ClassData classRef;
        if( type1.equals(type2) ){
            // When a type is one of the simple ones. ex int, boolean, [], the awnser is trivial
            return true;
        } else {
            // But if we have objects, we also need to check for the parrent classes, becuase the objects may have the same parrent, or be of parrent type
            classRef = classMap.get(type2);

            // Note, we only have to check if type1 is of parrent type, since if A extends B, and B b, b can be of type A but not the inverse
            if( classRef!=null)
                return classRef.checkParrentType(type1);

        }
        // In any other case the types were not equal
        return false;
    }

}
