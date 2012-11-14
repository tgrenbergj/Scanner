import java.util.*;

public class DFA extends FiniteAutomata {

	private int[][] dfa;
	private Map<Character, Integer> transitions;
	private Map<Integer, Character> revTransitions;
	private Map<Integer, String> tokenNames;
	private Set<Integer> deadStates;
	private static char EPSILON = (char) 169;
	
	/**
	 * Create a DFA with a pre-defined number of states and a list of possible
	 * single character transitions.
	 * 
	 * @param numStates The number of states the DFA will have
	 * @param transitions The character transitions the DFA will have
	 */
	public DFA(int numStates, Set<Character> transitions) {
		this.numStates = numStates;
		this.numTransitions = transitions.contains(EPSILON) ? transitions.size() - 1 : transitions.size();
		this.transitions = new HashMap<Character, Integer>();
		this.revTransitions = new HashMap<Integer, Character>();
		this.deadStates = new HashSet<Integer>();
		this.tokenNames = new HashMap<Integer, String>();
		
		int i = 0;
		//Add characters to an array so we can 
		//sort them for easy transition reading
		char[] trans = new char[numTransitions];
		for (Character c : transitions) {
			if (c != EPSILON) {
				trans[i] = c;
				i++;
			}
		}
		i = 0;
		Arrays.sort(trans);
		
		//Build an integer to character transition mapping,
		//and a reverse mapping
		for (Character c : trans) {
			if (c != EPSILON) {
				this.transitions.put(c, i);
				this.revTransitions.put(i,c);
				i++;
			}
		}
		
		dfa = new int[numStates][numTransitions];
	}
	
	/**
	 * Set a transition for a given character and start state
	 * 
	 * @param start The state to start on
	 * @param end The state to transition to
	 * @param transition The character to transition on from start to end
	 */
	public void addTransition(int start, int end, char transition) {
		dfa[start][transitions.get(transition)] = end;
	}
	
	/**
	 * Get all of the possible character transitions for this DFA
	 * @return A set of all possible single character transitions
	 */
	public Set<Character> getTransitions() {
		return transitions.keySet();
	}
	
	/**
	 * Get the next state number given a current state and character
	 */
	public int getNextState(int state, char transition) {
		if (!transitions.containsKey(transition))
			return -1;
		return dfa[state][transitions.get(transition)];
	}
	
	/**
	 * Check if a given state is a final state
	 * 
	 * @param state The state to check
	 * @return True if this state is a final state, false otherwise
	 */
	public boolean isFinalState(int state) {
		return finalStates.contains(state);
	}
	
	/**
	 * Check if a given state is a dead state
	 * 
	 * @param state The state to check
	 * @return True if this state is a dead state, false otherwise
	 */
	public boolean isDeadState(int state) {
		return deadStates.contains(state);
	}
	
	/**
	 * Associated a token name with a given state
	 * 
	 * @param state The state number
	 * @param name The token name
	 */
	public void addTokenName(int state, String name) {
		tokenNames.put(state, name);
	}
	
	/**
	 * Get the token name associated with a final state.
	 * 
	 * @param state The state to check
	 * @return The token name associated with the state
	 */
	public String getTokenName(int state) {
		return tokenNames.get(state);
	}
	
	/**
	 * Find all dead states in the DFA by seeing if a state only transitions
	 * back to itself, and it is not a final state.
	 */
	public void findDeadStates() {
		for ( int i = 0; i < dfa.length; i++) {
			if (!finalStates.contains(i)) {
				boolean same = true;
				for (int j = 0; same && j < dfa[i].length - 1; j++) {
					if (dfa[i][j] != dfa[i][j+1])
						same = false;
				}
				if (same) {
					deadStates.add(i);
				}
			}
			
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (int[] line : dfa) {
			sb.append(i + ": ");
			sb.append(Arrays.toString(line));
			if (startState == i) {
				sb.append(" -");
			} if (finalStates.contains(i)) {
				sb.append(" * " + tokenNames.get(i));
			}
			sb.append("\n");
			i++;
		}
		sb.append("Transitions:\n");
		for (i = 0; i < revTransitions.size(); i++) {
			sb.append(i + ": " + revTransitions.get(i) + "\n");
		}
		return sb.toString();
	}
}
