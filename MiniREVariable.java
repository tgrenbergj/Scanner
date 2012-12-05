import java.util.List;


public class MiniREVariable {

	public enum Type{STRING, INT}
	Type type;
	int val;
	List<MiniREString> strings;
	
	public MiniREVariable(int value){
		this.val = value;
		type = type.INT;
	}
	
	public MiniREVariable(List<MiniREString> strings){
		this.strings = strings;
		type = type.STRING;
	}
	
	public List<MiniREString> getData(){
		return strings;
	}
	
	public Type getType(){
		return type;
	}
}
