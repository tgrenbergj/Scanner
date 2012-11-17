all: build

run: Runner.class
	java Runner input/test_spec.txt input/test_input.txt

build: Runner.class

Runner.class: DFA.java DFAWalker.java FiniteAutomata.java NFA.java NFAConverter.java NFARunner.java NFASimulator.java NFATools.java RecursiveDescentParser.java Runner.java SpecificationReader.java
	javac *.java

clean:
	rm -rf *.class project.phase1.jgrebner3.tar.gz
	
tar: DFA.java DFAWalker.java FiniteAutomata.java NFA.java NFAConverter.java NFARunner.java NFASimulator.java NFATools.java RecursiveDescentParser.java Runner.java SpecificationReader.java
	tar cvzf project.phase1.jgrebner3.tar.gz *.java Makefile README *.sh input output
