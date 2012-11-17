import java.io.FileNotFoundException;
import java.io.IOException;


public class NFARunner {

	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("Not enough arguments.");
			System.err.println("Usage: java Runner spec_file input_file");
			System.exit(1);
		}
		
		String spec = args[0];
		String input = args[1];
		
		try {
			SpecificationReader sr = new SpecificationReader(spec);
			NFA nfa = sr.run();
//			System.out.println("NFA Table:");
//			System.out.println(nfa); // Not printing NFA TAble since it is not required and it will be easy to compare output this way
			System.out.println();
			System.out.println();
			System.out.println("NFA Simulator Output:");
			System.out.println();
			NFASimulator walker = new NFASimulator(nfa,input);
			walker.simulate();
		} catch (FileNotFoundException fnfe) {
			System.err.println("Files entered as parameters do not exist");
			fnfe.printStackTrace();
		} catch (IOException ioe) {
				System.err.println("Error reading input or spec file.");
				ioe.printStackTrace();
		}
		
	}

}
