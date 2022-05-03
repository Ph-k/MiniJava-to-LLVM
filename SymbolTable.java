import java.util.Map;
import java.util.HashMap;

public class SymbolTable {
    private Map<String, ClassData> classMap = new HashMap<String, ClassData>();

    public ClassData addClass(String className){
        ClassData newClassData = new ClassData();
        classMap.put(className, newClassData)/* == null ? true : false*/;
        return newClassData;
    }

    public ClassData findClass(String className){
        return classMap.get(className);
    }

    public void printClasses(){
        for (Map.Entry<String,ClassData> entry : classMap.entrySet()){
            System.out.println(entry.getKey());
            entry.getValue().print();
        }
    }

}
