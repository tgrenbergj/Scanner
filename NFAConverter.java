import java.util.*;
//Created by Akbar
public class NFAConverter {

	public static DFA NFAtoDFA(NFA nfa) {
		Set<Set<Integer>> visited = new HashSet<Set<Integer>>();
		Queue<State> queue = new LinkedList<State>();
		ArrayList<State> done = new ArrayList<State>();
		
		int numTransitions = nfa.getNumTransitions() - 1;
		
		State state = new State(nfa.getEpsilonClosure(nfa.getStartState()), numTransitions);
		visited.add(state.name);
		queue.add(state);
		
		while (!queue.isEmpty()) {
			State current = queue.remove();
			for (int i = 0; i < numTransitions; i++) {
				Set<Integer> nextStates = nfa.getNextStates(current.name, i+1);
				nextStates.addAll(nfa.getEpsilonClosure(nextStates));
				if (!visited.contains(nextStates)) {
					visited.add(nextStates);
					queue.add(new State(nextStates, numTransitions));
				}
				current.addTransition(nextStates, i);
			}
			done.add(current);
		}
		
		Map<Set<Integer>, Integer> map = new HashMap<Set<Integer>, Integer>();
		for (int i = 0; i < done.size(); i++) {
			map.put(done.get(i).name, i);
		}
		
		DFA dfa = new DFA(done.size(), numTransitions);
		for (State curState : done) {
			for (int i = 0; i < numTransitions; i++) {
				dfa.addTransition(map.get(curState.name), map.get(curState.nextStates[i]), i);
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
					break;
				}
			}
		}
	
		return dfa;
		
	}
	
	private static class State {
		
		Set<Integer> name;
		Set<Integer>[] nextStates;
		
		@SuppressWarnings("unchecked")
		public State(Set<Integer> name, int transitions) {
			this.name = name;
			this.nextStates = new HashSet[transitions];
		}
		
		public void addTransition(Set<Integer> states, int transition) {
			nextStates[transition] = states;
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
	
	public static void main(String[] args) {
		NFA newNFA = new NFA();
		DFA newDFA = NFAConverter.NFAtoDFA(newNFA);
		System.out.println(newDFA);
	}
	
}
