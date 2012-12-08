import java.io.*;
import java.util.*;


public class DFAWalker {
	private DFA dfa;
	private PushbackReader reader;
	private FileReader fr;
	private int line;
	private int lineCharacter;
	private int character;
	private int tokenCount;
	private int prevLine;
	private int prevLineCharacter;
	private int prevCharacter;
	private int prevTokenCount;
	
	/**
	 * Run walker on a file
	 */
	private DFAWalker(DFA dfa) {
		this.dfa = dfa;
		line = 1;
		lineCharacter = 1;
	}
	
	public DFAWalker(File file, DFA dfa) throws FileNotFoundException {
		this(dfa);
		fr = new FileReader(file);
		this.reader = new PushbackReader(fr, 10000);
	}
	
	/**
	 * Run walker on a string
	 */
	public DFAWalker(String text, DFA dfa) throws FileNotFoundException {
		this(dfa);
		InputStream in = new ByteArrayInputStream(text.getBytes());
		this.reader = new PushbackReader(new InputStreamReader(in));
	}
	
	public Token nextToken() throws IOException {
		Stack<Integer> prevStates = new Stack<Integer>();
		Token token = null;
		StringBuilder sb = new StringBuilder();
		int curState = dfa.getStartState();
		int nextState;
		int curChar = -1;
		while (peek() != -1 && peek() != 65535) {
			curChar = reader.read();
			nextState = dfa.getNextState(curState, (char) curChar);
			
			if ( nextState != -1 && !dfa.isDeadState(nextState) ) {
				prevStates.push(nextState);
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
				
				//Start recovery, maybe valid substring in invalid string
				boolean validFound = false;
				int len = sb.length();
				int popState = -1;
				while (!prevStates.isEmpty()) {
					popState = prevStates.pop();
					if (dfa.isFinalState(popState)) {
						validFound = true;
						break;
					} else {
						len--;
					}
				}
				
				//Push back invalid chars into stream, set valid token
				if (validFound) { 
					for (int i = sb.length() - 1; i >= len; i--) {
						reader.unread(sb.charAt(i));
					}			
					token = new Token(Token.TokenType.VALID, dfa.getTokenName(popState), sb.substring(0, len));
					break;
				}
				
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
					
					//Exact duplicate of above code
					boolean validFound = false;
					int len = sb.length();
					int popState = -1;
					while (!prevStates.isEmpty()) {
						popState = prevStates.pop();
						if (dfa.isFinalState(popState)) {
							validFound = true;
							break;
						} else {
							len--;
						}
					}
					
					if (validFound) { 
						reader.unread(-1);
						for (int i = sb.length() - 1; i >= len; i--) {
							reader.unread(sb.charAt(i));
						}
						token = new Token(Token.TokenType.VALID, dfa.getTokenName(popState), sb.substring(0, len));
					} else {
						token = new Token(Token.TokenType.INVALID, "INVALID", sb.toString());
					}
				}
			} else {
				token = new Token(Token.TokenType.DONE);
				reader.close();
				fr.close();
			}
		}
		if (!token.getType().equals(Token.TokenType.DONE)) {
			updatePosition(token);
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
	
	/**
	 * Keep track of the current position in the file
	 * 
	 * @param token The token just read
	 */
	private void updatePosition(Token token) {
		prevLine = line;
		prevCharacter = character;
		prevLineCharacter = lineCharacter;
		prevTokenCount = tokenCount;
		
		//Increase total character count
		character += token.getToken().length();
		lineCharacter += token.getToken().length();
		//Increase total token count
		if (token.getType().equals(Token.TokenType.VALID)) {
			tokenCount++;
		}
		//Increase new line count
		if (token.getToken().contains("\r\n") ) {
			line++;
			int index = token.getToken().indexOf("\r\n");
			lineCharacter = token.getToken().length() - index;
		} else if (token.getToken().contains("\n") ) {
			line++;
			int index = token.getToken().indexOf("\n");
			lineCharacter = token.getToken().length() - index;
		}
	}
	
	/**
	 * Get the position in the file of the beginning of the last returne
	 * character.
	 * 
	 * @return A string representation of the file position
	 */
	public String position() {
		return String.format("Line: %d Char: %d", prevLine, prevLineCharacter);
	}
	
	public int getChar() {
		return prevCharacter;
	}
	
	public int getToken() {
		return prevTokenCount;
	}
	
	public int getLine() {
		return prevLine;
	}
	
	public int getLineChar() {
		return prevLineCharacter;
	}
}
