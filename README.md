

# MiniJava -> LLVM compiler

A *very simple* compiler that takes MiniJava code and generates the LLVM intermediate code of it, using visitor patterns

*This program was written as part of the compilers course at DIT – UoA*

## What is MiniJava though?

**In sort**: MiniJava is a subset of Java. It supports classes, fields *(which can only be “protected”)* and methods *(which can only be “public”)* but not global functions. Also, the basic types of MiniJava are limited to int, boolean and arrays of them *(int[], boolean[] respectively)*. Of course, the classes defined in MiniJava can contain fields of these basic types or of other classes. Lastly, MiniJava also supports inheritance but does **not** support function overloading.

The MiniJava BNF used in this project can be found [here]( https://cgi.di.uoa.gr/~compilers/project_files/minijava-new-2022/minijava.html) 

# Project compilation
In the source file directory, you will find a makefile capable of creating the needed *base* visitor files *(using the JavaCC and JTB jars)* and also compiling the compilers java source code to create the `Main` java executable *(which is the projects MiniJava compiler)*. 

To use the makefile just type `make` in a linux terminal in the source code directory. The only requirement is that you have the JDK installed *(the program was tested on openjdk 11.0.15 and oracle's JDK 8)*.

# MiniJava Compiler usage
As already mentioned, the MiniJava compiler is the `Main` java executable found in the source directory after executing the makefile. Its usage goes as follows:

*`java`* `Main [file1.java] [file2.java] ... [fileN.java]`

Here `[fileX.java]` are MiniJava source code files given as command line arguments. The compiler will perform type checking to the given files, and if the files do not have any type errors, the resulting `[fileX.ll]` source code files will get created in the directory from which the program got executed *(Source if you do not move the Main executable)*.

If you wish to disable the type checking functionality of the compiler, and simply create the .ll files use the `--no-type-checking` argument flag before the MiniJava input files
ex *`java Main --no-type-checking [file1.java] [file2.java] ... [fileN.java]`*. But note that disabling the type checking and giving an invalid MiniJava program as input will result in undefined behavior.

#### How to compile the `.ll` files to actual executables?
As already mentioned, this MiniJava compiler simply creates the LLVM IR of the given MiniJava programs which *in simple terms* need to be again "compiled" to actual machine code using the Clang compiler. On a linux machine with the Clang *(version 4.0.0 and greater)* compiler installed this can be done by typing `clang -o out1 [fileX.ll]` in the terminal with the resulting executable being named `out1`.
