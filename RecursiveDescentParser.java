import java.io.*;
import java.util.*;
//	private ArrayList<Map<Character, Set<Integer>>> nfa;
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
	
	public boolean rexp() throws IOException {
		readSpaces();
		boolean ret = rexp1();
		if ( ret == false ) {
			return false;
		}
		rexpprime();
		return true;
	}
	
	public boolean rexpprime() throws IOException {
		readSpaces();
		if (peek() == '|') {
			in.read(); // get the pipe
			rexp1();
			rexpprime();
			return true;
		} else {
			return false;
		}
		
	}
	public boolean rexp1() throws IOException {
		readSpaces();
		boolean ret = rexp2();
		if ( ret == false )
			return false;
		rexp1prime();
		return true;
	}
	
	public boolean rexp1prime() throws IOException {
		readSpaces();
		//Do these, or return null
		boolean ret = rexp2();
		if (ret == false) {
			return false;
		}
		rexp1prime();
		return true;
	
	}
	
	public boolean rexp2() throws IOException {
		readSpaces();
		if (peek() == '(') {
			in.read(); //read first paren
			rexp();
			in.read(); //read second paren
			rexp2tail();
			return true;
		} else if (RE_CHAR.contains("" + peek())) { //might be 2 characters
			in.read(); //read the RE_CHAR
			rexp2tail();
			return true;
		} else {
			return rexp3();
		}		
	}
	
	public boolean rexp2tail() throws IOException {
		readSpaces();
		if (peek() == '*') {
			in.read();
			//return NFA that is just *
			return true;
		} else if (peek() == '+') {
			in.read();
			//return NFA that is just +
			return true;
		} else {
			//return an epsilon NFA
			return false;
		}
	}
	
	public boolean rexp3() throws IOException {
		readSpaces();
		if (peek() == '.' || peek() == '[' || peek() == '$')
			return charclass();
		else
			return false;
	}
	
	public boolean charclass() throws IOException {
		readSpaces();
		if (peek() == '.') {
			in.read();
			//return NFA with just .
		} else if (peek() == '[') {
			in.read();
			charclass1();
		} else {
			//Defined class - might not need?
		}
		return true;
	}
	
	public boolean charclass1() throws IOException {
		readSpaces();
		if (peek() == '^') {
			return excludeset();
		} else {
			return charsetlist();
		}		
	}
	
	public boolean charsetlist() throws IOException {
		readSpaces();
		if (peek() == ']') {
			in.read();  //Read closing bracket
			return true;
		} else {
		//Do these two
			charset();
			charsetlist();
			return true;
		}
	}
	
	public boolean charset() throws IOException{
		readSpaces();
		if (CLS_CHAR.contains("" + peek())) {
			in.read();	//could be two
			charsettail(); //Might need to pass down a letter here for the start of the range
		}
		return true;
	}
	
	public boolean charsettail() throws IOException{
		readSpaces();
		if (peek() == '-') {
			in.read(); //This will be the end of a range
			in.read(); //This will read CLS_CHAR
			return true;
		} else {
			return false;
		}
	}
	
	public boolean excludeset() throws IOException {
		readSpaces();
		if (peek() == '^') {
			in.read(); //read caret
			charset();
			in.read(); //read closing bracket (maybe check for it)
			readSpaces();
			//call in.read() a few times till you read " IN "
			readSpaces();
			excludesettail();
		}
		return true;
	}
	
	public boolean excludesettail() throws IOException {
		readSpaces();
		if (peek() == '[') {
			in.read(); //read open
			charset();
			in.read(); //read close
		} else {
			//check for defined class
		}
		return true;
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
		RecursiveDescentParser rdp = new RecursiveDescentParser("([aaab]+|b)*(abb)");
		rdp.rexp();
	}
}
