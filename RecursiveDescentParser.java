import java.io.*;
import java.util.*;
//Parser

public class RecursiveDescentParser {
	private PushbackInputStream in;
	private Set<String> RE_CHAR;
	private Set<String> CLS_CHAR;
	private NFA dot;
	private Map<String, NFA> charClasses;
	private static char EPSILON = (char) 169;
	
	public RecursiveDescentParser(String s, Map<String, NFA> charClasses) {
		this.charClasses = charClasses;
		in = new PushbackInputStream(new ByteArrayInputStream(s.getBytes()));
		RE_CHAR = new HashSet<String>();
		CLS_CHAR = new HashSet<String>();
		buildSets();
	}
	
	public NFA run() throws IOException {
		return rexp();
	}
	
	public NFA rexp() throws IOException {
		readSpaces();
		NFA rexp1 = rexp1();
		NFA rexpprime = null;
		if ( peek() == '|') {
			rexpprime = rexpprime();
		}
		if (rexp1 != null && rexpprime != null) {
			return NFATools.union(rexp1, rexpprime);
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
			return NFATools.union(rexp1, rexpprime);
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
		return NFATools.concat(rexp2, rexp1prime);
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
		return NFATools.concat(rexp2, rexp1prime);
	
	}
	
	public NFA rexp2() throws IOException {
		readSpaces();
		if (peek() == '(') {
			in.read(); //read first paren
			NFA rexp = rexp();
			in.read(); //read second paren
			return rexp2tail(rexp);
		} else if (peek() == '\\' || RE_CHAR.contains("" + peek())) { //might be 2 characters
			if ( peek() == '\\') {
				in.read();
			}
			NFA newNFA = new NFA((char) in.read()); //read the RE_CHAR
			return rexp2tail(newNFA);
		} else {
			return rexp3();
		}		
	}
	
	public NFA rexp2tail(NFA nfa) throws IOException {
		readSpaces();
		if (peek() == '*') {
			in.read();
			//return NFA that is just *
			return NFATools.star(nfa);
		} else if (peek() == '+') {
			in.read();
			//return NFA that is just +
			return NFATools.plus(nfa);
		} else {
			return nfa; //Just return the NFA, make no changes.
		}
	}
	
	public NFA rexp3() throws IOException {
		readSpaces();
		if (peek() == '.' || peek() == '[' || peek() == EPSILON) {
			return charclass();
		} else {
			return null;
		}
	}
	
	public NFA charclass() throws IOException {
		readSpaces();
		if (peek() == '.') {
			in.read();
			return dot;
		} else if (peek() == '[') {
			in.read();
			return NFATools.compress(charclass1()); //returns NFA with a class of characters
		} else {
			in.read(); //read epsilon
			String charClass = "";
			while (in.available() > 0 && peek() != EPSILON) {
				charClass += (char)in.read();
			}
			in.read(); //read last epsilon
			return charClasses.get(charClass);
		}
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
			return NFATools.union(charset, charsetlist);
		}
	}
	
	public NFA charset() throws IOException{
		readSpaces();
		if (peek() == '\\' || CLS_CHAR.contains("" + peek())) {
			if (peek() == '\\')
				in.read();
			char c = (char)in.read();
			NFA newNFA = new NFA(c);
			NFA charsettail = charsettail(c);
			if (charsettail == null)
				return newNFA;
			return NFATools.union(newNFA, charsettail); //Since these are in a [] set, we must union everything
			
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
					range = NFATools.union(range, new NFA((char)i));
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
			NFA charset1 = NFATools.compress(charset());
			in.read(); //read closing bracket (maybe check for it)
			readSpaces();
			in.read(); //Read "I"
			in.read(); //Read "N"
			readSpaces();
			NFA charset2 = NFATools.compress(excludesettail());
			return NFATools.minus(charset2, charset1);
		}
		return null;
	}
	
	public NFA excludesettail() throws IOException {
		readSpaces();
		if (peek() == '[') {
			in.read(); //read open
			NFA charset = charset();
			in.read(); //read close
			return NFATools.compress(charset);
		} else {
			in.read(); //read epsilon
			String charClass = "";
			while (in.available() > 0 && peek() != EPSILON) {
				charClass += (char)in.read();
			}
			in.read();//read out epsilon
			return charClasses.get(charClass);
		}
	}

	public char peek() throws IOException {
		int c = in.read();
		in.unread(c);
		return (char) c;
	}
	
	public void readSpaces() throws IOException {
		while (in.available() > 0) {
			int c = peek();
			if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
				in.read();
				c = peek();
			} else {
				break;
			}
		}
	}
	
	private void buildSets() {
		boolean[] exclude = new boolean[256];
		int[] index_to_exclude = {32, 34, 39, 40, 41, 42, 43, 46, 63, 91, 92, 93, 124};
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
		index_to_exclude = new int[] {45, 91, 92, 93, 94};
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
		dot = new NFA((char)32);
		for (int i = 33; i <= 126; i++) {
			dot = NFATools.union(dot, new NFA((char)i));
		}
		dot = NFATools.union(dot, new NFA('\t'));
		dot = NFATools.compress(dot);
	}
}
