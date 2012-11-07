import java.util.*;

/*
 * The first dimension is an index into the state of the NFA
 * The second dimension is an index into the transitions possible for the nfa
 * nfa[0...n-1][0] will always contain a set of epsilon transitions for that state
 */

public class NFA extends FiniteAutomata {

	private ArrayList<Map<Character, Set<Integer>>> nfa;
	private Set<Character> transitions;
	private static char EPSILON = 'ø';
	
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
	
	public Set<Character> getTransitions() {
		return transitions;
	}
	
	public int getNumTransitions() {
		return transitions.size();
	}
	
	public int getNumStates() {
		return nfa.size();
	}
	
	public Set<Integer> getNextStates(int state, char transition) {
		return nfa.get(state).get(transition);
	}
	
	public Set<Integer> getNextStates(Set<Integer> states, int transition) {
		Set<Integer> nextStates = new HashSet<Integer>();
		for (Integer state : states) {
			nextStates.addAll(nfa.get(state).get(transition));
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
			for (Integer i : nfa.get(current).get(EPSILON)) {
				if (!visited.contains(i)) {
					closure.add(i);
					visited.add(i);
					queue.add(i);
				}
			}
		}
		return closure;
	}
	
	public static NFA union(NFA nfa1, NFA nfa2) {
		NFA newNFA = new NFA();
		newNFA.addState();
		newNFA.setStartState(0);
		
		int current = 1;
		int offset = 1;
		
		for (Map<Character, Set<Integer>> map : nfa1.nfa) {
			newNFA.addState();
			for(Character c : map.keySet()) {
				Set<Integer> next = map.get(c);
				for (Integer end : next) {
					newNFA.addTransition(current, end + offset, c);
				}
			}
			current++;
		}
		newNFA.addEpsilonTransition(newNFA.getStartState(), nfa1.getStartState() + offset);
		for (Integer state : nfa1.finalStates) {
			newNFA.addFinalState(state + offset);
		}
		
		offset = current;
		
		for (Map<Character, Set<Integer>> map : nfa2.nfa) {
			newNFA.addState();
			for(Character c : map.keySet()) {
				Set<Integer> next = map.get(c);
				for (Integer end : next) {
					newNFA.addTransition(current, end + offset, c);
				}
			}
			current++;
		}
		
		newNFA.addEpsilonTransition(newNFA.getStartState(), nfa2.getStartState() + offset);
		for (Integer state : nfa2.finalStates) {
			newNFA.addFinalState(state + offset);
		}
		
		return newNFA;
	}
	
	public static NFA concat(NFA nfa1, NFA nfa2) {
		NFA newNFA = new NFA();
		
		newNFA.setStartState(nfa1.getStartState());
		
		int current = 0;
		int offset = 0;
		
		for (Map<Character, Set<Integer>> map : nfa1.nfa) {
			newNFA.addState();
			for(Character c : map.keySet()) {
				Set<Integer> next = map.get(c);
				for (Integer end : next) {
					newNFA.addTransition(current, end + offset, c);
				}
			}
			current++;
		}
		
		offset = current;
		
		for (Map<Character, Set<Integer>> map : nfa2.nfa) {
			newNFA.addState();
			for(Character c : map.keySet()) {
				Set<Integer> next = map.get(c);
				for (Integer end : next) {
					newNFA.addTransition(current, end + offset, c);
				}
			}
			current++;
		}
		
		for (Integer state : nfa1.getFinalStates()) {
			newNFA.addEpsilonTransition(state, nfa2.getStartState() + offset);
		}
		
		for (Integer state : nfa2.getFinalStates()) {
			newNFA.addFinalState(state + offset);
		}				
		
		return newNFA;
	}
	
	public static NFA star(NFA nfa1) {
		NFA newNFA = new NFA();
		newNFA.addState();
		newNFA.setStartState(0);
		newNFA.addFinalState(0);
		int offset = 1;
		int current = 1;
		
		for (Map<Character, Set<Integer>> map : nfa1.nfa) {
			newNFA.addState();
			for(Character c : map.keySet()) {
				Set<Integer> next = map.get(c);
				for (Integer end : next) {
					newNFA.addTransition(current, end + offset, c);
				}
			}
			current++;
		}
		
		for (Integer end : nfa1.getFinalStates()) {
			newNFA.addFinalState(end + offset);
			newNFA.addEpsilonTransition(end + offset, nfa1.getStartState() + offset);
		}
		
		newNFA.addEpsilonTransition(newNFA.getStartState(), nfa1.getStartState() + offset);
		
		return newNFA;
	}
	
	public static NFA plus(NFA nfa1) {
		return NFA.concat(nfa1, NFA.star(nfa1));
	}
	
	
	
	public static void main(String[] args) {
		NFA nfa1 = new NFA('a');
		NFA concatnfa = NFA.concat(nfa1, nfa1);
		NFA unionnfa = NFA.union(concatnfa, nfa1);
		
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("Start state: %d\n", startState));
		sb.append(String.format("Final states: %s\n", finalStates.toString()));
		int i = 0;
		for (Map<Character, Set<Integer>> map : nfa) {
			sb.append(String.format("State %d: %s\n", i++, map));
		}
		return sb.toString();
	}
//	@SuppressWarnings("unchecked")	
//	private void testNFA1() {
//		//Homework 2 problem
//		numTransitions = 3;
//		numStates = 13;
//		startState = 0;
//		finalStates = new HashSet<Integer>();
//		finalStates.add(9);
//		finalStates.add(12);
//		nfa = new HashSet[numStates][numTransitions];
//		
//		for (int i = 0; i < nfa.length; i++) {
//			for (int j = 0; j < nfa[i].length; j++) {
//				nfa[i][j] = new HashSet<Integer>();
//			}
//		}
//		
//		nfa[0][0].add(1);
//		nfa[0][0].add(6);
//		
//		nfa[1][0].add(2);
//		nfa[1][0].add(3);
//		
//		nfa[2][1].add(4);
//		
//		nfa[3][2].add(5);
//		
//		nfa[4][0].add(1);
//		nfa[4][0].add(6);
//		
//		nfa[5][0].add(1);
//		nfa[5][0].add(6);
//		
//		nfa[6][1].add(7);
//		
//		nfa[7][2].add(8);
//		
//		nfa[8][0].add(9);
//		
//		nfa[9][0].add(10);
//		
//		nfa[10][1].add(11);
//		
//		nfa[11][2].add(12);
//		
//		nfa[12][0].add(10);
//	}
//	
//	@SuppressWarnings("unchecked")	
//	private void testNFA2() {
//		//http://www.cs.gsu.edu/~cscskp/Automata/FA/node12.html
//		numTransitions = 3;
//		numStates = 3;
//		startState = 0;
//		finalStates = new HashSet<Integer>();
//		finalStates.add(2);
//		nfa = new HashSet[numStates][numTransitions];
//		
//		for (int i = 0; i < nfa.length; i++) {
//			for (int j = 0; j < nfa[i].length; j++) {
//				nfa[i][j] = new HashSet<Integer>();
//			}
//		}
//		
//		nfa[0][1].add(0);
//		nfa[0][1].add(1);
//		nfa[0][2].add(0);
//		
//		nfa[1][2].add(2);
//	}
//
//	@SuppressWarnings("unchecked")	
//	private void testNFA3() {
//		//http://www.jflap.org/tutorial/fa/nfa2dfa/index.html
//		numTransitions = 3;
//		numStates = 4;
//		startState = 0;
//		finalStates = new HashSet<Integer>();
//		finalStates.add(3);
//		nfa = new HashSet[numStates][numTransitions];
//		
//		for (int i = 0; i < nfa.length; i++) {
//			for (int j = 0; j < nfa[i].length; j++) {
//				nfa[i][j] = new HashSet<Integer>();
//			}
//		}
//		
//		nfa[0][1].add(1);
//		nfa[0][1].add(2);
//		nfa[0][2].add(1);
//		nfa[0][2].add(3);
//		
//		nfa[1][1].add(1);
//		nfa[1][1].add(3);
//		
//		nfa[2][0].add(0);
//		
//		nfa[3][1].add(1);
//		nfa[3][2].add(2);
//		
//	}
//	
//	@SuppressWarnings("unchecked")	
//	private void testNFA4() {
//		//http://www.jflap.org/tutorial/fa/nfa2dfa/index.html
//		numTransitions = 3;
//		numStates = 3;
//		startState = 0;
//		finalStates = new HashSet<Integer>();
//		finalStates.add(0);
//		finalStates.add(2);
//		nfa = new HashSet[numStates][numTransitions];
//		
//		for (int i = 0; i < nfa.length; i++) {
//			for (int j = 0; j < nfa[i].length; j++) {
//				nfa[i][j] = new HashSet<Integer>();
//			}
//		}
//		
//		nfa[0][0].add(1);
//		nfa[0][0].add(2);
//		
//		nfa[1][1].add(0);
//		nfa[1][2].add(0);
//	}
}
