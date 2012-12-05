import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MiniREString {

	String name;
	List<String> filenames;
	Map<String,Set<Integer>> map;
	int count;
	
	public MiniREString(String name, String filename,Integer initialLocation){
		this.name = name;
		filenames = new LinkedList<String>();
		filenames.add(filename);
		map = new HashMap<String,Set<Integer>>(); 
		map.put(filename, new HashSet<Integer>());
		map.get(filename).add(initialLocation);
		count = 1;
	}
	
	public void addLocation(String filename,Integer location){
		if(map.get(filename)==null){
			map.put(filename, new HashSet<Integer>());
			map.get(filename).add(location);
			count++;
		}
		else{
			map.get(filename).add(location);
			count++;
		}
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
	
	
}
