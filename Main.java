import syntaxtree.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception {
        if(args.length < 1){
            System.err.println("Usage: java Main <inputFile1> ... <inputFileN>");
            System.exit(1);
        }

        FileInputStream fis = null;
        for(int i=0; i<args.length; i++){
            try{
                fis = new FileInputStream(args[i]);
                MiniJavaParser parser = new MiniJavaParser(fis);

                Goal root = parser.Goal();

                System.out.println("Program " + args[i] + " parsed successfully!");

                SymbolTable symbolTable = new SymbolTable();
                FirstVisitor eval1 = new FirstVisitor(symbolTable);

                try{
                    root.accept(eval1, null);
                }catch (TypeCheckingException e) {
                    System.out.print("At program " + args[i] + " the following error was encountered:\n\t");
                    System.out.println(e);
                    continue;
                }

                SecondVisitor eval2 = new SecondVisitor(symbolTable);
                try{
                    root.accept(eval2, null);
                }catch (TypeCheckingException e) {
                    System.out.print("At program " + args[i] + " the following error was encountered:\n\t");
                    System.out.println(e);
                    continue;
                }

                System.out.println("No type erros for program " + args[i] + " were found, offsets:");
                symbolTable.printOffsets();
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