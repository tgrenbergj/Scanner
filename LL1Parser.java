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
		boolean parsed = false;
		String top = stack.peek();
		if(top.equals(token)&& token.equals("$")){
			stack.pop();
			return true;
		}
		if(grammar.isTerminal(top)){
			if(token.equals(top)){
				stack.pop();
				return true;
			}
			else{
				System.out.println("Error");
				return false;
			}
		}
		 if (grammar.isNonTerminal(top)){
			String[] a=  grammar.table[grammar.nontermMap.get(top)][grammar.termMap.get(token)];
			stack.pop();
			pushOnStack(a);
			return true;
		}
		else{
			System.out.println("Error on top of stack");
		}
		return parsed;
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
		
		String[] tokens = {"int", "+", "int", "*", "int"};
		for (int i = 0; i < tokens.length; i++) {
			System.out.println(parser.parseToken(tokens[i]));
		}

	}

}
