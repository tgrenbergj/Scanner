import java.util.*;


public class MiniREString {

	private String name;
	private Set<String> filenames;
	private Map<String,Set<Integer>> map;
	private int count;
	
	public MiniREString(String name, String filename,Integer initialLocation){
		this(name);
		filenames.add(filename);
		map.put(filename, new HashSet<Integer>());
		map.get(filename).add(initialLocation);
		count = 1;
	}
	
	public MiniREString(String name) {
		this.name = name;
		filenames = new HashSet<String>();
		map = new HashMap<String,Set<Integer>>(); 
		count = 0;
	}
	
	/**
	 * Add a new file name / character position pair to this string
	 * @param filename
	 * @param location
	 */
	public void addLocation(String filename, int location){
		if(map.get(filename)==null) {
			map.put(filename, new HashSet<Integer>());
		}
		
		if (!map.get(filename).contains(location)) {
			map.get(filename).add(location);
			count++;
		}
	}
	
	/**
	 * Get the number of occurences overall of this string
	 * @return
	 */
	public int getCount(){
		return count;
	}
	
	/**
	 * Get the text value of this string
	 * @return
	 */
	public String getName(){
		return name;
	}
	
	@Override
	public boolean equals(Object other){
		return name.equals(((MiniREString)other).getName());
	}
	
	
	/**
	 * Union the metadata of two strings and return a new MiniREString
	 * @param other The other string to union.
	 * @return A new MiniREString
	 */
	public MiniREString union(MiniREString other) {
		if (!other.name.equals(this.name)) {
			return null;
		}
		
		MiniREString newString = new MiniREString(this.name);
		
		newString.filenames.addAll(this.filenames);
		newString.filenames.addAll(other.filenames);
		
		for (String fname : newString.filenames) {
			newString.map.put(fname, new HashSet<Integer>());
		}
		
		for (String fname : this.filenames) {
			Set<Integer> thisLoc = this.map.get(fname);
			Set<Integer> newLoc = newString.map.get(fname);
			newLoc.addAll(thisLoc);
		}
		
		for (String fname : other.filenames) {
			Set<Integer> thisLoc = other.map.get(fname);
			Set<Integer> newLoc = newString.map.get(fname);
			newLoc.addAll(thisLoc);
		}
		
		for (String fname : newString.filenames) {
			newString.count += newString.map.get(fname).size();
		}
		return newString;
	}
	
	@Override
	public String toString(){
		String toRet = name + ": ";
		
		int i = 0;
		for(String fname: filenames){
			toRet += "<" + fname + " : " + map.get(fname).toString()  + ">";
			if (i != filenames.size() - 1) {
				toRet += " ";
			}
			i++;
		}
	
		return toRet;
	}
	
	@Override
	public Object clone() {
		MiniREString clone = new MiniREString(name);
		
		clone.count = count;
		
		for (String file : filenames) {
			clone.filenames.add(file);
		}
		
		for (String file : filenames) {
			Set<Integer> posSet = new HashSet<Integer>();
			posSet.addAll(map.get(file));
			clone.map.put(file, posSet);
		}
		
		return clone;
	}
	
}
