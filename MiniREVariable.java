import java.util.List;

public class MiniREVariable {

	public enum Type{STRING, INT, UNION, DIFF, INTERS, EPSILON};
	private Type type;
	private int num;
	private List<MiniREString> strings;
	
	/**
	 * Create a new variable that holds an integer
	 * @param value The integer 
	 */
	public MiniREVariable(int value){
		this.num = value;
		type = Type.INT;
	}
	
	/**
	 * Create a new variable that holds a list of strings
	 * @param strings The list of strings
	 */
	public MiniREVariable(List<MiniREString> strings){
		this.strings = strings;
		type = Type.STRING;
	}
	
	/**
	 * Create a new variable type with no data, eg an operation or epsilon
	 * @param type The type to set
	 */
	public MiniREVariable(Type type) {
		this.type = type;
	}
	
	/**
	 * @return The list of strings if it is a string variable
	 */
	public List<MiniREString> getStrings(){
		return strings;
	}
	
	/**
	 * @return The data if it is an int variable
	 */
	public int getInt() {
		return num;
	}
	
	/**
	 * @return The type of the variable
	 */
	public Type getType(){
		return type;
	}
	
	@Override
	public String toString() {
		if (type.equals(Type.STRING)) {
			return strings.toString();
		} else {
			return "" + num;
		}
	}
}
