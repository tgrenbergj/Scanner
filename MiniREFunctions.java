import java.util.*;
import java.io.*;
import java.nio.channels.FileChannel;

public class MiniREFunctions {

	
	/**
	 * Print a MiniREVariable
	 * @param var
	 */
	public static void print(MiniREVariable var) {
		System.out.println(var.toString());
	}
	
	/**
	 * Return the size of a string list if the variable is a list, 
	 * otherwise -1.
	 * @param var
	 * @return
	 */
	public static int hash(MiniREVariable var) {
		if (var.getType().equals(MiniREVariable.Type.STRING))
			return var.getStrings().size();
		else
			return -1;
	}
	
	/**
	 * Create a list of the strings with maximum frequency in the passed
	 * in list
	 * @param list
	 * @return
	 */
	public static List<MiniREString> maxfreqstring(MiniREVariable var) {
		if (!var.getType().equals(MiniREVariable.Type.STRING)) {
			System.err.println("Warning: tried to get the frequency of a non-stringlist");
			return new LinkedList<MiniREString>();
		}
		int max = -1;
		for (MiniREString str : var.getStrings()) {
			max = Math.max(max, str.getCount());
		}
		List<MiniREString> maxList = new LinkedList<MiniREString>();
		for (MiniREString str: var.getStrings()) {
			if (str.getCount() == max)
				maxList.add(str);
		}
		return maxList;
	}
	
	/**
	 * Replace all occurrences of regex with string in srcFile, and save
	 * the modified output to destFile
	 * @param regex
	 * @param string
	 * @param srcFile
	 * @param destFile
	 */
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
		String tmpFile1 = "__temp1__.txt";
		String tmpFile2 = "__temp2__.txt";
		
		copyFile(removeQuotes(srcFile), tmpFile1);
		
		int maxCount = 20;
		int count = 0;
		do {
			replace(regex, string, tmpFile1, tmpFile2);
			String temp = tmpFile1;
			tmpFile1 = tmpFile2;
			tmpFile2 = temp;
			count++;
		} while ( count < maxCount && !compareFiles(tmpFile1, tmpFile2) );
		
		if (count == maxCount) {
			System.err.println("Stopped recursing, expect infinite recursion.");
		}
		System.out.println(count);
		copyFile(tmpFile2, removeQuotes(destFile));
	}
	
	/**
	 * Find all occurrences of regex in the passed in filename.
	 * @param regex
	 * @param file
	 * @return
	 */
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
	
	/**
	 *  Union the two lists, and union common metadata among the two lists
	 */
	public static List<MiniREString> union(List<MiniREString> list1, List<MiniREString> list2) {
		List<MiniREString> newList = new LinkedList<MiniREString>();
		Map<String, List<MiniREString>> map = new HashMap<String, List<MiniREString>>();
		//Put all the strings from list1 into a map
		for ( MiniREString str : list1) {
			List<MiniREString> list = new LinkedList<MiniREString>();
			list.add(str);
			map.put(str.getName(), list);
		}
		//Put all strings from list2 into the same map, adding to a linked
		//list in case of collisions
		for ( MiniREString str : list2) {
			if (!map.containsKey(str.getName())) {
				map.put(str.getName(), new LinkedList<MiniREString>());
			}
			List<MiniREString> list = map.get(str.getName());
			list.add(str);
		}
		
		//Add either the union, or clone of, the strings to the new list
		for (String key : map.keySet()) {
			List<MiniREString> value = map.get(key);
			if (value.size() == 1) {
				newList.add((MiniREString) value.get(0).clone());
			} else if (value.size() == 2) {
				newList.add(value.get(0).union(value.get(1)));
			} else {
				System.err.println("Something went wrong, list had > 2 entries of same string");
				System.exit(0);
			}
		}
		
		return newList;
	}
	
	/**
	 * Intersect the two lists, and union common metadata among the two lists
	 */
	public static List<MiniREString> inters(List<MiniREString> list1, List<MiniREString> list2) {
		List<MiniREString> newList = new LinkedList<MiniREString>();
		Set<String> stringSet1 = new HashSet<String>();
		Set<String> stringSet2 = new HashSet<String>();
		Map<String, MiniREString> stringMap1 = new HashMap<String, MiniREString>();
		Map<String, MiniREString> stringMap2 = new HashMap<String, MiniREString>();
		for (MiniREString str : list1) {
			stringMap1.put(str.getName(), str);
			stringSet1.add(str.getName());
		}
		for (MiniREString str : list2) {
			stringMap2.put(str.getName(), str);
			stringSet2.add(str.getName());

		}
		stringSet1.retainAll(stringSet2);
		for (String str : stringSet1) {
			MiniREString ministr1 = stringMap1.get(str);
			MiniREString ministr2 = stringMap2.get(str);
			newList.add(ministr1.union(ministr2));
		}
		return newList;
	}
	
	/**
	 * Subtract the strings that are in list2 from list1
	 */
	public static List<MiniREString> diff(List<MiniREString> list1, List<MiniREString> list2) {
		List<MiniREString> newList = new LinkedList<MiniREString>();
		for (MiniREString str : list1) {
			if (!list2.contains(str)) {
				newList.add(str);
			}
		}
		return newList;
	}
	
	/**
	 * Create a DFA from a single regex string
	 */
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
	 * Remove surrounding quotes from a string
	 */
	private static String removeQuotes(String str) {
		if (str.startsWith("\"") && str.endsWith("\"") ) {
			return str.substring(1, str.length()-1);
		} else if (str.startsWith("'") && str.endsWith("'") ) {
			return str.substring(1, str.length()-1);
		} else {
			return str;
		}
		
	}
	
	/**
	 * Return true if two files are identical
	 * @param file1
	 * @param file2
	 * @return
	 */
	private static boolean compareFiles(String file1, String file2) {
		try {
			Scanner scan1 = new Scanner(new File(file1));
			Scanner scan2 = new Scanner(new File(file2));
			while (scan1.hasNextLine() && scan2.hasNextLine()) {
				String line1 = scan1.nextLine();
				String line2 = scan2.nextLine();
				if (!line1.equals(line2)) {
					return false;
				}
			}
			if (scan1.hasNextLine() || scan2.hasNextLine()) {
				return false;
			}
			return true;
		} catch (FileNotFoundException fnfe) {
			return false;
		}
	}
	
	private static void copyFile(String src, String dst) {
		File srcFile = new File(src);
		File dstFile = new File(dst);
		FileChannel srcChan = null;
		FileChannel dstChan = null;
		
		try {
			srcChan = new FileInputStream(srcFile).getChannel();
			dstChan = new FileOutputStream(dstFile).getChannel();
			long copied = 0;
			long length = srcChan.size();
			while (copied < length) {
				copied += dstChan.transferFrom(srcChan, 0, srcChan.size());
				dstChan.position(copied);
			}
			srcChan.close();
			dstChan.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
	}
}
