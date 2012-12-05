import java.util.*;
import java.io.*;

public class MiniREParser {

	LinkedList<Token> stack;
	LinkedList<String> locStack;
	String input, spec;
	String pos;
	int popcount;
	Map<String, MiniREVariable> varMap;
	Set<String> varName;
	
	public MiniREParser(String input, String spec) {
		stack = new LinkedList<Token>();
		locStack = new LinkedList<String>();
		varMap = new HashMap<String, MiniREVariable>();
		varName = new HashSet<String>();
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
			pop("REPLACE"); 
			Token regex = pop("REGEX"); 
			pop("WITH"); 
			Token asciistr = pop("ASCII-STR"); 
			pop("IN"); 
			Token source = pop("ASCII-STR"); 
			pop("GRTNOT"); 
			Token destination = pop("ASCII-STR");
			pop("SEMICOLON"); 
			MiniREFunctions.replace(regex.getToken(), asciistr.getToken(),
					source.getToken(), destination.getToken());
		} else if ( matches("RECREP") ) {
			pop("RECREP");  
			Token regex = pop("REGEX"); 
			pop("WITH"); 
			Token asciistr = pop("ASCII-STR"); 
			pop("IN"); 
			Token source = pop("ASCII-STR"); 
			pop("GRTNOT"); 
			Token destination = pop("ASCII-STR");
			pop("SEMICOLON"); 
			MiniREFunctions.recursivereplace(regex.getToken(), asciistr.getToken(),
					source.getToken(), destination.getToken());
		} else if ( matches("ID") ) {
			Token id = pop("ID"); 
			addVar(id.getToken());
			pop("EQ"); 
			MiniREVariable data = statementrighthand();
			setVar(id.getToken(), data);
			pop("SEMICOLON"); 
		} else if ( matches("PRINT") ) {
			pop("PRINT");
			pop("OPENPARENS");
			explist();
			pop("CLOSEPARENS");
			pop("SEMICOLON");
		} else {
			//Error and quit
			pop("PRINT or ID or REPLACE or RECREP");
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
	
	private MiniREVariable statementrighthand() {
		if ( matches("HASH") ) {
			pop("HASH"); 
			MiniREVariable list = exp();
			int count = MiniREFunctions.hash(list);
			return new MiniREVariable(count);
		} else if ( matches("MAXFREQ") ) {
			pop("MAXFREQ");
			pop("OPENPARENS");
			Token id = pop("ID"); 
			pop("CLOSEPARENS");
			MiniREVariable list = getVar(id.getToken());
			List<MiniREString> maxList = MiniREFunctions.maxfreqstring(list);
			return new MiniREVariable(maxList);
		} else {
			return exp();
		}
	}
	
	private MiniREVariable exp() {
		if ( matches("ID") ) {
			Token id = pop("ID");
			return getVar(id.getToken());
		} else if ( matches("FIND") ) {
			MiniREVariable retTemp = term();
			LinkedList<MiniREVariable> stack = new LinkedList<MiniREVariable>();
			stack.addLast(retTemp);
			exptail(stack);
			return evaluateStack(stack);
		} else if ( matches("OPENPARENS") ) {
			pop("OPENPARENS");
			MiniREVariable retVar = exp();
			pop("CLOSEPARENS");
			return retVar;
		} else {
			//Error and quit
			pop("ID or FIND or OPENPARENS");
		}
		return null;
	}
	
	private void explist() {
		MiniREFunctions.print( exp() );
		explisttail();
	}
	
	private void explisttail() {
		if ( matches("COMMA") ) {
			pop("COMMA");
			MiniREFunctions.print( exp() );
			explisttail();
		} else {
			return;
		}
	}
	
	private MiniREVariable term() {
		pop("FIND"); 
		Token regex = pop("REGEX"); 
		pop("IN"); 
		Token asciistr = pop("ASCII-STR");
		List<MiniREString> found = MiniREFunctions.find(regex.getToken(), asciistr.getToken());
		return new MiniREVariable(found);
	}
	
	private void exptail(LinkedList<MiniREVariable> stack) {
		if ( matches("DIFF") || matches("UNION") || matches("INTERS") ) {
			if ( matches("DIFF") ) {
				pop("DIFF");
				stack.addLast(new MiniREVariable(MiniREVariable.Type.DIFF));
			} else if ( matches("UNION") ) {
				pop("UNION"); 
				stack.addLast(new MiniREVariable(MiniREVariable.Type.UNION));
			} else if ( matches("INTERS") ) {
				pop("INTERS");
				stack.addLast(new MiniREVariable(MiniREVariable.Type.INTERS));
			} else {
				//Error and quit
				pop("DIFF or UNION or INTERS");
			}
			MiniREVariable tempRet = term();
			stack.addLast(tempRet);
			exptail(stack);
		} else {
			return;
		}
	}
	
	/**
	 * Check the token on the top of the stack versus passed in string.
	 * @param s The expected token at the top of the stack
	 * @return True if the token was found
	 */
	private boolean matches(String s) {
		if ( stack.peek().getName().equals(s) ) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Pop a token off of the stack.  If the token popped does not match
	 * the passed in name, we exit and print line/char number.
	 * 
	 * @param name The expected token name to find
	 * @return The token that was popped.
	 */
	private Token pop(String name) {
		Token popped = stack.pop();
		pos = locStack.pop();
		popcount++;
		if (popped.getName().equals(name)) {
			//System.out.println("Popping " + popped);
			return popped;
		} else {
			System.err.printf("Received unexpected token [%s] at location [%s], expected [%s].\n", popped, pos, name);
			System.out.println("Exiting.  Check error logs.");
			System.exit(0);
			return null;
		}
	}
	
	/**
	 * Evaluate a stack of lists with union, diff, and inters operations.
	 * Stack should look like: Var, Op, Var, Op, Var, Op, Var
	 * 
	 * @param stack
	 * @return
	 */
	private MiniREVariable evaluateStack(LinkedList<MiniREVariable> stack) {
		while (stack.size() != 1) {
			MiniREVariable list1 = stack.pop();
			MiniREVariable op = stack.pop();
			MiniREVariable list2 = stack.pop();
			MiniREVariable result;
			if (op.getType().equals(MiniREVariable.Type.DIFF)) {
				result = new MiniREVariable(MiniREFunctions.diff(list1.getStrings(), list2.getStrings()));
				stack.addFirst(result);
			} else if (op.getType().equals(MiniREVariable.Type.UNION)) {
				result = new MiniREVariable(MiniREFunctions.union(list1.getStrings(), list2.getStrings()));
				stack.addFirst(result);
			} else if (op.getType().equals(MiniREVariable.Type.INTERS)) {
				result = new MiniREVariable(MiniREFunctions.inters(list1.getStrings(), list2.getStrings()));
				stack.addFirst(result);
			} else {
				System.err.println("Error processing exp stack");
				System.exit(0);
			}
		}
		return stack.pop();
	}
	
	/**
	 * Add a new variable name to the program
	 * @param name The variable name to add
	 */
	private void addVar(String name) {
		varName.add(name);
	}
	
	/**
	 * Get the data a variable name is associated with
	 * @param name The variable name to retrieve
	 * @return The data associated with the variable
	 */
	private MiniREVariable getVar(String name) {
		MiniREVariable var = varMap.get(name);
		if (var == null) {
			System.err.printf("Received undefined variable [%s] at location [%s].\n", name, pos);
			System.out.println("Exiting.  Check error logs.");
			System.exit(0);
		}
		return var;
	}
	
	/**
	 * Set a variable to a value
	 * @param name The name of the variable
	 * @param var The variable data
	 */
	private void setVar(String name, MiniREVariable var) {
		varMap.put(name, var);
	}
	
	/**
	 * Read in the entire input file, with all tokens and their locations
	 * on a stack.  Exit if there are any invalid tokens.
	 * 
	 * @throws IOException
	 */
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
				locStack.addLast(walker.position());
			}
			token = walker.nextToken();
		}
	}
	
	public static void main(String[] args) {
		MiniREParser mrp = new MiniREParser("src\\input_phase2\\minire_input.txt", "src\\input_phase2\\minire_spec.txt");
		mrp.run();
	}
}
