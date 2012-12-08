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
			System.out.println(stack);
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
			System.out.println(stack);
			top = stack.peek();
		}
		String topName = grammar.getTerminalName(top);
		String tokenName = token.getName();
		
		if(tokenName.equals(topName)){
			stack.pop();
			System.out.println(stack);
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
	
	public String toString() {
		return stack.toString();
	}

}
