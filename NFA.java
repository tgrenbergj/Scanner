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
	private static char EPSILON = (char) 169;
	
	/**
	 * Create a new NFA that recognizes a single character.
	 * 
	 * @param c The only character this NFA will recognize
	 */
	public NFA(char c) {
		this();
		
		nfa.add(0, new HashMap<Character, Set<Integer>>());
		setStartState(0);
		
		nfa.add(1, new HashMap<Character, Set<Integer>>());
		this.addFinalState(1);
		
		addTransition(0, 1, c);
	}
	
	/**
	 * Initialize a completely empty NFA with no states or transitions.
	 */
	public NFA() {
		nfa = new ArrayList<Map<Character, Set<Integer>>>();
		transitions = new HashSet<Character>();
		tokenNames = new HashMap<Integer, Set<String>>();
	}
	
	/**
	 * Add a transition from one state to another in the NFA on a character.
	 * 
	 * @param start The start state
	 * @param end The end state
	 * @param transition The character to transition from start to end
	 */
	public void addTransition(int start, int end, char transition) {
		transitions.add(transition);
		if (!nfa.get(start).containsKey(transition))
			nfa.get(start).put(transition, new HashSet<Integer>());
		nfa.get(start).get(transition).add(end);
	}
	
	/**
	 * Add another empty state to the NFA.
	 */
	public void addState() {
		nfa.add(new HashMap<Character, Set<Integer>>());
	}
	
	/**
	 * Add an epsilon transition from one state to another.
	 * 
	 * @param start The start state
	 * @param end The end state
	 */
	public void addEpsilonTransition(int start, int end) {
		addTransition(start, end, EPSILON);
	}
	
	/**
	 * Get all of the states and transitions of the NFA.
	 * 
	 * @return The backing data of the NFA.
	 */
	public List<Map<Character, Set<Integer>>> getNFA() {
		return nfa;
	}
	
	/**
	 * Get all of the possible character transitions in this NFA.
	 * 
	 * @return A set of all the possible character transitions in this NFA.
	 */
	public Set<Character> getTransitions() {
		return transitions;
	}
	
	public int getNumTransitions() {
		return transitions.size();
	}
	
	public int getNumStates() {
		return nfa.size();
	}
	
	/**
	 * Associate a token name with all of the final states in this NFA.
	 * 
	 * @param name The name of the token class
	 */
	public void addTokenName(String name) {
		for (Integer i : finalStates) {
			if (!tokenNames.containsKey(i)) {
				tokenNames.put(i, new HashSet<String>());
			}
			tokenNames.get(i).add(name);
		}
	}
	
	/**
	 * Associate a specific state with a token name
	 * @param state The state to associate the token name with
	 * @param name The token name
	 */
	public void addTokenName(int state, String name) {
		if (!tokenNames.containsKey(state)) {
			tokenNames.put(state, new HashSet<String>());
		}
		tokenNames.get(state).add(name);
	}
	
	/**
	 * Get a set of all token names associated with a final state
	 * @param state The state to get the token names of
	 * @return A set of token names
	 */
	public Set<String> getTokenNames(int state) {
		if (!finalStates.contains(state)) {
			return null;
		} else {
			return tokenNames.get(state);
		}
	}
	
	/**
	 * Get all of the states you can get to from a start state and a character.
	 * 
	 * @param state The state to start from.
	 * @param transition The character to transition with. 
	 * @return A set of states transitioned to.
	 */
	public Set<Integer> getNextStates(int state, char transition) {
		return nfa.get(state).get(transition);
	}
	
	/**
	 * Get all of the states you can get to from a set of start states
	 * and a character.
	 * 
	 * @param states The set of states to start from.
	 * @param transition The character to transition with. 
	 * @return  A set of states transitioned to.
	 */
	public Set<Integer> getNextStates(Set<Integer> states, char transition) {
		Set<Integer> nextStates = new HashSet<Integer>();
		for (Integer state : states) {
			if (nfa.get(state).containsKey(transition)) {
				nextStates.addAll(nfa.get(state).get(transition));
			}
		}
		return nextStates;
	}
	
	/**
	 * @param states A set of start states
	 * @return A set of states resulting from the epsilon closure.
	 */
	public Set<Integer> getEpsilonClosure(Set<Integer> states) {
		Set<Integer> closure = new HashSet<Integer>();
		for (int i : states) {
			closure.addAll(getEpsilonClosure(i));
		}
		return closure;
	}
	
	/**
	 * 
	 * @param state The state to start from.
	 * @return A set of states resulting from the epsilon closure.
	 */
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
}