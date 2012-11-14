import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.io.PushbackReader;

public class DFAWalker {
	private DFA dfa;
	private PushbackReader reader;
	
	public DFAWalker(String fileName, DFA dfa) throws FileNotFoundException {
		this.dfa = dfa;
		this.reader = new PushbackReader(new FileReader(fileName));
	}
	
	public void walk() throws IOException{
		int currInt;
		char currChar;
		char tempChar;
		
		int currState = dfa.getStartState();
		int nextState;
		
		StringBuilder token = new StringBuilder();
		while(true){
			currInt = reader.read();
			
			//done when eof is reached
			if(currInt == -1 || currInt == Character.MAX_VALUE)
				break;
			
			//skip spaces, tabs, new lines, carriage returns
			currChar = (char)currInt;

			token.append(currChar);
			
			nextState = dfa.getNextState(currState, currChar);
			
			if(nextState == -1 || dfa.isDeadState(nextState)){ //longest match has been reached
				
				reader.unread(currChar);
				
				//now consume all the remaining chars until a space/tab/newline/carriage return is reached
				tempChar = peek();
				
				while(tempChar == ' ' || tempChar == '\t' || tempChar == '\n' || tempChar == '\r') {
					reader.read();
					tempChar = peek();
				}
				
					
				if(dfa.isFinalState(currState))
					System.out.println(dfa.getTokenName(currState) + " " + token.deleteCharAt(token.length() - 1));
				else
					System.out.println("Invalid token: " + token.deleteCharAt(token.length() - 1));
				
				currState = dfa.getStartState(); // start from the beginning of DFA for the next iteration
				token = new StringBuilder(); // reset token too
			}		
			
			else
				currState = nextState;
		}
		

		
	}
	
	public char peek() throws IOException {
		int c = reader.read();
		reader.unread(c);
		return (char) c;
	}
	
	public static void main(String[] args) throws Exception {
		SpecificationReader sr = new SpecificationReader("sample_spec.txt");
		NFA nfa = sr.run();
		DFA dfa = NFAConverter.NFAtoDFA(nfa);
		System.out.println(dfa);
		DFAWalker walker = new DFAWalker("sample_input.txt", dfa);
		walker.walk();
	}
}
