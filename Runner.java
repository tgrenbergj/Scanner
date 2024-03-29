import java.io.*;

/**
 * A simple runner class that takes in the spec file, runs the recursive descent
 * parser to produce an NFA, converts this NFA to a DFA, and then runs the 
 * table walker on the DFA.  This will output the entire DFA table, along
 * with the output of the table walker.
 * 
 * Usage: java Runner spec_file input_file
 */
public class Runner {

	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("Not enough arguments.");
			System.err.println("Usage: java Runner spec_file input_file");
			System.exit(1);
		}
		
		String spec = args[0];
		String input = args[1];
		
		try {
			SpecificationReader sr = new SpecificationReader("src\\input_phase2\\minire_spec.txt");
			NFA nfa = sr.run();
			DFA dfa = NFAConverter.NFAtoDFA(nfa);
			System.out.println("DFA Table:");
			//System.out.println(dfa);
			System.out.println();
			System.out.println();
			System.out.println("Table Walker Output:");
			System.out.println();
			File file = new File("src\\input_phase2\\minire_input.txt");
			DFAWalker walker = new DFAWalker(file, dfa);
			Token token = walker.nextToken();
			while (!token.isDone()) {
				if (!token.isWhitespace())
					System.out.printf("[%s]\n", token);
				token = walker.nextToken();
			}
		} catch (FileNotFoundException fnfe) {
			System.err.println("Files entered as parameters do not exist");
			fnfe.printStackTrace();
		} catch (IOException ioe) {
				System.err.println("Error reading input or spec file.");
				ioe.printStackTrace();
		}
		
	}
}
