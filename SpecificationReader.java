import java.io.*;
import java.util.*;

/**
 * Reads in a specification file line by line, and calls the recursive
 * descent parser on each line to create NFAs.  All of the NFAs are then
 * combined into a large NFA which can be then converted to a DFA.
 */
public class SpecificationReader {
	private final char EPSILON = (char) 7;
	private String fileName;
	private Scanner scan;
	private Map<String, Integer> tokenOrder;
	private int tokenCount;
	
	public SpecificationReader(String fileName){
		this.fileName = fileName;
		tokenCount = 0;
		tokenOrder = new HashMap<String,Integer>();
	}
	
	/**
	 * Reads in an entire input file.  It first processes character classes,
	 * then applies those character classes to identifiers.  It then unions all
	 * of the identifiers together into one large NFA.
	 * 
	 * @return An NFA that represents a language specification.
	 */
	public NFA run() throws IOException{
		scan = new Scanner(new File(fileName));
		String line =  scan.nextLine();
		Map<String,NFA> table = new HashMap<String,NFA>();
		List<NFA> identifier = new LinkedList<NFA>();
		
		//Read the character classes
		while(line.length() != 0){
				int extract = line.indexOf(' ');
				String entry = line.substring(1, extract);
				String toSend = line.substring(extract);
				
				for(String s:table.keySet()){
					if(toSend.contains("$"+s)){
						toSend = toSend.replace("$"+s,EPSILON+s+EPSILON);
					}
				}
				RecursiveDescentParser rdp = new RecursiveDescentParser(toSend,table);
				NFA insert = rdp.run();
				table.put(entry, insert);
				line = scan.nextLine();
			
		}
		//Read the line separating classes and identifiers
		line = scan.nextLine();
		
		//Read the identifiers
		while(line.length()!=0){
			int extract = line.indexOf(' ');
			String entry = line.substring(1, extract);
			tokenOrder.put(entry, tokenCount++);
			
			String toSend = line.substring(extract);
			for(String s:table.keySet()){
				if(toSend.contains("$"+s)){
					toSend = toSend.replace("$"+s,EPSILON+s+EPSILON);
				}
			}
			
			RecursiveDescentParser rdp = new RecursiveDescentParser(toSend,table);
			NFA insert = rdp.run();
			insert.addTokenName(entry);
			identifier.add(insert);
			if (!scan.hasNextLine())
				break;
			line = scan.nextLine();
		}
		
		//Combine all of the identifiers into a single NFA
		NFA[] nfas = new NFA[identifier.size()];
		int i = 0;
		for (NFA nfa : identifier)
			nfas[i++] = nfa;
		
		NFA combinedNFA = NFATools.unionAll(true, nfas);
		combinedNFA.setTokenOrder(tokenOrder);
		return combinedNFA;
	}
	
}
