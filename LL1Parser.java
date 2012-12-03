import java.util.Stack;


public class LL1Parser {

	private Grammar grammar;
	private Stack<String> stack;
	public LL1Parser(Grammar grammar){
		this.grammar = grammar;
		stack = new Stack<String>();
		stack.push("$");
		stack.push(grammar.start);
	}
	
	public boolean parseToken(String token){
		String top = stack.peek();
		
		if(top.equals(token)&& token.equals("$")){
			stack.pop();
			return true;
		}
		
		while (grammar.isNonTerminal(top)){
			System.out.println(stack);
			String[] rule =  grammar.table[grammar.nontermMap.get(top)][grammar.termMap.get(token)];
			if (rule == null) {
				System.err.println("terminal");
				return false;
			}
			stack.pop();
			if (!rule[0].equals("EPSILON"))
				pushOnStack(rule);
			top = stack.peek();
		}
		
		if(token.equals(top)){
			System.out.println(stack);
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
	public static void main(String[] args) {
		Grammar grammar = GrammarReader.read("src\\input_phase2\\microsoft_grammar.txt");
		LL1Parser parser = new LL1Parser(grammar);
		
		String[] tokens = {"int", "+", "int", "*", "int", "$"};
		for (int i = 0; i < tokens.length; i++) {
			parser.parseToken(tokens[i]);
		}

	}

}
