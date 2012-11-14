import java.io.FileReader;
import java.io.IOException;

public class DFAWalker {
	private DFA dfa;
	private String fileName;
	
	public DFAWalker(String fileName, DFA dfa){
		this.dfa = dfa;
		this.fileName = fileName;
	}
	
	public void walk() throws IOException{
		FileReader reader = new FileReader(fileName);
		
		int currInt;
		char currChar;
		char tempChar;
		
		int currState = dfa.getStartState();
		int nextState;
		
		StringBuilder token = new StringBuilder();
		while(true){
			currInt = reader.read();
			
			//done when eof is reached
			if(currInt == -1)
				break;
			
			//skip spaces, tabs, new lines, carriage returns
			currChar = (char)currInt;
			if((currChar == ' ') || (currChar == '\t') || (currChar == '\n') || (currChar == '\r'))
				continue;
			
			token.append(currChar);
			
			nextState = dfa.getNextState(currState, currChar);
			
			if(dfa.isDeadState(nextState)){ //longest match has been reached
				
				//now consume all the remaining chars until a space/tab/newline/carriage return is reached
				tempChar = currChar;
				while(tempChar != ' ' && tempChar != '\t' && tempChar != '\n' && tempChar != '\r')
					tempChar = (char)reader.read();
				
					
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
}
