import java.util.*;

/*
 * The first dimension is an index into the state of the NFA
 * The second dimension is an index into the transitions possible for the nfa
 * nfa[0...n-1][0] will always contain a set of epsilon transitions for that state
 */

public class NFA extends FiniteAutomata {

	private Set<Integer>[][] nfa;
	
	public NFA() {
		testNFA4();
	}
	
	@Override
	public void addTransition(int start, int end, int transition) {
		nfa[start][transition].add(end);
	}
	
	public Set<Integer> getNextStates(int state, int transition) {
		return nfa[state][transition];
	}
	
	public Set<Integer> getNextStates(Set<Integer> states, int transition) {
		Set<Integer> nextStates = new HashSet<Integer>();
		for (Integer state : states) {
			nextStates.addAll(nfa[state][transition]);
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
			for (Integer i : nfa[current][0]) {
				if (!visited.contains(i)) {
					closure.add(i);
					visited.add(i);
					queue.add(i);
				}
			}
		}
		return closure;
	}
	
	@SuppressWarnings("unchecked")	
	private void testNFA1() {
		//Homework 2 problem
		numTransitions = 3;
		numStates = 13;
		startState = 0;
		finalStates = new HashSet<Integer>();
		finalStates.add(9);
		finalStates.add(12);
		nfa = new HashSet[numStates][numTransitions];
		
		for (int i = 0; i < nfa.length; i++) {
			for (int j = 0; j < nfa[i].length; j++) {
				nfa[i][j] = new HashSet<Integer>();
			}
		}
		
		nfa[0][0].add(1);
		nfa[0][0].add(6);
		
		nfa[1][0].add(2);
		nfa[1][0].add(3);
		
		nfa[2][1].add(4);
		
		nfa[3][2].add(5);
		
		nfa[4][0].add(1);
		nfa[4][0].add(6);
		
		nfa[5][0].add(1);
		nfa[5][0].add(6);
		
		nfa[6][1].add(7);
		
		nfa[7][2].add(8);
		
		nfa[8][0].add(9);
		
		nfa[9][0].add(10);
		
		nfa[10][1].add(11);
		
		nfa[11][2].add(12);
		
		nfa[12][0].add(10);
	}
	
	@SuppressWarnings("unchecked")	
	private void testNFA2() {
		//http://www.cs.gsu.edu/~cscskp/Automata/FA/node12.html
		numTransitions = 3;
		numStates = 3;
		startState = 0;
		finalStates = new HashSet<Integer>();
		finalStates.add(2);
		nfa = new HashSet[numStates][numTransitions];
		
		for (int i = 0; i < nfa.length; i++) {
			for (int j = 0; j < nfa[i].length; j++) {
				nfa[i][j] = new HashSet<Integer>();
			}
		}
		
		nfa[0][1].add(0);
		nfa[0][1].add(1);
		nfa[0][2].add(0);
		
		nfa[1][2].add(2);
	}

	@SuppressWarnings("unchecked")	
	private void testNFA3() {
		//http://www.jflap.org/tutorial/fa/nfa2dfa/index.html
		numTransitions = 3;
		numStates = 4;
		startState = 0;
		finalStates = new HashSet<Integer>();
		finalStates.add(3);
		nfa = new HashSet[numStates][numTransitions];
		
		for (int i = 0; i < nfa.length; i++) {
			for (int j = 0; j < nfa[i].length; j++) {
				nfa[i][j] = new HashSet<Integer>();
			}
		}
		
		nfa[0][1].add(1);
		nfa[0][1].add(2);
		nfa[0][2].add(1);
		nfa[0][2].add(3);
		
		nfa[1][1].add(1);
		nfa[1][1].add(3);
		
		nfa[2][0].add(0);
		
		nfa[3][1].add(1);
		nfa[3][2].add(2);
		
	}
	
	@SuppressWarnings("unchecked")	
	private void testNFA4() {
		//http://www.jflap.org/tutorial/fa/nfa2dfa/index.html
		numTransitions = 3;
		numStates = 3;
		startState = 0;
		finalStates = new HashSet<Integer>();
		finalStates.add(0);
		finalStates.add(2);
		nfa = new HashSet[numStates][numTransitions];
		
		for (int i = 0; i < nfa.length; i++) {
			for (int j = 0; j < nfa[i].length; j++) {
				nfa[i][j] = new HashSet<Integer>();
			}
		}
		
		nfa[0][0].add(1);
		nfa[0][0].add(2);
		
		nfa[1][1].add(0);
		nfa[1][2].add(0);
	}
}
