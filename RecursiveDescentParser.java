import java.io.*;
import java.util.*;
//Parser

public class RecursiveDescentParser {
	PushbackInputStream in;
	
	Set<String> RE_CHAR;
	Set<String> CLS_CHAR;
	public RecursiveDescentParser(String s) {
		in = new PushbackInputStream(new ByteArrayInputStream(s.getBytes()));
		RE_CHAR = new HashSet<String>();
		CLS_CHAR = new HashSet<String>();
		buildSets();
	}
	
	public NFA rexp() throws IOException {
		readSpaces();
		NFA rexp1 = rexp1();
		NFA rexpprime = null;
		if ( peek() == '|') {
			rexpprime = rexpprime();
		}
		if (rexp1 != null && rexpprime != null) {
			return NFA.union(rexp1, rexpprime);
		} else {
			return rexp1;
		}
	}
	
	public NFA rexpprime() throws IOException {
		readSpaces();
		if (peek() == '|') {
			in.read(); // get the pipe
			NFA rexp1 = rexp1();
			NFA rexpprime = rexpprime();
			if (rexpprime == null) {
				return rexp1;
			}
			return NFA.concat(rexp1, rexpprime);
		} else {
			return null;
		}
		
	}
	public NFA rexp1() throws IOException {
		readSpaces();
		NFA rexp2 = rexp2();
		if ( rexp2 == null )
			return null;
		NFA rexp1prime = rexp1prime();
		if (rexp1prime == null)
			return rexp2;
		return NFA.concat(rexp2, rexp1prime);
	}
	
	public NFA rexp1prime() throws IOException {
		readSpaces();

		NFA rexp2 = rexp2();
		if (rexp2 == null) {
			return null;
		}
		NFA rexp1prime = rexp1prime();
		if (rexp1prime == null)
			return rexp2;
		return NFA.concat(rexp2, rexp1prime);
	
	}
	
	public NFA rexp2() throws IOException {
		readSpaces();
		if (peek() == '(') {
			in.read(); //read first paren
			NFA rexp = rexp();
			in.read(); //read second paren
			return rexp2tail(rexp);
		} else if (RE_CHAR.contains("" + peek())) { //might be 2 characters
			NFA newNFA = new NFA((char) in.read()); //read the RE_CHAR
			return rexp2tail(newNFA);  // need to make this work for star or plus
		} else {
			return rexp3();
		}		
	}
	
	//Might be smart to pass down the new NFA to this function
	public NFA rexp2tail(NFA nfa) throws IOException {
		readSpaces();
		if (peek() == '*') {
			in.read();
			//return NFA that is just *
			return NFA.star(nfa);
		} else if (peek() == '+') {
			in.read();
			//return NFA that is just +
			return NFA.plus(nfa);
		} else {
			return nfa; //Just return the NFA, make no changes.
		}
	}
	
	public NFA rexp3() throws IOException {
		readSpaces();
		if (peek() == '.' || peek() == '[' || peek() == '$') {
			return charclass();
		} else {
			return null;
		}
	}
	
	public NFA charclass() throws IOException {
		readSpaces();
		if (peek() == '.') {
			in.read();
			//return NFA with ALL CHARACTERS
		} else if (peek() == '[') {
			in.read();
			return NFA.compress(charclass1()); //returns NFA with a class of characters
		} else {
			//Defined class - might not need?
			//returns a premade NFA character class
			return null;
		}
		return null;
	}
	
	public NFA charclass1() throws IOException {
		readSpaces();
		if (peek() == '^') {
			return excludeset();
		} else {
			return charsetlist();
		}		
	}
	
	public NFA charsetlist() throws IOException {
		readSpaces();
		if (peek() == ']') {
			in.read();  //Read closing bracket
			return null;  //Actually complete the NFA at this point
		} else {
			NFA charset = charset();
			NFA charsetlist = charsetlist();
			if (charsetlist == null)
				return charset;
			return NFA.union(charset, charsetlist);
		}
	}
	
	public NFA charset() throws IOException{
		readSpaces();
		if (CLS_CHAR.contains("" + peek())) {
			char c = (char)in.read();
			NFA newNFA = new NFA(c);	//could be two
			NFA charsettail = charsettail(c);
			if (charsettail == null)
				return newNFA;
			return NFA.union(newNFA, charsettail); //Since these are in a [] set, we must union everything
			
		}
		return null;
	}
	
	public NFA charsettail(char c) throws IOException{
		readSpaces();
		if (peek() == '-') {
			in.read(); //This will read the dash
			char cend = (char)in.read(); //This will be the end of a range
			//Check if the end character of the range is bigger than the start
			if ((Character.isLowerCase(c) && Character.isLowerCase(cend) && c < cend) || 
					(Character.isUpperCase(c) && Character.isUpperCase(cend) && c < cend) ||
					(Character.isDigit(c) && Character.isDigit(cend) && c < cend)) {
				NFA range = new NFA((char)(c+1));
				for (int i = c+2; i <= cend; i++) {
					range = NFA.union(range, new NFA((char)i));
				}
				return range;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	public NFA excludeset() throws IOException {
		readSpaces();
		if (peek() == '^') {
			in.read(); //read caret
			charset();
			in.read(); //read closing bracket (maybe check for it)
			readSpaces();
			//call in.read() a few times till you read " IN "
			readSpaces();
			excludesettail();
			//Exclude set tail should return a set of GOOD characters
			//We want to MINUS out whatever charset returns from exclude set;
			return null;  //just return null for now
		}
		return null;
	}
	
	public NFA excludesettail() throws IOException {
		readSpaces();
		if (peek() == '[') {
			in.read(); //read open
			charset();
			in.read(); //read close
			return null;
		} else {
			return null;
		}
	}

	public char peek() throws IOException {
		int c = in.read();
		in.unread(c);
		return (char) c;
	}
	
	public void readSpaces() throws IOException {
		int c = peek();
		while (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
			in.read();
			c = peek();
		}
	}
	
	private void buildSets() {
		boolean[] exclude = new boolean[256];
		int[] index_to_exclude = {32, 92, 42, 43, 63, 124, 91, 93, 40, 41, 46, 39, 34};
		for (int i : index_to_exclude) {
			exclude[i] = true;
		}
		for (int i = 32; i <= 126; i++) {
			if (exclude[i]) {
				RE_CHAR.add("\\" + (char)i);
			} else {
				RE_CHAR.add("" + (char)i);
			}
		}

		exclude = new boolean[256];
		index_to_exclude = new int[] {92, 91, 93, 94, 45};
		for (int i : index_to_exclude) {
			exclude[i] = true;
		}
		for (int i = 32; i <= 126; i++) {
			if (exclude[i]) {
				CLS_CHAR.add("\\" + (char)i);
			} else {
				CLS_CHAR.add("" + (char)i);
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		RecursiveDescentParser rdp = new RecursiveDescentParser("test(r[a-zA-Z]b|a)+");
		NFA nfa = rdp.rexp();
		System.out.println(nfa);
		
	}
}
