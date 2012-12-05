import java.util.List;

public class MiniREVariable {

	public enum Type{STRING, INT, UNION, DIFF, INTERS, EPSILON};
	private Type type;
	private int num;
	private List<MiniREString> strings;
	
	public MiniREVariable(int value){
		this.num = value;
		type = Type.INT;
	}
	
	public MiniREVariable(List<MiniREString> strings){
		this.strings = strings;
		type = Type.STRING;
	}
	
	public MiniREVariable(Type type) {
		this.type = type;
	}
	
	public List<MiniREString> getStrings(){
		return strings;
	}
	
	public int getInt() {
		return num;
	}
	
	public Type getType(){
		return type;
	}
	
	public String toString() {
		if (type.equals(Type.STRING)) {
			return strings.toString();
		} else {
			return "" + num;
		}
	}
}
