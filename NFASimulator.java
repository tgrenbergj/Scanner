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
		char curChar;
		StringBuilder validToken = new StringBuilder();
		nextStates.addAll(nfa.getEpsilonClosure(curState));
		nextStates.add(curState);
		maxClones.add(nextStates.size());
		Set<Integer> cache = new HashSet<Integer>();
		String currentIdent = "";
		int totalTransitions = nextStates.size();
		while(peek() != -1 && peek() != 65535){ //while there are more characters we keep getting them and testing them
			curChar = (char)reader.read();
			cache.addAll(nextStates);
			maxClones.add(nextStates.size());
			Set<Integer> currentTransitions = nfa.getNextStates(nextStates, curChar);
			nextStates.clear();
			nextStates.addAll(currentTransitions);
			//Take all the epsilon moves we can from the states we just added using curChar
			nextStates.addAll(nfa.getEpsilonClosure(currentTransitions));

			totalTransitions += nextStates.size();
			boolean allNull = true;
			for(int st:nextStates){
				if(nfa.getTokenNames(st)!=null){
					allNull = false;
				}
			}
			//If our current character yeilded to no valid states, or to null states, we should take action as it is invalid
			if(nextStates.size()==0|| allNull){
				if(validToken.length()>0 && !currentIdent.equals("INVALID")){ //If there is a token that is valid in the current list
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
					//Here we see if the next character makes it valid, if so then we leave it be and push it back, if not then we add on
					// to the current list of invalid characters and move on
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
					else{//Current token is fine, add it and unread the token we just read.
						validToken.append(curChar);
						reader.unread(next);	
					}
				}//if we encountered an illegal character while nothing before this is illegal we mark our current
				// copy of token as invalid
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
					}//If the current token is illegal but the character we just read is legal
					//we should end the current token and start off with the new token(so we push it back)
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
					//We just check to make sure that if we can go somewher with the new character
					// we restart our process from the beginning as the next character\
					//is a different type, we push it back so we read it next iteration
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
			}//If the current character leads us to a valid state
			else{
				validToken.append(curChar); //Add teh character to our token type
				for(int tran: nextStates){
					if(nfa.getTokenNames(tran)!= null && nfa.getTokenNames(tran).size()!=0){
						currentIdent = nfa.getTokenNames(tran).toString(); //update our current token type to reflect changes
						currentIdent = currentIdent.replace("[", "");
						currentIdent = currentIdent.replace("]", "");
						break;
					}
				}
				
				if(peek()==-1 || peek()==65535){ //If the next character makes us invalid we should end the current valid and start over
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
	//Helps to figure out if a string is whitespace or not
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
