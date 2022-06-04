import syntaxtree.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception {
        if(args.length < 1){
            System.err.println("Usage: java Main <inputFile1> ... <inputFileN>");
            System.exit(1);
        }

        boolean performTypeCheck = true;

        for(int i=0; i<args.length; i++){
                if( args[i].equals("--no-type-checking") ){
                    performTypeCheck = false;
                    break;
                }
        }

        FileInputStream fis = null;
        // For all the given inputs
        for(int i=0; i<args.length; i++){
            if( args[i].equals("--no-type-checking") == false ){
                try{
                    System.out.println("\n__________" + args[i] + "__________\n");
                    fis = new FileInputStream(args[i]);
                    // First we parse the input
                    MiniJavaParser parser = new MiniJavaParser(fis);

                    Goal root = parser.Goal();

                    System.out.println("Program " + args[i] + " parsed successfully!");


                    // Then we perform some first-level checking and fill the symbol table using the first visitor
                    SymbolTable symbolTable = new SymbolTable();
                    FirstVisitor eval1 = new FirstVisitor(symbolTable);

                    try{
                        root.accept(eval1, null);
                    }catch (TypeCheckingException e) {
                        // We catch any exceptions about an error of the file, we print what is wrong with it
                        System.out.print("At program " + args[i] + " the following error was encountered:\n\t");
                        System.out.println(e);
                        // And we continue to the next file
                        continue;
                    }

                    if( performTypeCheck ){
                        // The same way as above, but now we use the second visitor to perform type checking
                        SecondVisitor eval2 = new SecondVisitor(symbolTable);
                        try{
                            root.accept(eval2, null);
                        }catch (TypeCheckingException e) {
                            System.out.print("At program " + args[i] + " the following error was encountered:\n\t");
                            System.out.println(e);
                            continue;
                        }
                    }

                    /* We do not have to show the offsets at this assigment */
                    /*System.out.println("No type erros for program " + args[i] + " were found, offsets:");
                    symbolTable.printOffsets();*/

                    String basename = args[i].substring(args[i].lastIndexOf("/") + 1);

                    FileWriter llOuput = new FileWriter(basename.split(".java")[0] + ".ll");

                    LlvmVisitor llvmVisitor = new LlvmVisitor(llOuput,symbolTable);
                    root.accept(llvmVisitor, null);

                    llOuput.close();

                }
                catch(ParseException ex){
                    System.out.print("At program " + args[i] + " the following parsing error was encountered:\n\t");
                    System.out.println(ex.getMessage());
                }
                catch(FileNotFoundException ex){
                    System.err.println(ex.getMessage());
                }
                finally{
                    try{
                        if(fis != null) fis.close();
                    }
                    catch(IOException ex){
                        System.err.println(ex.getMessage());
                    }
                }
            }
        }
    }
}