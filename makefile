all: compile

compile:
	java -jar ../jtb132di.jar -te minijava.jj
	java -jar ../javacc5.jar minijava-jtb.jj
	javac Main.java
clean:
	find . -type f -not \( -name 'Main.java' -or -name 'minijava.jj' -or -name 'MyVisitor.java' -or -name 'Example.java' -or -name 'makefile' \) -delete
	rmdir syntaxtree visitor
#rm -f *.class *~
#rm -rfv !("Main.java"|"minijava.jj"|"MyVisitor.java"|"makefile"|"Example.java")
