import java.io.*;


public class LL1ParserDriver {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		if ( args.length < 3) {
			System.out.println("Usage: LL1ParserDriver grammar.txt grammar_info.txt spec.txt script.txt [term1 term2 term3 ... termn]");
		}
		
		String grammar_file = args[0];
		String grammar_info = args[1];
		String spec_file = args[2];
		String input_file = args[3];
		String[] specialTerminals = new String[args.length - 4];
		for (int i = 0; i < args.length - 4; i++) {
			specialTerminals[i] = args[4+i];
		}
		
		Grammar grammar = GrammarReader.read(grammar_file, grammar_info, spec_file, specialTerminals);
		SpecificationReader sr = new SpecificationReader(spec_file);
		NFA nfa = sr.run();
		DFA dfa = NFAConverter.NFAtoDFA(nfa);
		
		File file = new File(input_file);
		
		LL1Parser parser = new LL1Parser(grammar);
		DFAWalker walker = new DFAWalker(file, dfa);
		System.out.println(parser);
		
		Token token = walker.nextToken();
		while (!token.isDone()) {
			if (token.getType().equals(Token.TokenType.INVALID)) {
				System.out.printf("Invalid token [%s] at [%s].  Stopping.\n", token, walker.position());
				break;
			} else if (token.getType().equals(Token.TokenType.VALID)) {
				System.out.println("----Consumed: " + token.getToken());
				parser.parseToken(token);
			}
			token = walker.nextToken();
		}
	}

}
