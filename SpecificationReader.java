import java.io.File;
import java.io.IOException;
import java.util.*;

public class SpecificationReader {
	String fileName;
	Scanner scan;
	private static char EPSILON = (char) 169;
	
	public SpecificationReader(String fileName){
		this.fileName = fileName;
	}
	
	public NFA run() throws IOException{
		scan = new Scanner(new File(fileName));
		String line =  scan.nextLine();
		Map<String,NFA> table = new HashMap<String,NFA>();
		List<NFA> identifier = new LinkedList<NFA>();
		while(line.length() != 0){
				int extract = line.indexOf(' ');
				String entry = line.substring(1, extract);
				for(String s:table.keySet()){
					if(line.contains("$"+s)){
						line = line.replace("$"+s,EPSILON+s+EPSILON);
					}
				}
				String toSend = line.substring(extract);
				RecursiveDescentParser rdp = new RecursiveDescentParser(toSend,table);
				NFA insert = rdp.run();
				table.put(entry, insert);
				System.out.println(entry);
				line = scan.nextLine();
			
		}
		line = scan.nextLine();
		while(line.length()!=0){
			int extract = line.indexOf(' ');
			String entry = line.substring(1, extract);
			for(String s:table.keySet()){
				if(line.contains("$"+s)){
					line = line.replace("$"+s,EPSILON+s+EPSILON);
				}
			}
			String toSend = line.substring(extract);
			RecursiveDescentParser rdp = new RecursiveDescentParser(toSend,table);
			NFA insert = rdp.run();
			insert.addTokenName(entry);
			identifier.add(insert);
			System.out.println(entry);
			if (!scan.hasNextLine())
				break;
			line = scan.nextLine();
		}
		NFA[] nfas = new NFA[identifier.size()];
		int i = 0;
		for (NFA nfa : identifier)
			nfas[i++] = nfa;
		
		NFA combinedNFA = NFATools.combine(nfas);
		combinedNFA.findDeadStates();
		return combinedNFA;
	}
	
	public static void main(String[] args) throws IOException{
		SpecificationReader sr = new SpecificationReader("sample_spec.txt");
		System.out.println(sr.run());
		
	}
}
