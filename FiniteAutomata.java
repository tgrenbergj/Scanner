import java.util.*;

/**
 * A class with common properties found in both a NFA and DFA.
 */
public abstract class FiniteAutomata {

	protected int numStates;
	protected int numTransitions;
	protected int startState;
	protected Set<Integer> finalStates;
	
	public FiniteAutomata() {
		finalStates = new HashSet<Integer>();
	}
	
	/**
	 * Get the single start state of the automata.
	 * 
	 * @return The start state.
	 */
	public int getStartState() {
		return startState;
	}
	
	/**
	 * Set the start state of the automata.
	 * 
	 * @param startState The (already existing) start state.
	 */
	public void setStartState(int startState) {
		this.startState = startState;
	}
	
	/**
	 * Get all of the final states of an automata.
	 * 
	 * @return A set of integer states.
	 */
	public Set<Integer> getFinalStates() {
		return finalStates;
	}
	
	/**
	 * Set a state in the automata to be a final state
	 * 
	 * @param state The (already existing) state to become a final state.
	 */
	public void addFinalState(int state) {
		finalStates.add(state);
	}
	
	/**
	 * Get the number of states in the automata.
	 * 
	 * @return The number of states.
	 */
	public int getNumStates() {
		return numStates;
	}
	
	/**
	 * Get the number of transitions in the automata.
	 * 
	 * @return The number of transitions.
	 */
	public int getNumTransitions() {
		return numTransitions;
	}
}
