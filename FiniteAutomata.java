import java.util.*;

public abstract class FiniteAutomata {

	protected int numStates;
	protected int numTransitions;
	protected int startState;
	protected Set<Integer> finalStates;
	
	public FiniteAutomata() {
		finalStates = new HashSet<Integer>();
	}
	
	public int getStartState() {
		return startState;
	}
	
	public void setStartState(int startState) {
		this.startState = startState;
	}
	
	public Set<Integer> getFinalStates() {
		return finalStates;
	}
	
	public void addFinalState(int state) {
		finalStates.add(state);
	}
	
	public int getNumStates() {
		return numStates;
	}
	
	public int getNumTransitions() {
		return numTransitions;
	}
	
	public abstract void addTransition(int start, int end, int transition);
}
