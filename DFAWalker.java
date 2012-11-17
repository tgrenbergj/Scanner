import java.io.*;

/**
 * A table walker that takes in an input file and a DFA.  The DFA table
 * will then be used to traverse the input file and determine valid tokens.
 */
public class DFAWalker {
	private DFA dfa;
	private PushbackReader reader;
	
	public DFAWalker(String fileName, DFA dfa) throws FileNotFoundException {
		this.dfa = dfa;
		this.reader = new PushbackReader(new FileReader(fileName));
	}
	
	/**
	 * Runs the table walker on the input file.  Outputs every token it
	 * encounters, along with all invalid tokens it encounters that aren't
	 * all whitespace.  
	 */
	public void walk() throws IOException{
		int currChar;
		
		int currState = dfa.getStartState();
		int nextState;
		
		StringBuilder token = new StringBuilder();
		
		//Run until we hit EOF
		while (peek() != -1 && peek() != 65535) {
			
			//Get the next character and see where it transitions
			currChar = reader.read();
			nextState = dfa.getNextState(currState, (char) currChar);
			
			//If the next state is a valid state, append the char and move on
			if ( nextState != -1 && !dfa.isDeadState(nextState) ) {
				token.append((char) currChar);
				currState = nextState;
			//Otherwise we are at the end of a token
			} else {
				StringBuilder invalid = new StringBuilder();
				//Print out the valid token if it has anything in it, and
				//we were at a final state
				if (dfa.isFinalState(currState) && token.length() > 0) {
					System.out.println(dfa.getTokenName(currState) + " " + token.toString());
				} else {
					//Otherwise we need to add this on to the invalid token
					invalid.append(token.toString());
				}
				//Unread the character that broke the token and reset
				reader.unread(currChar);
				currState = dfa.getStartState();
				
				int temp = peek();
				nextState = dfa.getNextState(currState, (char) temp);
				
				//While there are consecutive characters that are invalid
				//read them in and add them to an invalid token
				while (nextState == -1 || dfa.isDeadState(nextState)) {
					if (temp == -1 || peek() == 65535)
						break;
					invalid.append((char)reader.read());
					if (temp == '\n')
						break;
					temp = peek();
					nextState = dfa.getNextState(currState, (char) temp);
				}
				
				//If the invalid characters we read in weren't all whitespace
				//Print out an invalid token
				if (invalid.length() > 0 && !isWhitespace(invalid.toString())) {
					System.out.println("INVALID " + invalid.toString().trim());
				}
				token = new StringBuilder();
			}
		}
		
		//Print out the last token of the file after reading EOF
		if (token.length() > 0) {
			if (dfa.isFinalState(currState)) {
				System.out.println(dfa.getTokenName(currState) + " " + token.toString());
			} else {
				System.out.println("INVALID " + token.toString().trim());
			}
		}
	}
	
	/**
	 * Looks at the next character in the input stream, but does not
	 * consume it.
	 * 
	 * @return The next character in the input stream
	 */
	public int peek() throws IOException {
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
	public boolean isWhitespace(String s) {
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c != ' ' && c != '\t'&& c != '\n' && c != '\r') {
				return false;
			}
		}
		return true;
	}
}
