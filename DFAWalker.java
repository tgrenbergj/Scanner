import java.io.*;


public class DFAWalker {
	private DFA dfa;
	private PushbackReader reader;
	
	/**
	 * Run walker on a file
	 */
	public DFAWalker(File file, DFA dfa) throws FileNotFoundException {
		this.dfa = dfa;
		this.reader = new PushbackReader(new FileReader(file));
	}
	
	/**
	 * Run walker on a string
	 */
	public DFAWalker(String text, DFA dfa) throws FileNotFoundException {
		this.dfa = dfa;
		InputStream in = new ByteArrayInputStream(text.getBytes());
		this.reader = new PushbackReader(new InputStreamReader(in));
	}
	
	public Token nextToken() throws IOException {
		Token token = null;
		StringBuilder sb = new StringBuilder();
		int curState = dfa.getStartState();
		int nextState;
		int curChar = -1;
		while (peek() != -1 && peek() != 65535) {
			curChar = reader.read();
			nextState = dfa.getNextState(curState, (char) curChar);
			
			if ( nextState != -1 && !dfa.isDeadState(nextState) ) {
				sb.append((char) curChar);
				curState = nextState;
			} else {
				
				if ( dfa.isFinalState(curState) && sb.length() > 0 ) {
					token = new Token(Token.TokenType.VALID, dfa.getTokenName(curState), sb.toString());
					reader.unread(curChar);
					break;
				}
				
				//Unread the character that broke the token and reset
				reader.unread(curChar);
				curState = dfa.getStartState();
				
				curChar = peek();
				nextState = dfa.getNextState(curState, (char) curChar);
				
				//While there are consecutive characters that are invalid
				//read them in and add them to an invalid token
				while (nextState == -1 || dfa.isDeadState(nextState)) {
					if (curChar == -1 || peek() == 65535)
						break;
					sb.append((char)reader.read());
					if (curChar == '\n' || curChar == '\r')
						break;
					curChar = peek();
					nextState = dfa.getNextState(curState, (char) curChar);
				}
				
				//If the invalid characters we read in weren't all whitespace
				//Print out an invalid token
				if (sb.length() > 0 && isWhitespace(sb.toString())) {
					token = new Token(Token.TokenType.WHITESPACE, "WHITESPACE", sb.toString());
				} else {
					token = new Token(Token.TokenType.INVALID, "INVALID", sb.toString());
				}
				break;
			}
		}
		
		if (token == null) {
			if (sb.length() > 0) {
				if (dfa.isFinalState(curState)) {
					token = new Token(Token.TokenType.VALID, dfa.getTokenName(curState), sb.toString());
				} else if (isWhitespace(sb.toString())) {
					token = new Token(Token.TokenType.WHITESPACE, "WHITESPACE", sb.toString());
				} else {
					token = new Token(Token.TokenType.INVALID, "INVALID", sb.toString());
				}
			} else {
				token = new Token(Token.TokenType.DONE);
			}
		}

		return token;
	}
	
	
	/**
	 * Looks at the next character in the input stream, but does not
	 * consume it.
	 * 
	 * @return The next character in the input stream
	 */
	private int peek() throws IOException {
		int c = reader.read();
		reader.unread(c);
		return c;
	}
	
	/**
	 * Determines if a string is made entirely of whitespace.
	 * 
	 * @param s The string to check.
	 * @return True if the string is all whitespace, false otherwise.
	 */
	private boolean isWhitespace(String s) {
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c != ' ' && c != '\t'&& c != '\n' && c != '\r') {
				return false;
			}
		}
		return true;
	}
}
