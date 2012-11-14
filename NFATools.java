import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class NFATools {

	private static char EPSILON = (char) 169;
	
	public static NFA union(NFA ... nfas) {
		NFA newNFA = new NFA();
		newNFA.addState();
		newNFA.setStartState(0);
		
		int current = 1;
		int offset = 1;
		for (NFA nfa : nfas) {
			
			for (Map<Character, Set<Integer>> map : nfa.getNFA()) {
				newNFA.addState();
				for(Character c : map.keySet()) {
					Set<Integer> next = map.get(c);
					for (Integer end : next) {
						newNFA.addTransition(current, end + offset, c);
					}
				}
				current++;
			}
			newNFA.addEpsilonTransition(newNFA.getStartState(), nfa.getStartState() + offset);
			for (Integer state : nfa.finalStates) {
				int newFinalState = state + offset;
				newNFA.addFinalState(newFinalState);
				for (String name : nfa.getTokenNames(state)) {
					newNFA.addTokenName(newFinalState, name);
				}
			}
			
			offset = current;
		}

		return newNFA;
	}
	
	/**
	 * Take two NFAs and return their concatenation
	 */
	public static NFA concat(NFA nfa1, NFA nfa2) {
		NFA newNFA = new NFA();
		
		newNFA.setStartState(nfa1.getStartState());
		
		int current = 0;
		int offset = 0;
		
		for (Map<Character, Set<Integer>> map : nfa1.getNFA()) {
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
		
		for (Map<Character, Set<Integer>> map : nfa2.getNFA()) {
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
		
		for (Map<Character, Set<Integer>> map : nfa1.getNFA()) {
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
		return NFATools.concat(nfa1, NFATools.star(nfa1));
	}
	
	//NOTE: ONLY WORKS ON CHARACTER CLASSES
	public static NFA compress(NFA nfa1) {
		NFA newNFA = new NFA();
		newNFA.addState();
		newNFA.setStartState(0);
		int current = 1;
		for (Character c: nfa1.getTransitions()) {
			if ( c != EPSILON) {
				newNFA.addState();
				newNFA.addTransition(newNFA.getStartState(), current, c);
				newNFA.addFinalState(current);
				current++;
			}
		}
		return newNFA;
	}
	
	
	//nfa1 is the old set, and nfa2 is what I want to subtract from nfa1
	public static NFA minus(NFA nfa1, NFA nfa2) {
		Set<Character> nfa1set = nfa1.getTransitions();
		Set<Character> nfa2set = nfa2.getTransitions();
		Set<Character> newset = new HashSet<Character>();
		for (Character c: nfa1set) {
			if (!nfa2set.contains(c))
				newset.add(c);
		}
		
		NFA newNFA = new NFA();
		newNFA.addState();
		newNFA.setStartState(0);
		int current = 1;
		for (Character c: newset) {
			if ( c != EPSILON) {
				newNFA.addState();
				newNFA.addTransition(newNFA.getStartState(), current, c);
				newNFA.addFinalState(current);
				current++;
			}
		}
		return newNFA;
	}
	
}
