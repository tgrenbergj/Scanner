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
			if (str.getCount() == max)
				maxList.add(str);
		}
		return maxList;
	}
	
	public static void replace(String regex, String string, String srcFile, String destFile) {
		if (srcFile.equals(destFile)) {
			System.err.println("Source can not equal destination");
			return;
		}
		try {
			regex = removeQuotes(regex);
			string = removeQuotes(string);
			srcFile = removeQuotes(srcFile);
			destFile = removeQuotes(destFile);
			PrintWriter destWriter = new PrintWriter(new File(destFile));
			DFAWalker walker = new DFAWalker(new File(srcFile), makeDFA(regex));
			Token token = walker.nextToken();
			while (!token.isDone()) {
				if (token.isValid()) {
					destWriter.print(string);
				} else {
					destWriter.print(token.getToken());
				}
				token = walker.nextToken();
			}
			destWriter.flush();
			destWriter.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}		
	}
	
	public static void recursivereplace(String regex, String string, String srcFile, String destFile) {
		//TODO Calls replace recursively until there are no new changes made
	}
	
	public static List<MiniREString> find(String regex, String file) {
		List<MiniREString> list = new LinkedList<MiniREString>();
		try {
			regex = removeQuotes(regex);
			file = removeQuotes(file);
			DFAWalker walker = new DFAWalker(new File(file), makeDFA(regex));
			Token token = walker.nextToken();
			while (!token.isDone()) {
				if (token.isValid()) {
					boolean found = false;
					for (MiniREString str : list) {
						if (str.getName().equals(token.getToken())) {
							str.addLocation(file, walker.getChar());
							found = true;
							break;
						}
					}
					if (!found) {
						list.add(new MiniREString(token.getToken(), file, walker.getChar()));
					}
				}
				token = walker.nextToken();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return list;
	}
	
	public static List<MiniREString> union(List<MiniREString> list1, List<MiniREString> list2) {
		List<MiniREString> newList = new LinkedList<MiniREString>();
		//TODO
//		Map<>
//		for (MiniREString str : list1) {
//			if (!list2.contains(str)) {
//				newList.add(str);
//			} else {
//				MiniREString otherStr = list2.
//				newList.add()
//			}
//		}
//		
		return newList;
	}
	
	public static List<MiniREString> inters(List<MiniREString> list1, List<MiniREString> list2) {
		List<MiniREString> newList = new LinkedList<MiniREString>();
		//TODO
		return newList;
	}
	
	public static List<MiniREString> diff(List<MiniREString> list1, List<MiniREString> list2) {
		List<MiniREString> newList = new LinkedList<MiniREString>();
		for (MiniREString str : list1) {
			if (!list2.contains(str)) {
				newList.add(str);
			}
		}
		return newList;
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
	
	
	/**
	 * Rempove surrounding quotes from a string
	 */
	private static String removeQuotes(String str) {
		return str.substring(1, str.length()-1);
	}
}
