import java.util.*;

/**
 * Methods that perform static operations on NFAs.  This includes union,
 * concatenation, star, etc.  All functions do not alter the original NFA.
 */
public class NFATools {

	private static char EPSILON = (char) 169;
	
	/**
	 * Returns the union of NFAs that do not have token names associated
	 * with them.
	 * 
	 * @param nfas The NFAs you want to union
	 * @return The union of multiple NFAs
	 */
	public static NFA union(NFA ... nfas) {
		return unionAll(false, nfas);
	}
	
	/**
	 * Returns the union of many NFAs, and keeps track of which tokens
	 * are associated with which final states.
	 * 
	 * @param doTokens Whether or not these NFAs have token names.
	 * @param nfas The NFAs you want to union
	 * @return The union of multiple NFAs
	 */
	public static NFA unionAll(boolean doTokens, NFA ... nfas) {
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
				if (doTokens) {
					for (String name : nfa.getTokenNames(state)) {
						newNFA.addTokenName(newFinalState, name);
					}
				}
			}
			
			offset = current;
		}

		return newNFA;
	}
	
	/**
	 * Concatenates two NFAs together.
	 * 
	 * @param nfa1 The NFA to start on
	 * @param nfa2 The NFA to end on
	 * @return A concatenated NFA
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
	
	/**
	 * Converts an NFA to have a Kleene star operation. Will now accept
	 * 0 or more of the passed in NFA.
	 * 
	 * @param nfa1 The NFA to do a Kleene star operation on
	 * @return The previous NFA with an added Kleene star operation on it
	 */
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
	
	
	/**
	 * Does the plus operation on an NFA.  Will return an NFA that accepts
	 * one or more of the NFA that was passed in.
	 * 
	 * @param nfa1 The NFA to do the plus operation on
	 * @return An NFA with the plus operation performed on it.
	 */
	public static NFA plus(NFA nfa1) {
		return NFATools.concat(nfa1, NFATools.star(nfa1));
	}
	

	/**
	 * Basically take an NFA that only accepts a character class, and reduce
	 * the number of states it needs to represent that NFA.
	 * 
	 * @param nfa1 The NFA to compress.
	 * @return A compressed NFA.
	 */
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
	
	
	/**
	 * A method to remove one character class from another.
	 * 
	 * @param nfa1 The main NFA character class
	 * @param nfa2 The NFA character class you want to subtract from nfa1
	 * @return The new character class of (nfa1 - nfa2).
	 */
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
