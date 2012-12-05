import java.util.*;
import java.io.*;

public class MiniREFunctions {

	public static void print(MiniREVariable var) {
		System.out.println(var.toString());
	}
	
	public static int hash(List<MiniREString> list) {
		return list.size();
	}
	
	public static List<MiniREString> maxfreqstring(List<MiniREString> list) {
		int max = -1;
		for (MiniREString str : list) {
			max = Math.max(max, str.getCount());
		}
		List<MiniREString> maxList = new LinkedList<MiniREString>();
		for (MiniREString str: list) {
			maxList.add(str);
		}
		return maxList;
	}
	
	public static void replace(String regex, String string, String srcFile, String destFile) {
		//TODO Opens srcFile, finds every occurrence of regex, replaces it with
		//string, and saves this changed file in destFile
	}
	
	public static void recursivereplace(String regex, String string, String srcFile, String destFile) {
		//TODO Calls replace recursively until there are no new changes made
	}
	
	public static List<MiniREString> find(String regex, String file) {
		List<MiniREString> list = new LinkedList<MiniREString>();
		int filePos = 0;
		try {
			DFAWalker walker = new DFAWalker(new File(file), makeDFA(regex));
			Token token = walker.nextToken();
			while (!token.isDone()) {
				if (token.isValid()) {
					boolean found = false;
					for (MiniREString str : list) {
						if (str.getName().equals(token.getToken())) {
							str.addLocation(file, filePos);
							found = true;
							break;
						}
					}
					if (!found) {
						list.add(new MiniREString(token.getToken(), file, filePos));
					}
				}
				filePos += token.getToken().length();
				token = walker.nextToken();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return list;
	}
	
	public static List<MiniREString> union(List<MiniREString> list1, List<MiniREString> list2) {
		//TODO Unions two string lists together
		return null;
	}
	
	public static List<MiniREString> inters(List<MiniREString> list1, List<MiniREString> list2) {
		//TODO Intersects two lists
		return null;
	}
	
	public static List<MiniREString> diff(List<MiniREString> list1, List<MiniREString> list2) {
		//TODO Subtract the strings in list2 from list1
		return null;
	}
	
	private static DFA makeDFA(String regex) {
		RecursiveDescentParser rdp = new RecursiveDescentParser(regex,null);
		NFA nfa = rdp.run();
		nfa.addTokenName("REGEX");
		Map<String, Integer> tokenOrder = new HashMap<String, Integer>();
		tokenOrder.put("REGEX", 0);
		nfa.setTokenOrder(tokenOrder);
		return NFAConverter.NFAtoDFA(nfa);
	}
	
	public static void main(String[] args) {
		List<MiniREString> list = find("statement", "minire.txt");
		System.out.println(list);
	}
}
