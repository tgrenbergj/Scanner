import java.io.*;
import java.util.*;


public class LL1Parser {
	
	private Grammar grammar;
	private Stack<String> stack;
	public LL1Parser(Grammar grammar){
		this.grammar = grammar;
		stack = new Stack<String>();
		stack.push("$");
		stack.push(grammar.start);
	}
	
	public boolean parseToken(Token token){
		String top = stack.peek();
		
		if(top.equals(token.getName())&& token.getName().equals("$")){
			stack.pop();
			return true;
		}
		
		while (grammar.isNonTerminal(top)){
			String[] rule =  grammar.getRule(top, token.getName());
			if (rule == null) {
				System.err.println("terminal");
				return false;
			}
			stack.pop();
			if (!rule[0].equals(Grammar.EPSILON))
				pushOnStack(rule);
			top = stack.peek();
		}
		String topName = grammar.getTerminalName(top);
		String tokenName = token.getName();
		
		if(tokenName.equals(topName)){
			stack.pop();
			return true;
		}
		else{
			System.out.println("Error");
			return false;
		}
	}
	
	public void pushOnStack(String[] stuff){
		for(int i = stuff.length-1; i>=0;i--){
			stack.push(stuff[i]);
		}
	}
	
	/**
	 * Temporary main method
	 */
	public static void main(String[] args)  throws IOException {
		String grammar_file = "src\\input_phase2\\minire_grammar.txt";
		String spec_file = "src\\input_phase2\\minire_spec.txt";
		String input_file = "src\\input_phase2\\minire_input.txt";
		String[] specialTerminals = {"ASCII-STR", "REGEX", "ID", "$"};
		
		Grammar grammar = GrammarReader.read(grammar_file, spec_file, specialTerminals);
		SpecificationReader sr = new SpecificationReader(spec_file);
		NFA nfa = sr.run();
		DFA dfa = NFAConverter.NFAtoDFA(nfa);
		
		File file = new File(input_file);
		
		LL1Parser parser = new LL1Parser(grammar);
		DFAWalker walker = new DFAWalker(file, dfa);
		Token token = walker.nextToken();
		
		while (!token.isDone()) {
			if (token.getType().equals(Token.TokenType.INVALID)) {
				System.out.printf("Invalid token [%s].  Stopping.\n", token);
				break;
			} else if (token.getType().equals(Token.TokenType.VALID)) {
				boolean valid = parser.parseToken(token);
				System.out.printf("Valid: %s [%s]\n", valid ? "true " : "false", token);
			}
			token = walker.nextToken();
		}

	}

}
