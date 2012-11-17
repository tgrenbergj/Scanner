import java.io.*;
import java.util.*;


public class NFASimulator {
	private NFA nfa;
	private PushbackReader reader;
	private List<Integer> maxClones = new ArrayList<Integer>(); //holds max number of clones
	private Set<Integer> nextStates = new HashSet<Integer>(); //All the next states returned by nfa
	int cloneCount = 0;
	
	public NFASimulator(NFA nfa, String file)throws FileNotFoundException{
		this.nfa = nfa;
		this.reader = new PushbackReader(new FileReader(file),1000);
	}
	
	public void simulate() throws IOException{
		int curState = nfa.getStartState();
		Set<Integer> a;
		char curChar;
		StringBuilder validToken = new StringBuilder();
		char epsilon = (char)169;
		nextStates.addAll(nfa.getEpsilonClosure(curState));
		nextStates.add(curState);
		maxClones.add(nextStates.size());
		Set<Integer> finalStates = nfa.getFinalStates();
		Set<Integer> cache = new HashSet<Integer>();
		String currentIdent = "";
		int totalTransitions = nextStates.size();
		while(peek() != -1 && peek() != 65535){
			curChar = (char)reader.read();
			cache.addAll(nextStates);
			maxClones.add(nextStates.size());
			Set<Integer> currentTransitions = nfa.getNextStates(nextStates, curChar);
			nextStates.clear();
			nextStates.addAll(currentTransitions);

			nextStates.addAll(nfa.getEpsilonClosure(currentTransitions));

			totalTransitions += nextStates.size();
			boolean allNull = true;
			for(int st:nextStates){
				if(nfa.getTokenNames(st)!=null){
					allNull = false;
				}
			}
			if(nextStates.size()==0|| allNull){
				if(validToken.length()>0 && !currentIdent.equals("INVALID")){ //If there is a token that is valid
					if(peek()==-1 || peek()==65535){
						System.out.println(currentIdent + validToken);
						currentIdent = "INVALID";
						validToken = new StringBuilder();
						validToken.append(curChar);
						break;
					}
					char next = (char)reader.read();
					currentTransitions.clear();
					currentTransitions.addAll(nfa.getNextStates(nextStates, next));
					currentTransitions.addAll(nfa.getEpsilonClosure(currentTransitions));
					if(currentTransitions.size()==0){
						validToken.append(curChar);
						validToken = new StringBuilder();
						currentIdent = "";
						reader.unread(next);
						nextStates.clear();
						nextStates.add(curState);
						nextStates.addAll(nfa.getEpsilonClosure(curState));
						totalTransitions += nextStates.size();
					}
					else{
						validToken.append(curChar);
						reader.unread(next);	
					}
				}
				else if(currentIdent.equals("")){
					if(validToken.length()!=0)
					System.out.println("Valid char should be empty" + validToken);
					validToken = new StringBuilder();
					validToken.append(curChar);
					currentIdent = "INVALID";
				}
				else if(currentIdent.equals("INVALID")){ //If there is an invalid token before us but this makes us valid then we should print invalid
					//Lets check if this takes us from invalid to valid
				//	char next = (char)reader.read();
					currentTransitions.clear();
					currentTransitions.addAll(nfa.getNextStates(nextStates, curChar));
					currentTransitions.addAll(nfa.getEpsilonClosure(currentTransitions));
					boolean nulls = true;
					for(int st:currentTransitions){
						if(nfa.getTokenNames(st)!=null){
							nulls = false;
						}
					}
					if(currentTransitions.size()!=0 || !nulls){
						validToken.append(curChar);
						nextStates.clear();
						nextStates.addAll(currentTransitions);
						continue;
					}
					Set<Integer> begin = new HashSet<Integer>();
					begin.add(curState);
					begin.addAll(nfa.getEpsilonClosure(curState));
					currentTransitions.clear();
					currentTransitions.addAll(nfa.getNextStates(begin, curChar));
					currentTransitions.addAll(nfa.getEpsilonClosure(currentTransitions));
					
					if(currentTransitions.size()!=0){
						nextStates.clear();
						nextStates.add(curState);
						nextStates.addAll(nfa.getEpsilonClosure(curState));
						if(!isWhitespace(validToken.toString()))
							System.out.println(currentIdent +" " +validToken.toString().trim());
						currentIdent = "";
						validToken = new StringBuilder();
						reader.unread(curChar);
					}
					else{
						validToken.append(curChar);
					}
				}
				else if(!isWhitespace(curChar+"")){
					char next = (char)reader.read();
					Set<Integer> begin = new HashSet<Integer>();
					begin.add(curState);
					begin.addAll(nfa.getEpsilonClosure(curState));
					currentTransitions.clear();
					currentTransitions.addAll(nfa.getNextStates(begin, next));
					currentTransitions.addAll(nfa.getEpsilonClosure(currentTransitions));
					if(currentTransitions.size()!=0){
						nextStates.clear();
						nextStates.add(curState);
						nextStates.addAll(nfa.getEpsilonClosure(curState));
						if(!isWhitespace(validToken.toString()))
							System.out.println(currentIdent +" " +validToken.toString().trim());
						currentIdent = "";
						validToken = new StringBuilder();
						reader.unread(next);
					}
					else{
						validToken.append(curChar);
					}
				}
			}
			else{
				validToken.append(curChar);
				for(int tran: nextStates){
					if(nfa.getTokenNames(tran)!= null && nfa.getTokenNames(tran).size()!=0){
						currentIdent = nfa.getTokenNames(tran).toString();
						currentIdent = currentIdent.replace("[", "");
						currentIdent = currentIdent.replace("]", "");
						break;
					}
				}
				
				if(peek()==-1 || peek()==65535){
					if(!isWhitespace(validToken.toString()))
					System.out.println(currentIdent + " "+ validToken.toString().trim());
					currentIdent = "";
					validToken = new StringBuilder();
					break;
				}
				char next = (char)reader.read();
				reader.unread((int)next);
				currentTransitions.clear();
				currentTransitions.addAll(nfa.getNextStates(nextStates, next));
				currentTransitions.addAll(nfa.getEpsilonClosure(currentTransitions));
				if(currentTransitions.size()==0){
					if(!isWhitespace(validToken.toString()))
					System.out.println(currentIdent + " "+ validToken.toString().trim());
					validToken = new StringBuilder();
					currentIdent = "";
					nextStates.clear();
					nextStates.add(curState);
					nextStates.addAll(nfa.getEpsilonClosure(curState));
					totalTransitions += nextStates.size();
				}		
			}
		}
		if(!isWhitespace(validToken.toString()))
		System.out.println(currentIdent + " "+ validToken.toString().trim());
		System.out.println("Total transitions made "+ totalTransitions);
		System.out.println("Max Clones made by NFA " + Collections.max(maxClones));
	}

	//Look forward in the input stream
	public int peek() throws IOException {
		int c = reader.read();
		reader.unread(c);
		return c;
	}
	public boolean isWhitespace(String s) {
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c != ' ' && c != '\t'&& c != '\n' && c != '\r') {
				return false;
			}
		}
		return true;
	}
	public static void main(String[] args) throws Exception {
		SpecificationReader sr = new SpecificationReader("test_spec.txt");
		NFA nfa = sr.run();
		NFASimulator sim = new NFASimulator(nfa,"test_input.txt");
		sim.simulate();
	}

}
