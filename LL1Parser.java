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
			String[] rule =  grammar.table[grammar.nontermMap.get(top)][grammar.termMap.get(token.getName())];
			if (rule == null) {
				System.err.println("terminal");
				return false;
			}
			stack.pop();
			if (!rule[0].equals("EPSILON"))
				pushOnStack(rule);
			top = stack.peek();
		}
		
		if(token.getName().equals(top)){
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
		Grammar grammar = GrammarReader.read("src\\input_phase2\\minire_grammar.txt");
		System.out.println(grammar);
		SpecificationReader sr = new SpecificationReader("src\\input_phase2\\minire_spec.txt");
		NFA nfa = sr.run();
		DFA dfa = NFAConverter.NFAtoDFA(nfa);
		
		File file = new File("src\\input_phase2\\minire_input.txt");
		
		LL1Parser parser = new LL1Parser(grammar);
		DFAWalker walker = new DFAWalker(file, dfa);
		Token token = walker.nextToken();
		while (!token.isDone()) {
			if (token.getType().equals(Token.TokenType.INVALID)) {
				System.out.printf("Invalid token [%s].  Stopping.\n", token.getName());
			} else if (token.getType().equals(Token.TokenType.VALID)) {
				System.out.println(parser.parseToken(token));
				System.out.println(token.getToken());
			}
			token = walker.nextToken();
		}

	}

}
