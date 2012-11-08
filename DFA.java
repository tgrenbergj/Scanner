import java.util.*;

public class DFA extends FiniteAutomata {

	private int[][] dfa;
	private Map<Character, Integer> transitions;
	private static char EPSILON = (char) 169;
	
	public DFA(int numStates, Set<Character> transitions) {
		this.numStates = numStates;
		this.numTransitions = transitions.size() - 1;
		int i = 0;
		Character[] trans = (Character[]) transitions.toArray();
		Arrays.sort(trans);
		for (Character c : trans) {
			if (c != EPSILON) {
				this.transitions.put(c, i);
				i++;
			}
		}
		dfa = new int[numStates][numTransitions];
	}
	
	public void addTransition(int start, int end, char transition) {
		dfa[start][transitions.get(transition)] = end;
	}
	
	public int getNextState(int state, char transition) {
		return dfa[state][transitions.get(transition)];
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
		return sb.toString();
	}
}
