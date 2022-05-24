import syntaxtree.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import visitor.GJDepthFirst;

public class LlvmVisitor extends GJDepthFirst<String, Void>{
    FileWriter llOutput;
    private SymbolTable symbolTable;
    private String resultVar,resultLlType;

    private List<String> argumentList = new ArrayList<String>();

    private void writeMethod(MethodData methodData, boolean firstItter, String className) throws IOException{
        int i;
        String argType;
        String tempString;
            
            // Writing return type
            tempString = "\n\ti8* bitcast (" + toLlType(methodData.getReturnType()) + " (i8*";
            if(!firstItter)
                tempString = ", " + tempString; // Adding comma if needed
            else
                firstItter=false;
            llOutput.write(tempString);

            // And type of args
            for(i=0; i < methodData.getArgsCount(); i++ ){
                argType = methodData.findNArng(i);
                llOutput.write("," + toLlType(argType) /*+ (((i+1) < methodData.getArgsCount()) ? ", " : ")*")*/);
            }

            // And name of method
            llOutput.write(")* @" + className + "." + methodData.getName() + " to i8*)");
    }

    private void writeMethods(ClassData classData, boolean firstItter, ClassData extendedClass) throws Exception{
        MethodData overideMethod;
        ClassData overideClass;
        for (Map.Entry<String,MethodData> methodEntry : classData.getMethodMap().entrySet()){
            if(!methodEntry.getValue().overrides())
                if( extendedClass == null ){
                    writeMethod(methodEntry.getValue(),firstItter,classData.name);
                }else{
                    overideMethod = extendedClass.findMethod(methodEntry.getKey());
                    overideClass = extendedClass.findMethodClass(methodEntry.getKey());
                    if(overideMethod!=null){
                        writeMethod(overideMethod,firstItter,overideClass.name);
                    }/*else /*if(methodEntry.getValue().overrides() == false)*//*{
                        writeMethod(methodEntry.getValue(),firstItter,classData.name);
                    }*/
                }
        }
    }

    LlvmVisitor(FileWriter givenLlOutput, SymbolTable givenSymbolTable) throws Exception{
        llOutput = givenLlOutput;
        symbolTable = givenSymbolTable;

        ClassData classData;
        int i, numberOfMethods;
        ArrayList<ClassData> parentsList;
        //Creating Vtable for each class
        for (Map.Entry<String,ClassData> classEntry : symbolTable.getClassMap().entrySet()){
            classData = classEntry.getValue();
            if(classData != symbolTable.getMainClassRef() ){
                // Vtables name, and number of methods
                parentsList = classData.getParents();
                numberOfMethods = classData.getNumberOfNonOverridingMethods();

                for (i = 0; i < parentsList.size(); i++){
                    numberOfMethods += parentsList.get(i).getNumberOfNonOverridingMethods();
                }

                llOutput.write("@." + classEntry.getKey() + "_Vtable = global [" + numberOfMethods + " x i8*] [" );


                for (i = 0; i < parentsList.size(); i++){
                    writeMethods(parentsList.get(i),true,classData);
                }
                writeMethods(classData,true,null);
                llOutput.write("\n                                ]\n\n");
            }else{
                llOutput.write("@." + classEntry.getKey() + "_Vtable = global [0 x i8*] []\n\n" );
            }
        }

        llOutput.write("declare i8* @calloc(i32, i32)\n" +
                       "declare i32 @printf(i8*, ...)\n" +
                       "declare void @exit(i32)\n\n" +
                       "@_cint = constant [4 x i8] c\"%d\\0a\\00\"\n" +
                       "@_cOOB = constant [15 x i8] c\"Out of bounds\\0a\\00\"\n" +
                       "define void @print_int(i32 %i) {\n" +
                       "\t%_str = bitcast [4 x i8]* @_cint to i8*\n" +
                       "\tcall i32 (i8*, ...) @printf(i8* %_str, i32 %i)\n" +
                       "\tret void\n}\n\n" +
                       "define void @throw_oob() {\n" +
                       "\t%_str = bitcast [15 x i8]* @_cOOB to i8*\n" +
                       "\tcall i32 (i8*, ...) @printf(i8* %_str)\n" +
                       "\tcall void @exit(i32 1)\n" +
                       "\tret void\n}\n\n");
    }

    private class LastVisited{
        public ClassData classRef = null;
        public MethodData method = null;
    }

    private LastVisited lastVisited = new LastVisited();

    private String toLlType(String type) {
        switch (type) {
            case "int":
                return "i32";
            case "boolean":
                return "i1";
            case "int[]":
                return "i32*";
            case "boolean[]":
                return "i8*";
            case "void":
                return "void";
            default:
                return "i8*";
        }
    }

    private boolean isBoolean(String type){
        return type.equals("boolean") || type.equals("true") || type.equals("false");
    }

    private boolean isInt(String type){
        return type.equals("int") || type.matches("[0-9]+");
    }

    private boolean isStaticValue(String value){
        return isBoolean(value) || isInt(value) || value.equals("int[]") || value.equals("boolean[]");
    }

   /**
    * f0 -> MainClass()
    * f1 -> ( TypeDeclaration() )*
    * f2 -> <EOF>
    */
    @Override
    public String visit(Goal n, Void argu) throws Exception {
        String _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> "public"
    * f4 -> "static"
    * f5 -> "void"
    * f6 -> "main"
    * f7 -> "("
    * f8 -> "String"
    * f9 -> "["
    * f10 -> "]"
    * f11 -> Identifier()
    * f12 -> ")"
    * f13 -> "{"
    * f14 -> ( VarDeclaration() )*
    * f15 -> ( Statement() )*
    * f16 -> "}"
    * f17 -> "}"
    */
    @Override
    public String visit(MainClass n, Void argu) throws Exception {
        String _ret=null;
        n.f0.accept(this, argu);
        String className = n.f1.accept(this, argu);
        lastVisited.classRef = symbolTable.findClass(className);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        lastVisited.method = lastVisited.classRef.findMethod("main");
        llOutput.write("define " + toLlType(lastVisited.method.getReturnType()) + " @" + lastVisited.classRef.getName() + "_main" + 
        " (i8* %this");
        String argType, argName;
        for(int i=0; i < lastVisited.method.getArgsCount(); i++ ){
            argType = lastVisited.method.findNArng(i);
            argName = lastVisited.method.findNArngName(i);
            llOutput.write(", " + toLlType(argType) + " %" + argName );
        }
        llOutput.write(") {\n");

        for (Map.Entry<String,String> entry : lastVisited.method.getVariables().entrySet()){
            llOutput.write("\t%" + entry.getKey() + " = alloca " + toLlType(entry.getValue()) + "\n" );
        }
        n.f7.accept(this, argu);
        n.f8.accept(this, argu);
        n.f9.accept(this, argu);
        n.f10.accept(this, argu);
        n.f11.accept(this, argu);
        n.f12.accept(this, argu);
        n.f13.accept(this, argu);
        n.f14.accept(this, argu);
        n.f15.accept(this, argu);
        n.f16.accept(this, argu);
        n.f17.accept(this, argu);

        lastVisited.classRef = null;
        lastVisited.method = null;
        llOutput.write("}\n\n");

        return _ret;
    }

    /**
     * f0 -> ClassDeclaration()
    *       | ClassExtendsDeclaration()
    */
    @Override
    public String visit(TypeDeclaration n, Void argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> ( VarDeclaration() )*
    * f4 -> ( MethodDeclaration() )*
    * f5 -> "}"
    */
    @Override
    public String visit(ClassDeclaration n, Void argu) throws Exception {
        String _ret=null;
        n.f0.accept(this, argu);
        String className = n.f1.accept(this, argu);
        lastVisited.classRef = symbolTable.findClass(className);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);

        lastVisited.classRef = null;

        return _ret;
    }

    /**
     * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "extends"
    * f3 -> Identifier()
    * f4 -> "{"
    * f5 -> ( VarDeclaration() )*
    * f6 -> ( MethodDeclaration() )*
    * f7 -> "}"
    */
    @Override
    public String visit(ClassExtendsDeclaration n, Void argu) throws Exception {
        String _ret=null;
        n.f0.accept(this, argu);
        String className = n.f1.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        lastVisited.classRef = symbolTable.findClass(className);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        n.f7.accept(this, argu);

        lastVisited.classRef = null;

        return _ret;
    }

    /**
     * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
    @Override
    public String visit(VarDeclaration n, Void argu) throws Exception {
        String _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "public"
    * f1 -> Type()
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( FormalParameterList() )?
    * f5 -> ")"
    * f6 -> "{"
    * f7 -> ( VarDeclaration() )*
    * f8 -> ( Statement() )*
    * f9 -> "return"
    * f10 -> Expression()
    * f11 -> ";"
    * f12 -> "}"
    */
    @Override
    public String visit(MethodDeclaration n, Void argu) throws Exception {
        String _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String methodName = n.f2.accept(this, argu);
        lastVisited.method = lastVisited.classRef.findMethod(methodName);
        llOutput.write("define " + toLlType(lastVisited.method.getReturnType()) + " @" + lastVisited.classRef.getName() + "." + methodName + 
        " (i8* %this");
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        // Writing args
        String argType, argName;
        for(int i=0; i < lastVisited.method.getArgsCount(); i++ ){
            argType = lastVisited.method.findNArng(i);
            argName = lastVisited.method.findNArngName(i);
            llOutput.write(", " + toLlType(argType) + " %." + argName );
        }
        llOutput.write(") {\n");
        n.f6.accept(this, argu);
        n.f7.accept(this, argu);
        // Allocating local vars for args
        for(int i=0; i < lastVisited.method.getArgsCount(); i++ ){
            argType = lastVisited.method.findNArng(i);
            argName = lastVisited.method.findNArngName(i);
            llOutput.write("\t%" + argName + " = alloca " + toLlType(argType) + "\n" +
                           "\tstore " + toLlType(argType) + " %." + argName + ", " + toLlType(argType) +  "* %" + argName + "\n");
        }
        llOutput.write("\n");

        // Allocating vars
        for (Map.Entry<String,String> entry : lastVisited.method.getVariables().entrySet()){
            llOutput.write("\t%" + entry.getKey() + " = alloca " + toLlType(entry.getValue()) + "\n" );
        }
        n.f8.accept(this, argu);
        n.f9.accept(this, argu);
        String returnVal = n.f10.accept(this, argu);

        // When returning a value there are 3 cases:

        if(lastVisited.method.findVariable(returnVal) != null){// Case 1, returning local val

            String retValLoaded = lastVisited.method.getNewVar();
            llOutput.write("\n\t" + retValLoaded + " = load " + toLlType(lastVisited.method.getReturnType()) + ", " + toLlType(lastVisited.method.getReturnType())  + "* %" + returnVal + "\n" + 
                           "\tret " + toLlType(lastVisited.method.getReturnType()) + " " + retValLoaded + "\n");

        }else if(lastVisited.classRef.findVariable(returnVal) != null){// Case 2, returning a class field

            String classFieldVarPrt = lastVisited.method.getNewVar(),
                   classFieldVarCasted = lastVisited.method.getNewVar(),
                   classFieldVarLoaded = lastVisited.method.getNewVar(),
                   LlType = toLlType(symbolTable.findVarType(lastVisited.classRef, /*here we have a class field*/ null, returnVal));
            int varOffset = lastVisited.classRef.findVariableOffset(returnVal)+8;

            llOutput.write("\n\t" + classFieldVarPrt + " = getelementptr i8, i8* %this, i32 " + varOffset + "\n" + 
                           "\t" + classFieldVarCasted + " = bitcast i8* " + classFieldVarPrt + " to " + LlType + "*\n" +
                           "\t" + classFieldVarLoaded + " = load " + LlType + ", " + LlType + "* " + classFieldVarCasted + "\n" +
                           "\tret " + toLlType(lastVisited.method.getReturnType()) + " " + classFieldVarLoaded + "\n");

        }else{ // Case 3, returning a static value

            if(returnVal.equals("true")){
                returnVal = "1";
            }else if(returnVal.equals("false")){
                returnVal = "0";
            }
            llOutput.write("\tret " + toLlType(lastVisited.method.getReturnType()) + " " + returnVal + "\n");
        }

        n.f11.accept(this, argu);
        n.f12.accept(this, argu);

        lastVisited.method = null;
        llOutput.write("}\n\n");
        return _ret;
    }

    /**
     * f0 -> FormalParameter()
    * f1 -> FormalParameterTail()
    */
    @Override
    public String visit(FormalParameterList n, Void argu) throws Exception {
        String _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Type()
    * f1 -> Identifier()
    */
    @Override
    public String visit(FormalParameter n, Void argu) throws Exception {
        String _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> ( FormalParameterTerm() )*
    */
    @Override
    public String visit(FormalParameterTail n, Void argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> ","
    * f1 -> FormalParameter()
    */
    @Override
    public String visit(FormalParameterTerm n, Void argu) throws Exception {
        String _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> ArrayType()
    *       | BooleanType()
    *       | IntegerType()
    *       | Identifier()
    */
    @Override
    public String visit(Type n, Void argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> BooleanArrayType()
    *       | IntegerArrayType()
    */
    @Override
    public String visit(ArrayType n, Void argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "boolean"
    * f1 -> "["
    * f2 -> "]"
    */
    @Override
    public String visit(BooleanArrayType n, Void argu) throws Exception {
        String _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "int"
    * f1 -> "["
    * f2 -> "]"
    */
    @Override
    public String visit(IntegerArrayType n, Void argu) throws Exception {
        String _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "boolean"
    */
    @Override
    public String visit(BooleanType n, Void argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "int"
    */
    @Override
    public String visit(IntegerType n, Void argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> Block()
    *       | AssignmentStatement()
    *       | ArrayAssignmentStatement()
    *       | IfStatement()
    *       | WhileStatement()
    *       | PrintStatement()
    */
    @Override
    public String visit(Statement n, Void argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "{"
    * f1 -> ( Statement() )*
    * f2 -> "}"
    */
    @Override
    public String visit(Block n, Void argu) throws Exception {
        String _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
    @Override
    public String visit(AssignmentStatement n, Void argu) throws Exception {
        String _ret=null;
        String var = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String expr = n.f2.accept(this, argu);
        if(lastVisited.classRef.findVariable(var)!=null && lastVisited.method!=null){
            String loadedVar, pointerToClassVar, castedClassVar,
                   exprType = symbolTable.findVarType(lastVisited.classRef, lastVisited.method, expr);
            int varOffset = lastVisited.classRef.findVariableOffset(var)+8;

            if(!isStaticValue(expr)){
                loadedVar = lastVisited.method.getNewVar();
                llOutput.write("\t" + loadedVar + " = load " + toLlType(exprType) + ", " + toLlType(exprType) + "* %" + expr + "\n");
            }else loadedVar = expr;

            pointerToClassVar = lastVisited.method.getNewVar();
            castedClassVar = lastVisited.method.getNewVar();
            llOutput.write("\t" + pointerToClassVar + " = getelementptr i8, i8* %this, i32 " + varOffset + "\n" +
                           "\t" + castedClassVar + " = bitcast i8* " + pointerToClassVar + " to " + toLlType(lastVisited.classRef.findVariable(var)) + "*\n");
            
            resultVar = loadedVar; resultLlType = toLlType(lastVisited.classRef.findVariable(var)); var = castedClassVar.substring(1);
            if(resultVar.equals("true")){
                resultVar = "1";
            }else if(resultVar.equals("false")){
                resultVar = "0";
            }
        }
        llOutput.write("\tstore " + resultLlType + " " + resultVar + ", " + resultLlType +  "* %" + var + "\n\n");
        resultVar = null; resultLlType = null;
        n.f3.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Identifier()
    * f1 -> "["
    * f2 -> Expression()
    * f3 -> "]"
    * f4 -> "="
    * f5 -> Expression()
    * f6 -> ";"
    */
    @Override
    public String visit(ArrayAssignmentStatement n, Void argu) throws Exception {
        String _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "if"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    * f5 -> "else"
    * f6 -> Statement()
    */
    @Override
    public String visit(IfStatement n, Void argu) throws Exception {
        String _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "while"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    */
    @Override
    public String visit(WhileStatement n, Void argu) throws Exception {
        String _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "System.out.println"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"
    */
    @Override
    public String visit(PrintStatement n, Void argu) throws Exception {
        String _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String expr = n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);

        String printIntArg;
        if(resultVar == null){
            printIntArg = expr;
        }else printIntArg = isStaticValue(expr)? resultVar : expr;
        llOutput.write("\tcall void (i32) @print_int(i32 " + printIntArg + ")\n\n");
        return _ret;
    }

    /**
     * f0 -> AndExpression()
    *       | CompareExpression()
    *       | PlusExpression()
    *       | MinusExpression()
    *       | TimesExpression()
    *       | ArrayLookup()
    *       | ArrayLength()
    *       | MessageSend()
    *       | Clause()
    */
    @Override
    public String visit(Expression n, Void argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> Clause()
    * f1 -> "&&"
    * f2 -> Clause()
    */
    @Override
    public String visit(AndExpression n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return "boolean";
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
    @Override
    public String visit(CompareExpression n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return "boolean";
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
    @Override
    public String visit(PlusExpression n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return "int";
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
    @Override
    public String visit(MinusExpression n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return "int";
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
    @Override
    public String visit(TimesExpression n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return "int";
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
    @Override
    public String visit(ArrayLookup n, Void argu) throws Exception {

        //String _ret=null;
        String type1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);

        type1 = symbolTable.findVarType(lastVisited.classRef, lastVisited.method, type1);

        if( type1.equals("int[]") ){
            return "int";
        }else if( type1.equals("boolean[]") ) {
            return "boolean";
        }else throw new Exception("invalid arrey type: " + type1);
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
    @Override
    public String visit(ArrayLength n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return "int";
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    */
    @Override
    public String visit(MessageSend n, Void argu) throws Exception {
        String callingObject = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String callingMethod = n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        argumentList.clear(); // Clearing the list before using it again
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);

        ClassData callingClassRef = symbolTable.findClass(symbolTable.findVarType(lastVisited.classRef, lastVisited.method, callingObject));
        if( callingClassRef == null ){
            throw new Exception("Class " + callingObject + " has not been declared!");
        }

        MethodData callingMethodRef = callingClassRef.findMethod(callingMethod);
        if( callingMethodRef == null ){
            throw new TypeCheckingException("Method " + callingMethod + " is not a member of " + callingClassRef.getName());
        }

        String callingObjectVar = lastVisited.method.getNewVar(),
               callingObjectCasted = lastVisited.method.getNewVar(),
               callingObjectLoaded = lastVisited.method.getNewVar(),
               callingObjectPrt = lastVisited.method.getNewVar(),
               callingMethodLoaded = lastVisited.method.getNewVar(),
               callingMethodCasted = lastVisited.method.getNewVar(),
               callingMethodReturnVar = lastVisited.method.getNewVar();

        llOutput.write("\n" +
                        "\t" + callingObjectVar + " = load i8*, i8** %" + callingObject + "\n" +
                        "\t" + callingObjectCasted + " = bitcast i8* " + callingObjectVar + " to i8***\n" +
                        "\t" + callingObjectLoaded + " = load i8**, i8*** " + callingObjectCasted + "\n" +
                        "\t" + callingObjectPrt + " = getelementptr i8*, i8** " + callingObjectLoaded + ", i32 " + callingMethodRef.getOffset()/8 + "\n" +
                        "\t" + callingMethodLoaded + " = load i8*, i8** " + callingObjectPrt + "\n" +
                        "\t" + callingMethodCasted + " = bitcast i8* " + callingMethodLoaded + " to " + toLlType(callingMethodRef.getReturnType()) + " (i8*");

        String argType, methodLLAgrs = "", methodLLAgrsNvalues = "";
        for(int i=0; i < callingMethodRef.getArgsCount(); i++ ){
            argType = callingMethodRef.findNArng(i);
            methodLLAgrs += ", " + toLlType(argType);
            methodLLAgrsNvalues += ", " + toLlType(argType) + " " + argumentList.get(i);
        }

        llOutput.write(methodLLAgrs + ")*\n" + 
                       "\t" + callingMethodReturnVar + " = call " + toLlType(callingMethodRef.getReturnType()) + " " + callingMethodCasted + "(i8* " + callingObjectVar + methodLLAgrsNvalues + ")\n\n"
        );

        resultVar = callingMethodReturnVar;
        resultLlType = toLlType(callingMethodRef.getReturnType());

        return callingMethodRef.getReturnType();
    }

    /**
     * f0 -> Expression()
    * f1 -> ExpressionTail()
    */
    @Override
    public String visit(ExpressionList n, Void argu) throws Exception {
        String _ret=null;
        String arg = n.f0.accept(this, argu);
        argumentList.add(arg);
        n.f1.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> ( ExpressionTerm() )*
    */
    @Override
    public String visit(ExpressionTail n, Void argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> ","
    * f1 -> Expression()
    */
    @Override
    public String visit(ExpressionTerm n, Void argu) throws Exception {
        String _ret=null;
        n.f0.accept(this, argu);
        String arg = n.f1.accept(this, argu);
        argumentList.add(arg);
        return _ret;
    }

    /**
     * f0 -> NotExpression()
    *       | PrimaryExpression()
    */
    @Override
    public String visit(Clause n, Void argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> IntegerLiteral()
    *       | TrueLiteral()
    *       | FalseLiteral()
    *       | Identifier()
    *       | ThisExpression()
    *       | ArrayAllocationExpression()
    *       | AllocationExpression()
    *       | BracketExpression()
    */
    @Override
    public String visit(PrimaryExpression n, Void argu) throws Exception {
        String _f0 = n.f0.accept(this, argu);
        if ( _f0.equals("this") ) return "this";
        return _f0;
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    @Override
    public String visit(IntegerLiteral n, Void argu) throws Exception {
        return n.f0.toString();//.accept(this, argu);
    }

    /**
     * f0 -> "true"
     */
    @Override
    public String visit(TrueLiteral n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        return "true";
    }

    /**
     * f0 -> "false"
     */
    @Override
    public String visit(FalseLiteral n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        return "false";
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    @Override
    public String visit(Identifier n, Void argu) throws Exception {
        return n.f0.toString();
    }

    /**
     * f0 -> "this"
     */
    @Override
    public String visit(ThisExpression n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        return lastVisited.classRef.getName();
    }

    /**
     * f0 -> BooleanArrayAllocationExpression()
    *       | IntegerArrayAllocationExpression()
    */
    @Override
    public String visit(ArrayAllocationExpression n, Void argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "new"
    * f1 -> "boolean"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
    @Override
    public String visit(BooleanArrayAllocationExpression n, Void argu) throws Exception {
        //String _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return "boolean[]";//return _ret;
    }

    /**
     * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
    @Override
    public String visit(IntegerArrayAllocationExpression n, Void argu) throws Exception {
        //String _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return "int[]";//return _ret;
    }

    /**
     * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
    @Override
    public String visit(AllocationExpression n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        String className = n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);

        ClassData classRef = symbolTable.findClass(className);
        if(classRef == null) throw new Exception("Class " + classRef + " does not exist");
        String objectVar = lastVisited.method.getNewVar(),
               castedObjectVar = lastVisited.method.getNewVar(),
               VtablePointer = lastVisited.method.getNewVar();

        int objectSize = classRef.getVariableOffset() + 8;

        llOutput.write("\n" + 
            "\t" + objectVar + " = call i8* @calloc(i32 1, i32 " + objectSize + ")\n" + 
            "\t" + castedObjectVar + " = bitcast i8* " + objectVar + " to i8***\n" +
            "\t" + VtablePointer + " = getelementptr [" + classRef.getNumberOfNonOverridingMethods() + " x i8*], " + 
            "[" + classRef.getNumberOfNonOverridingMethods() + " x i8*]* @." + className + "_Vtable, i32 0, i32 0\n" +
            "\tstore i8** " + VtablePointer + ", i8*** " + castedObjectVar + "\n\n"
        );

        resultVar = objectVar;
        resultLlType = "i8*";

        return className;
    }

    /**
     * f0 -> "!"
    * f1 -> Clause()
    */
    @Override
    public String visit(NotExpression n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        String clauseType = n.f1.accept(this, argu);
        //clauseType = symbolTable.findVarType(lastVisited.classRef,lastVisited.method,clauseType);
        return clauseType;
    }

    /**
     * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
    @Override
    public String visit(BracketExpression n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        String expression = n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return expression;
    }

    /*public void close() throws IOException{
        llOutput.close();
    }*/
}
