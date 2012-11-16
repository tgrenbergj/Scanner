import java.util.List;
import java.util.LinkedList;
import java.util.Set;


public class NfaSimulator {
	public static void main (String[] args) throws Exception{
		SpecificationReader sr = new SpecificationReader("specification.txt");
		String test = "1234ab";
		int counter = 0;
		int transitions = 0;
		int clonesMade = 0;
		LinkedList<Integer> maxClones = new LinkedList<Integer>();
		NFA nfa = sr.run();
		List<Integer> statesList = new LinkedList<Integer>();
		int startState = nfa.getStartState();
		Set<Integer> states = nfa.getNextStates(startState, (char)169);
		statesList.addAll(states);
		states = nfa.getNextStates(startState, test.charAt(counter));
		statesList.addAll(states);
		transitions = transitions + states.size();
		clonesMade = clonesMade + states.size();
		String trail = "";
		String ident = "";
		
		while(counter<test.length()){
			counter++;	
			List<Integer> trans = new LinkedList<Integer>();
			for(Integer state: statesList){
				Set<Integer>nextStates = nfa.getNextStates(state, (char)169);
				
			}
		}
	}
}
