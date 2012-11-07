import java.util.*;

public class DFA extends FiniteAutomata {

	private int[][] dfa;
	
	public DFA(int numStates, int numTransitions) {
		this.numStates = numStates;
		this.numTransitions = numTransitions;
		dfa = new int[numStates][numTransitions];
	}
	
	public void addTransition(int start, int end, int transition) {
		dfa[start][transition] = end;
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
