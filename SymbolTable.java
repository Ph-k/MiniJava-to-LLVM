import java.util.Map;
import java.util.HashMap;

public class SymbolTable {
    private Map<String, ClassData> classMap = new HashMap<String, ClassData>();

    public ClassData addClass(String className, ClassData parrentClassRef) throws Exception{
        ClassData newClassData;
        if(parrentClassRef == null){
            newClassData = new ClassData();
        }else{
            newClassData = new ExtendedClassData(parrentClassRef);
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
}
