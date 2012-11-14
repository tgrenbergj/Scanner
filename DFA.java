import java.util.*;

public class DFA extends FiniteAutomata {

	private int[][] dfa;
	private Map<Character, Integer> transitions;
	private Map<Integer, Character> revTransitions;
	private Map<Integer, String> tokenNames;
	private Set<Integer> deadStates;
	private static char EPSILON = (char) 169;
	
	public DFA(int numStates, Set<Character> transitions) {
		this.numStates = numStates;
		this.numTransitions = transitions.contains(EPSILON) ? transitions.size() - 1 : transitions.size();
		this.transitions = new HashMap<Character, Integer>();
		this.revTransitions = new HashMap<Integer, Character>();
		
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
	 */
	public void addTransition(int start, int end, char transition) {
		dfa[start][transitions.get(transition)] = end;
	}
	
	/**
	 * Get all of the possible character transitions for this DFA
	 */
	public Set<Character> getTransitions() {
		return transitions.keySet();
	}
	
	/**
	 * Get the next state number given a current state and character
	 */
	public int getNextState(int state, char transition) {
		return dfa[state][transitions.get(transition)];
	}
	
	public boolean isFinalState(int state) {
		return finalStates.contains(state);
	}
	
	public boolean isDeadState(int state) {
		return deadStates.contains(state);
	}
	
	public String getTokenName(int state) {
		return tokenNames.get(state);
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
				sb.append(" *");
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
