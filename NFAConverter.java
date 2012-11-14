import java.io.IOException;
import java.util.*;
//Created by Akbar
public class NFAConverter {
	private static char EPSILON = (char) 169;

	public static DFA NFAtoDFA(NFA nfa) {
		Set<Set<Integer>> visited = new HashSet<Set<Integer>>();
		Queue<State> queue = new LinkedList<State>();
		ArrayList<State> done = new ArrayList<State>();
		
		State state = new State(nfa.getEpsilonClosure(nfa.getStartState()), nfa.getTransitions());
		visited.add(state.name);
		queue.add(state);
		
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
		
		Map<Set<Integer>, Integer> map = new HashMap<Set<Integer>, Integer>();
		for (int i = 0; i < done.size(); i++) {
			map.put(done.get(i).name, i);
		}
		
		DFA dfa = new DFA(done.size(), nfa.getTransitions());
		for (State curState : done) {
			for (Character c : dfa.getTransitions()) {
				dfa.addTransition(map.get(curState.name), map.get(curState.nextStates.get(c)), c);
			}
		}
		
		for (State curState : done) {
			if (curState.name.contains(nfa.getStartState())) {
				dfa.setStartState(map.get(curState.name));
				break;
			}
		}
		
		for (State curState : done) {
			for (int i : nfa.getFinalStates()) {
				if (curState.name.contains(i)) {
					dfa.addFinalState(map.get(curState.name));
					String tokenName = null;
					for (int finalState : curState.name) {
						if (nfa.getTokenNames(finalState) != null)
							tokenName = (String) nfa.getTokenNames(finalState).toArray()[0];
					}
					dfa.addTokenName(map.get(curState.name), tokenName);
					break;
				}
			}
		}
		dfa.findDeadStates();
		return dfa;
		
	}
	
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
	
	public static void main(String[] args) throws IOException{
		DFA newDFA = NFAConverter.NFAtoDFA(new RecursiveDescentParser("hello(a|bb)*", null).rexp());
		System.out.println(newDFA);
	}
	
}
