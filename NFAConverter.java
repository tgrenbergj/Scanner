import java.util.*;

/**
 * A class that takes in a complete NFA, and outputs a valid DFA
 * to be used by the table walker
 */
public class NFAConverter {
	
	private static char EPSILON = (char) 169;

	/**
	 * A method to convert a NFA to a DFA
	 * @param nfa The NFA to convert
	 * @return The converted DFA
	 */
	public static DFA NFAtoDFA(NFA nfa) {
		Set<Set<Integer>> visited = new HashSet<Set<Integer>>();
		Queue<State> queue = new LinkedList<State>();
		ArrayList<State> done = new ArrayList<State>();
		
		State state = new State(nfa.getEpsilonClosure(nfa.getStartState()), nfa.getTransitions());
		visited.add(state.name);
		queue.add(state);
		
		//Do a BFS on the NFA.  Basically take the epsilon closure of a state,
		//see where you can go if you tried every character transition on it,
		//and visit those states and do the same thing.
		while (!queue.isEmpty()) {
			State current = queue.remove();
			for (Character c : nfa.getTransitions()) {
				if ( c != EPSILON ) {
					Set<Integer> nextStates = nfa.getNextStates(current.name, c);
					nextStates.addAll(nfa.getEpsilonClosure(nextStates));
					if (!visited.contains(nextStates)) {
						visited.add(nextStates);
						queue.add(new State(nextStates, nfa.getTransitions()));
					}
					current.addTransition(nextStates, c);
				}
			}
			done.add(current);
		}
		
		//Map each of the named states, eg {0, 1, 5, 6, 7} of the NFA conversion
		//table to a single state number for the DFA
		Map<Set<Integer>, Integer> map = new HashMap<Set<Integer>, Integer>();
		for (int i = 0; i < done.size(); i++) {
			map.put(done.get(i).name, i);
		}
		
		//For each state the DFA has, get the next state it will go to
		//for each character transition that is possible in the language
		DFA dfa = new DFA(done.size(), nfa.getTransitions());
		for (State curState : done) {
			for (Character c : dfa.getTransitions()) {
				dfa.addTransition(map.get(curState.name), map.get(curState.nextStates.get(c)), c);
			}
		}
		
		//Find the start state of the DFA and set it
		for (State curState : done) {
			if (curState.name.contains(nfa.getStartState())) {
				dfa.setStartState(map.get(curState.name));
				break;
			}
		}
		
		//Find all of the final states of the DFA and set them.  In addition,
		//make sure each final state is mapped to a token name
		for (State curState : done) {
			for (int i : nfa.getFinalStates()) {
				if (curState.name.contains(i)) {
					dfa.addFinalState(map.get(curState.name));
					String tokenName = null;
					for (int finalState : curState.name) {
						if (nfa.getTokenNames(finalState) != null) {
							String newTokenName = (String) nfa.getTokenNames(finalState).toArray()[0];
							if (tokenName != null && !tokenName.equals(newTokenName)) {
								if (nfa.getTokenRank(tokenName) > nfa.getTokenRank(newTokenName)) {
									System.err.printf("WARNING: Ambiguous state %d, matches both %s and %s.  %s was defined later.\n",
										i, tokenName, newTokenName, tokenName);
								} else {
									System.err.printf("WARNING: Ambiguous state %d, matches both %s and %s.  %s was defined later.\n",
											i, tokenName, newTokenName, newTokenName);
									tokenName = newTokenName;
								}
							} else {
								tokenName = newTokenName;
							}
						}
					}
					dfa.addTokenName(map.get(curState.name), tokenName);
					break;
				}
			}
		}
		
		//Find all of the possible dead states in the graph
		dfa.findDeadStates();
		
		return dfa;
		
	}
	

	/**
	 * A state that will be used in the NFA -> DFA conversion.
	 * 
	 * It's name is the set of states in the NFA that this single DFA
	 * state will represent.
	 * 
	 * Also map which states the NFA would transition to on each character
	 */
	private static class State {
		
		Set<Integer> name;
		Map<Character, Set<Integer>> nextStates;
		
		public State(Set<Integer> name, Set<Character> transitions) {
			this.name = name;
			nextStates = new HashMap<Character, Set<Integer>>();
			for (Character c : transitions) {
				if ( c != EPSILON ) {
					nextStates.put(c, new HashSet<Integer>());
				}
			}
		}
		
		public void addTransition(Set<Integer> states, char transition) {
			nextStates.get(transition).addAll(states);
		}
		
		@Override
		public boolean equals(Object o) {
			return this.name.equals(((State)o).name);
		}
		
		@Override
		public int hashCode() {
			return name.hashCode();
		}
		
	}

}
