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
		if (!transitions.containsKey(transition))
			return -1;
		return dfa[state][transitions.get(transition)];
	}
	
	public boolean isFinalState(int state) {
		return finalStates.contains(state);
	}
	
	public boolean isDeadState(int state) {
		return deadStates.contains(state);
	}
	
	public void addTokenName(int state, String name) {
		tokenNames.put(state, name);
	}
	
	public String getTokenName(int state) {
		return tokenNames.get(state);
	}
	
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
