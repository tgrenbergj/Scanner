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
		Map<String,NFA> identifier = new HashMap<String,NFA>();
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
				table.put(entry, insert);
				System.out.println(insert);
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
			identifier.put(entry, insert);
			System.out.println(insert);
			line = scan.nextLine();
		}
		
		return null;
	}
	
	public static void main(String[] args) throws IOException{
		SpecificationReader sr = new SpecificationReader("Input.txt");
		sr.run();
		
	}
}
