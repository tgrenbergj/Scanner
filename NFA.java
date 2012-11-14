import java.util.*;

/*
 * The first dimension is an index into the state of the NFA
 * The second dimension is an index into the transitions possible for the nfa
 * nfa[0...n-1][0] will always contain a set of epsilon transitions for that state
 */

public class NFA extends FiniteAutomata {

	private ArrayList<Map<Character, Set<Integer>>> nfa;
	private Set<Character> transitions;
	private Map<Integer, Set<String>> tokenNames;
	private Set<Integer> deadStates;
	private static char EPSILON = (char) 169;
	
	public NFA(char c) {
		this();
		
		nfa.add(0, new HashMap<Character, Set<Integer>>());
		setStartState(0);
		
		nfa.add(1, new HashMap<Character, Set<Integer>>());
		this.addFinalState(1);
		
		addTransition(0, 1, c);
	}
	
	public NFA() {
		nfa = new ArrayList<Map<Character, Set<Integer>>>();
		transitions = new HashSet<Character>();
		deadStates = new HashSet<Integer>();
		tokenNames = new HashMap<Integer, Set<String>>();
	}
	
	public void addTransition(int start, int end, char transition) {
		transitions.add(transition);
		if (!nfa.get(start).containsKey(transition))
			nfa.get(start).put(transition, new HashSet<Integer>());
		nfa.get(start).get(transition).add(end);
	}
	
	public void addState() {
		nfa.add(new HashMap<Character, Set<Integer>>());
	}
	
	public void addEpsilonTransition(int start, int end) {
		addTransition(start, end, EPSILON);
	}
	
	public List<Map<Character, Set<Integer>>> getNFA() {
		return nfa;
	}
	
	public Set<Character> getTransitions() {
		return transitions;
	}
	
	public int getNumTransitions() {
		return transitions.size();
	}
	
	public int getNumStates() {
		return nfa.size();
	}
	
	public void addTokenName(String name) {
		for (Integer i : finalStates) {
			if (!tokenNames.containsKey(i)) {
				tokenNames.put(i, new HashSet<String>());
			}
			tokenNames.get(i).add(name);
		}
	}
	
	public void addTokenName(int state, String name) {
		if (!tokenNames.containsKey(state)) {
			tokenNames.put(state, new HashSet<String>());
		}
		tokenNames.get(state).add(name);
	}
	
	public Set<String> getTokenNames(int state) {
		if (!finalStates.contains(state)) {
			return null;
		} else {
			return tokenNames.get(state);
		}
	}
	
	public Set<Integer> getNextStates(int state, char transition) {
		return nfa.get(state).get(transition);
	}
	
	public Set<Integer> getNextStates(Set<Integer> states, char transition) {
		Set<Integer> nextStates = new HashSet<Integer>();
		for (Integer state : states) {
			if (nfa.get(state).containsKey(transition)) {
				nextStates.addAll(nfa.get(state).get(transition));
			}
		}
		return nextStates;
	}
	
	public Set<Integer> getEpsilonClosure(Set<Integer> states) {
		Set<Integer> closure = new HashSet<Integer>();
		for (int i : states) {
			closure.addAll(getEpsilonClosure(i));
		}
		return closure;
	}
	
	public Set<Integer> getEpsilonClosure(int state) {
		Set<Integer> visited = new HashSet<Integer>();
		Set<Integer> closure = new HashSet<Integer>();
		Queue<Integer> queue = new LinkedList<Integer>();
		
		closure.add(state);
		visited.add(state);
		queue.add(state);
		
		while (!queue.isEmpty()) {
			int current = queue.remove();
			if (nfa.get(current).containsKey(EPSILON)) {
				for (Integer i : nfa.get(current).get(EPSILON)) {
					if (!visited.contains(i)) {
						closure.add(i);
						visited.add(i);
						queue.add(i);
					}
				}
			}
		}
		return closure;
	}
	
	public void findDeadStates() {
		for(int i = 0; i < nfa.size(); i++) {
			if (!finalStates.contains(i)) {
				if (nfa.get(i).size() == 0) {
					deadStates.add(i);
				}
			}
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("Start state: %d\n", startState));
		sb.append(String.format("Final states: %s\n", finalStates.toString()));
		sb.append(String.format("Possible transitions: %s\n", transitions));
		int i = 0;
		for (Map<Character, Set<Integer>> map : nfa) {
			sb.append(String.format("State %d: %s", i, map));
			if ( i == startState) {
				sb.append(" [start]");
			}
			if ( finalStates.contains(i) ) {
				sb.append(" [final]");
			}
			sb.append("\n");
			i++;
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		NFA nfa1 = new NFA('a');
		System.out.println(nfa1.getEpsilonClosure(nfa1.getStartState()));
	}
}