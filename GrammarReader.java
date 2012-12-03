import java.util.*;
import java.io.*;

/**
 *	A static class that will read in a grammar definition file
 *  and return its representation in a Grammar object.
 */
public class GrammarReader {
	
	private static Scanner scan, lineScan;
	private static String line;
	private static Grammar grammar;
	
	/**
	 * Takes in a grammar definition and returns a Grammar
	 * 
	 * @param file The file containing the grammar
	 * @return The Grammar object representing the grammar
	 */
	public static Grammar read(String gramFile, String tokenFile, String[] specialTerminals) {
		try {
			scan = new Scanner(new File(gramFile));
		} catch (FileNotFoundException fnfe) {
			System.err.println("Can not find file " + gramFile);
			return null;
		}
		
		grammar = new Grammar(specialTerminals);
		
		if (!readTokens())
			return null;
		if (!readStart())
			return null;
		if (!readRules())
			return null;
		
		grammar.makeFirstSets();
		grammar.makeFollowSets();
		grammar.makeTable();
		grammar.makeTokenMap(tokenFile);
		
		return grammar;
 	}
	
	/**
	 * Read in the tokens of the grammar
	 * @return false if the grammar is malformed, true otherwise
	 */
	public static boolean readTokens() {
		//Find line that starts tokens
		readBlanks();
		if (!line.startsWith("%% Tokens")) {
			System.err.println("Rules malformed, was expecting %% Tokens");
			return false;
		}
		//Potentially read blank lines between header and tokens
		readBlanks();
		//Make sure to read multiple lines of tokens
		while (!line.startsWith("%% Start")) {
			lineScan = new Scanner(line);
			
			//Get each whitespace separated word as a terminal
			while (lineScan.hasNext()) {
				String rule = lineScan.next();
				grammar.addTerminal(rule);
			}
			readBlanks();
		}
		return true;
	}
	
	/**
	 * Read in the start state of the grammar
	 * @return false if the grammar is malformed, true otherwise
	 */
	public static boolean readStart() {
		if (!line.startsWith("%% Start")) {
			System.err.println("Rules malformed, was expecting %% Start");
			return false;
		}
		
		//Read blanks and set start rule
		readBlanks();
		grammar.setStart(line.trim());
		readBlanks();
		return true;
	}
	
	/**
	 * Read in the rules of the grammar
	 * @return false if the grammar is malformed, true otherwise
	 */
	public static boolean readRules() {
		if (!line.startsWith("%% Rules")) {
			System.err.println("Rules malformed, was expecting %% Rules");
			return false;
		}
		while (scan.hasNextLine()) {
			readBlanks();
			String[] rules = line.split(" (->|\\||::=)");
			String nonterm = rules[0].trim();
			grammar.addNonterminal(nonterm);
			for (int i = 1; i < rules.length; i++) {
				rules[i] = rules[i].trim();
				rules[i] = rules[i].replaceAll("><", "> <");
				String[] split = rules[i].split("\\s+");
				grammar.addRule(nonterm, split);
			}
		}
		
		return true;
	}
	
	/**
	 * Read blank lines from the input
	 */
	public static void readBlanks() {
		line = scan.nextLine();
		while (line.length() == 0) {
			line = scan.nextLine();
		}
	}
}
