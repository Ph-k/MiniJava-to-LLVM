all: compile

compile:
	java -jar ../jars/jtb132di.jar -te minijava.jj
	java -jar ../jars/javacc5.jar minijava-jtb.jj
	javac Main.java ClassData.java ExtendedClassData.java FirstVisitor.java MethodData.java SecondVisitor.java SymbolTable.java TypeCheckingException.java LlvmVisitor.java

clean:
	rm -f *.class *~

