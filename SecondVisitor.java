
import syntaxtree.*;
import java.util.List;
import java.util.ArrayList;
import visitor.GJDepthFirst;

public class SecondVisitor extends GJDepthFirst<String, Void>{
    private SymbolTable symbolTable;
    public SecondVisitor(SymbolTable givenSymbolTable){
        symbolTable = givenSymbolTable;
    }

    private class LastVisited{
        public ClassData classRef = null;
        public MethodData method = null;
    }

    private LastVisited lastVisited = new LastVisited();

    private List<String> argumentList = new ArrayList<String>();

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

    private boolean isBoolean(String type){
        return type.equals("boolean") || type.equals("true") || type.equals("false");
    }

    private boolean isInt(String type){
        return type.equals("int") || type.matches("[0-9]+");
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
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        lastVisited.method = lastVisited.classRef.findMethod("main");
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
        String varType = n.f0.accept(this, argu);
        String varName = n.f1.accept(this, argu);
        n.f2.accept(this, argu);

        if( lastVisited.method != null ){
            // this is a method var
        }else{
            // this is a class var
        }

        if(symbolTable.typeExists(varType) == false){
            throw new Exception("At class: " + lastVisited.classRef.getName() +
            (   lastVisited.method != null?
                ". At method: " + lastVisited.method.getName() :
                ""
            )
            + ". Type " + varType + " does not exist!"
            );
        }

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
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        n.f7.accept(this, argu);
        n.f8.accept(this, argu);
        n.f9.accept(this, argu);
        String returnType = n.f10.accept(this, argu);
        n.f11.accept(this, argu);
        n.f12.accept(this, argu);

       
        if(!symbolTable.typeEquality(
            symbolTable.findVarType(lastVisited.classRef, lastVisited.method, returnType),
            lastVisited.method.getReturnType()
            )
        ){
            throw new Exception("Invalid return type. Method: " + lastVisited.method.getName() + " returns " + lastVisited.method.getReturnType() + " but " + returnType + " was given");
        }
        lastVisited.method = null;
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
        String argType = n.f0.accept(this, argu);
        String argName = n.f1.accept(this, argu);
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
        //String _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return "boolean[]";
        //return _ret;
    }

    /**
     * f0 -> "int"
    * f1 -> "["
    * f2 -> "]"
     */
    @Override
    public String visit(IntegerArrayType n, Void argu) throws Exception {
        //String _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return "int[]";
        //return _ret;
    }

    /**
     * f0 -> "boolean"
     */
    @Override
    public String visit(BooleanType n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        return "boolean";
    }

    /**
     * f0 -> "int"
     */
    @Override
    public String visit(IntegerType n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        return "int";
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
        String type1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String type2 =  n.f2.accept(this, argu);
        n.f3.accept(this, argu);

        String t1=type1,t2=type2;
        type1 = symbolTable.findVarType(lastVisited.classRef, lastVisited.method, type1);
        type2 = symbolTable.findVarType(lastVisited.classRef, lastVisited.method, type2);
        
        if(!symbolTable.typeEquality(type1, type2) ){
            throw new Exception("Cannot cast " + type1 + " to " + type2);
        }
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
        String expressionType = n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);

        // To find the type of a variable
        expressionType = symbolTable.findVarType(lastVisited.classRef, lastVisited.method, expressionType);
        if( !isBoolean(expressionType) ){
            throw new Exception("Expression of while loop cannot be of type: " + expressionType + " it must be boolean");
        }
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
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
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
        //String _ret=null;
        String type1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String type2 = n.f2.accept(this, argu);
        type1 = symbolTable.findVarType(lastVisited.classRef, lastVisited.method, type1);
        type2 = symbolTable.findVarType(lastVisited.classRef, lastVisited.method, type2);
        if( !isBoolean(type1) || !isBoolean(type2) ){
            throw new Exception("Cannot compare " + type1 + " with " + type2 + " only boolean and boolean is allowed");
        }
        return "boolean";
        //return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(CompareExpression n, Void argu) throws Exception {
        //String _ret=null;
        String type1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String type2 = n.f2.accept(this, argu);
        type1 = symbolTable.findVarType(lastVisited.classRef, lastVisited.method, type1);
        type2 = symbolTable.findVarType(lastVisited.classRef, lastVisited.method, type2);
        if( !isInt(type1) || !isInt(type2) ){
            throw new Exception("Cannot compare " + type1 + " with " + type2 + " only int and int is allowed");
        }
        return "boolean";
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(PlusExpression n, Void argu) throws Exception {
        //String _ret=null;
        String type1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String type2 = n.f2.accept(this, argu);
        type1 = symbolTable.findVarType(lastVisited.classRef, lastVisited.method, type1);
        type2 = symbolTable.findVarType(lastVisited.classRef, lastVisited.method, type2);
        if( !isInt(type1) || !isInt(type2) ){
            throw new Exception("Cannot compare " + type1 + " with " + type2 + " only int and int is allowed");
        }
        return "int";
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(MinusExpression n, Void argu) throws Exception {
        //String _ret=null;
        String type1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String type2 = n.f2.accept(this, argu);
        type1 = symbolTable.findVarType(lastVisited.classRef, lastVisited.method, type1);
        type2 = symbolTable.findVarType(lastVisited.classRef, lastVisited.method, type2);
        if( !isInt(type1) || !isInt(type2) ){
            throw new Exception("Cannot compare " + type1 + " with " + type2 + " only int and int is allowed");
        }
        return "int";
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(TimesExpression n, Void argu) throws Exception {
        //String _ret=null;
        String type1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String type2 = n.f2.accept(this, argu);
        type1 = symbolTable.findVarType(lastVisited.classRef, lastVisited.method, type1);
        type2 = symbolTable.findVarType(lastVisited.classRef, lastVisited.method, type2);
        if( !isInt(type1) || !isInt(type2) ){
            throw new Exception("Cannot compare " + type1 + " with " + type2 + " only int and int is allowed");
        }
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
        String indexType = n.f2.accept(this, argu);
        type1 = symbolTable.findVarType(lastVisited.classRef, lastVisited.method, type1);
        indexType = symbolTable.findVarType(lastVisited.classRef, lastVisited.method, indexType);
        n.f3.accept(this, argu);
        if( !isInt(indexType) ){
            throw new Exception("Index cannot be " + indexType + ", only int allowed!");
        }

        type1 = symbolTable.findVarType(lastVisited.classRef, lastVisited.method, type1);

        if( (!type1.equals("int[]")) && !(type1.equals("boolean[]")) ){
            throw new Exception("Array can not be " + indexType + " must be int[] or boolean[]");
        }

        if( type1.equals("int[]") ){
            return "int";
        }else /*if( type1.equals("boolean[]") ) because of the above ifs, it can not be anything else*/{
            return "boolean";
        }
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
     */
    @Override
    public String visit(ArrayLength n, Void argu) throws Exception {
        //String _ret=null;
        String type1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);

        type1 = symbolTable.findVarType(lastVisited.classRef, lastVisited.method, type1);

        if( (!type1.equals("int[]")) && !(type1.equals("boolean[]")) ){
            throw new Exception("Array can not be " + type1 + " must be int[] or boolean[]");
        }

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
        //String _ret=null;
        String callingClassName =  n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String callingMethodName = n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        argumentList.clear(); // Clearing the list before using it again
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);

        // To find the class name from object
        callingClassName = symbolTable.findVarType(lastVisited.classRef, lastVisited.method, callingClassName);

        ClassData callingClassRef = symbolTable.findClass(callingClassName);
        if( callingClassRef == null ){
            throw new Exception("Class " + callingClassName + " has not been declared!");
        }


        MethodData callingMethodRef = callingClassRef.findMethod(callingMethodName);
        if( callingMethodRef == null ){
            throw new Exception("Method " + callingMethodName + " is not a member of " + callingClassRef.getName());
        }

        String argType;
        int i = 0;
        if( argumentList.size() != callingMethodRef.getArgsCount() ){
            throw new Exception("For method: " + callingClassRef.getName() + '.' + callingMethodRef.getName() + " was expecting " + callingMethodRef.getArgsCount() + " arguments, insteed I got " + argumentList.size());
        }else{
            for (String arg : argumentList) {
                argType = symbolTable.findVarType(lastVisited.classRef, lastVisited.method, arg);
                if(! symbolTable.typeEquality( callingMethodRef.findNArng(i++), argType) ){
                    throw new Exception("Parameter " + String.valueOf(i) + " for " + callingMethodName + " is " + argType + ". Was expecting " + callingMethodRef.findNArng(i-1));
                }
            }
        } 

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
        String allocationType = n.f3.accept(this, argu);
        n.f4.accept(this, argu);

        allocationType = symbolTable.findVarType(lastVisited.classRef, lastVisited.method, allocationType);
        if( !isInt(allocationType) ){
            throw new Exception("Array allocator must be int. Insted it is: " + allocationType);
        }
        return "boolean[]";
        //return _ret;
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
        String allocationType = n.f3.accept(this, argu);
        n.f4.accept(this, argu);

        allocationType = symbolTable.findVarType(lastVisited.classRef, lastVisited.method, allocationType);
        if( !isInt(allocationType) ){
            throw new Exception("Array allocator must be int. Insted it is: " + allocationType);
        }
        return "int[]";
        //return _ret;
    }

    /**
     * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
     */
    @Override
    public String visit(AllocationExpression n, Void argu) throws Exception {
        //String _ret=null;
        n.f0.accept(this, argu);
        String identifier = n.f1.accept(this, argu);
        if(symbolTable.typeExists(identifier) == false){
            throw new Exception("At class: " + lastVisited.classRef.getName() +
            (   lastVisited.method != null?
                ". At method: " + lastVisited.method.getName() :
                ""
            )
            + ". Type " + identifier + " does not exist!"
            );
        }
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        return identifier;
        //return _ret;
    }

    /**
     * f0 -> "!"
    * f1 -> Clause()
     */
    @Override
    public String visit(NotExpression n, Void argu) throws Exception {
        //String _ret=null;
        n.f0.accept(this, argu);
        String clauseType = n.f1.accept(this, argu);
        clauseType = symbolTable.findVarType(lastVisited.classRef,lastVisited.method,clauseType);
        if( !isBoolean(clauseType) ){
            throw new Exception("Logical operator !, expects booleans. " + clauseType + " is not allowed.");
        }
        return clauseType;
    }

    /**
     * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
     */
    @Override
    public String visit(BracketExpression n, Void argu) throws Exception {
        //String _ret=null;
        n.f0.accept(this, argu);
        String expression = n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return expression;
        //return _ret;
    }

}
