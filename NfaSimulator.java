import java.util.Set;


public class NfaSimulator {
	public static void main (String[] args) throws Exception{
		SpecificationReader sr = new SpecificationReader("specification.txt");
		String test = "1234abc";
		NFA nfa = sr.run();
		int startState = nfa.getStartState();
		Set<Integer> states = nfa.getEpsilonClosure(startState);
		int counter = 0;
		while(counter<test.length()){
			Set<Integer> nextStates = nfa.getNextStates(states, test.charAt(counter));
			nextStates = nfa.getEpsilonClosure(nextStates);
			System.out.println(nextStates);
			for(Integer state : nextStates){
				if (nfa.getTokenNames(state)!=null){
					System.out.println(nfa.getTokenNames(state));
				}
			}
			states = nfa.getEpsilonClosure(nextStates);
			counter++;
			
		}
	}
}
