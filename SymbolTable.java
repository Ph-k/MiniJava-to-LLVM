import java.util.Map;
import java.util.HashMap;

public class SymbolTable {
    private Map<String, ClassInfo> classMap = new HashMap<String, ClassInfo>();

    public boolean addClass(String className){
        return classMap.put(className, new ClassInfo()) == null ? true : false ;
    }

    public ClassInfo findClass(String className){
        return classMap.get(className);
    }

    public void printClasses(){
        for (Map.Entry<String,ClassInfo> entry : classMap.entrySet()){
            System.out.println("Class = " + entry.getKey());
        }
    }

}
