import java.io.File;
import java.io.IOException;
import java.util.*;

public class DFAWalker {
	private DFA dfa;
	private String fileName;
	Scanner scan;
	public DFAWalker(String fileName, DFA dfa){
		this.dfa = dfa;
		this.fileName = fileName;
	}
	
	public void walk() throws IOException{
		File file = new File(fileName);
		Scanner scan = new Scanner(file);
	}
}
