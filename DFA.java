import java.util.*;

/**
 * Represents a DFA, backed by an array.  The rows of the array are the
 * states of the DFA, and the columns are the single character transitions.
 * You can find which character maps to which column by using the HashMap
 * transitions, and do the reverse with revTransitions.
 */
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
				for (int j = 0; same && j < dfa[i].length; j++) {
					if (dfa[i][j] != i)
						same = false;
				}
				if (same) {
					deadStates.add(i);
				}
			}
			
		}
	}
	
	public String toString() {
		int longestState = ("" + dfa.length).length();
		StringBuilder sb = new StringBuilder();
		sb.append(transitionsToString(longestState));
		sb.append("\n");
		for (int i = 0; i < dfa.length; i++) {
			sb.append(stateToString(i, longestState));
			sb.append("\n");
		}
		sb.append(transitionsToString(longestState));
		return sb.toString();
	}
	
	private String stateToString(int state, int padding) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%"+padding+"d: ", state));
		sb.append("[");
		for (int i = 0; i < dfa[state].length; i++) {
			sb.append(String.format("%" + padding + "d, ", dfa[state][i]));
		}
		sb.delete(sb.length() - 2, sb.length());
		sb.append("]");
		if (startState == state) {
			sb.append(" -");
		} if (finalStates.contains(state)) {
			sb.append(" * " + tokenNames.get(state));
		}
		return sb.toString();
	}
	
	private String transitionsToString(int padding) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < padding + 2; i++) {
			sb.append(" ");
		}
		sb.append("<");
		for (int i = 0; i < numTransitions; i++) {
			sb.append(String.format("%" + padding + "s", revTransitions.get(i)));
			if (i != numTransitions - 1)
				sb.append(", ");
		}
		sb.append(">");
		return sb.toString();
	}
}
