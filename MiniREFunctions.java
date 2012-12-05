import java.util.*;

public class MiniREFunctions {

	public static void print(MiniREVariable var) {
		//TODO Print this variable, might be INT or String List
	}
	
	public static int hash(List<MiniREString> list) {
		list.size();
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
		//TODO finds all occurences of regex in file
		return null;
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
		return null;
	}
}
