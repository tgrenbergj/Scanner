import java.util.*;
import java.io.*;

public class MiniREParser {

	LinkedList<Token> stack;
	String input, spec;
	int popcount;
	
	public MiniREParser(String input, String spec) {
		stack = new LinkedList<Token>();
		this.input = input;
		this.spec = spec;
		try {
			populateStack();
		} catch (IOException ioe) {
			System.err.println("Could not read input or spec file");
		}
	}
	
	public void run() {
		minireprogram();
	}
	
	private void minireprogram() {
		pop("BEGIN");
		statementlist();
		pop("END");
	}
	
	private void statementlist() {
		statement();
		statementlisttail();
	}
	
	private void statement() {
		if ( matches("REPLACE") ) {
			//replace REGEX with ASCII-STR in  <file-names> ;
			pop("REPLACE"); //get rid of REPLACE
			Token regex = pop("REGEX"); //get rid of REGEX
			pop("WITH"); //get rid of WITH
			Token asciistr = pop("ASCII-STR"); //get rid of ASCII-STR
			pop("IN"); //get rid of IN
			filenames();
			pop("SEMICOLON"); //get rid of SEMICOLON
		} else if ( matches("RECREP") ) {
			//recursivereplace REGEX with ASCII-STR in  <file-names> ;
			pop("RECREP");  //get rid of RECREP
			Token regex = pop("REGEX"); //get rid of REGEX
			pop("WITH"); //get rid of WITH
			Token asciistr = pop("ASCII-STR"); //get rid of ASCII-STR
			pop("IN"); //get rid of IN
			filenames();
			pop("SEMICOLON"); //get rid of SEMICOLON
		} else if ( matches("ID") ) {
			//ID = <statement-righthand> ;
			pop("ID"); //get rid of ID
			pop("EQ"); //get rid of EQ
			statementrighthand();
			pop("SEMICOLON"); //get rid of EQ
		} else if ( matches("PRINT") ) {
			//print ( <exp-list> ) ;
			pop("PRINT");
			pop("OPENPARENS");
			explist();
			pop("CLOSEPARENS");
			pop("SEMICOLON");
		} else {
			System.err.println("Unexpected token " + stack.peek());
			System.exit(0);
		}
	}
	
	private void statementlisttail() {
		if ( matches("REPLACE") || matches("RECREP") || matches("ID") || matches("PRINT") ) {
			statement();
			statementlisttail();
		} else {
			return;
		}
	}
	
	private void filenames() {
		Token source, destination;
		source = pop("ASCII-STR"); //get rid of ASCII-STR
		pop("GRTNOT"); //get rid of GRTNOT
		destination = pop("ASCII-STR"); //get rid of ASCII-STR
	}
	
	private void statementrighthand() {
		if ( matches("HASH") ) {
			pop("HASH"); //get rid of HASH;
			exp();
		} else if ( matches("MAXFREQ") ) {
			pop("MAXFREQ"); //get rid of MAXFREQ
			pop("OPENPARENS"); //Get rid of OPENPARENS
			Token id = pop("ID"); //Get rid of ID
			pop("CLOSEPARENS"); //get rid of CLOSEPARENS
		} else {
			exp();
		}
	}
	
	private void exp() {
		if ( matches("ID") ) {
			Token id = pop("ID");  //get rid of ID
		} else if ( matches("FIND") ) {
			term();
			exptail();
		} else if ( matches("OPENPARENS") ) {
			pop("OPENPARENS"); //get rid of OPENPARENS
			exp();
			pop("CLOSEPARENS"); //get rid of CLOSEPARENS
		} else {
			System.err.println("Unexpected token " + stack.peek());
			System.exit(0);
		}
	}
	
	private void explist() {
		exp();
		explisttail();
	}
	
	private void explisttail() {
		if ( matches("COMMA") ) {
			pop("COMMA");
			exp();
			explisttail();
		} else {
			return;
		}
	}
	
	private void term() {
		pop("FIND"); //get rid of FIND
		Token regex = pop("REGEX"); //get rid of REGEX
		pop("IN"); //get rid of in
		Token asciistr = pop("ASCII-STR"); //get rid of ASCII-STR
	}
	
	private void exptail() {
		if ( matches("DIFF") || matches("UNION") || matches("INTERS") ) {
			binop();
			term();
			exptail();
		} else {
			return;
		}
	}
	
	private void binop() {
		if ( matches("DIFF") ) {
			pop("DIFF"); //get rid of DIFF
		} else if ( matches("UNION") ) {
			pop("UNION"); //get rid of UNION
		} else if ( matches("INTERS") ) {
			pop("INTERS"); //get rid of INTERS
		} else {
			System.err.println("Unexpected token " + stack.peek());
			System.exit(0);
		}
	}
	
	private boolean matches(String s) {
		if ( stack.peek().getName().equals(s) ) {
			return true;
		} else {
			return false;
		}
	}
	
	private Token pop(String name) {
		Token popped = stack.pop();
		popcount++;
		if (popped.getName().equals(name)) {
			System.out.println("Popping " + popped);
			return popped;
		} else {
			//System.out.printf("Received unexpected token [%s] at location [%d], expected [%s].\n", popped, popcount, name);
			System.exit(0);
			return null;
		}
	}
	
	private void populateStack() throws IOException {
		SpecificationReader sr = new SpecificationReader(spec);
		NFA nfa = sr.run();
		DFA dfa = NFAConverter.NFAtoDFA(nfa);
		File inputFile = new File(input);
		DFAWalker walker = new DFAWalker(inputFile, dfa);
		Token token = walker.nextToken();
		while (!token.isDone()) {
			if (token.getType().equals(Token.TokenType.INVALID)) {
				System.out.printf("Invalid token [%s].  Stopping.\n", token);
				System.exit(0);
			} else if (token.getType().equals(Token.TokenType.VALID)) {
				stack.addLast(token);
			}
			token = walker.nextToken();
		}
	}
	
	public static void main(String[] args) {
		MiniREParser mrp = new MiniREParser("src\\input_phase2\\minire_input.txt", "src\\input_phase2\\minire_spec.txt");
		mrp.run();
	}
}
