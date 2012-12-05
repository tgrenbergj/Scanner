import java.util.*;


public class MiniREString {

	String name;
	Set<String> filenames;
	Map<String,Set<Integer>> map;
	int count;
	
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
	
	public void addLocation(String filename,Integer location){
		if(map.get(filename)==null) {
			map.put(filename, new HashSet<Integer>());
		}
		map.get(filename).add(location);
		count++;
	}
	
	public int getCount(){
		return count;
	}
	
	public String getName(){
		return name;
	}
	
	public boolean equals(Object other){
		return name.equals(((MiniREString)other).getName());
	}
	
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
