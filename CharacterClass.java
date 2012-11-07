import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class CharacterClass 
{	

	private String name; //or identifier
	private Set<Character> characters;

	//contains all the character classes specified by the Regex input file that has been parsed by the "recursive descent parser"
	private static Map<String, CharacterClass> allClasses = new HashMap<String, CharacterClass>();
	
	/**
	 *  
	 * @param name The name/identifier of the character class being created
	 * @param characters The characters classified under the name/identifier
	 */
	public CharacterClass(String name, Set<Character> characters) 
	{

		if (characters.size() == 1 && characters.contains('.')) 
		{
			// the character '.' represents any character in the language
			// includes: digits, lower case, upper case, and the escaped characters
			// a total of 95 characters
			this.characters = new HashSet<Character>(Arrays.asList('0' , '1' , '2' , '3' , '4' , '5' , '6' , 
																   '7' , '8' , '9' , 'a' , 'b' , 'c' , 'd' , 
																   'e' , 'f' , 'g' , 'h' , 'i' , 'j' , 'k' ,
																   'l' , 'm' , 'n' , 'o' , 'p' , 'q' , 'r' ,
																   's' , 't' , 'u' , 'v' , 'w' , 'x' , 'y' ,
																   'z' , 'A' , 'B' , 'C' , 'D' , 'E' , 'F' ,
																   'G' , 'H' , 'I' , 'J' , 'K' , 'L' , 'M' , 
																   'N' , 'O' , 'P' , 'Q' , 'R' , 'S' , 'T' , 
																   'U' , 'V' , 'W' , 'X' , 'Y' , 'Z' , ' ' ,
																   '\\' , '*' , '+' , '|' , '[' , ']' , '(' ,
																   ')' , '.' , '\'' , '\"' , '!' , '#' , '%' ,
																   '$' , ',' , '&' , '/' , '-' , ':' , ';' ,
																   '<' , '=' , '>' , '?' , '@' , '^' , '_' ,
																   '`' , '{' , '}' , '~'));
		}
		
		else 
		{
			this.characters = new HashSet<Character>();
			this.characters.addAll(characters);
		}

		allClasses.put(name, this);
		this.name = name;
		
	}
	
	public static Set<String> getClasses() 
	{
		return allClasses.keySet();
	}
	
	
	public static Set<Character> getCharactersByName(String name) 
	{
		CharacterClass cclass = allClasses.get(name);
		
		if (cclass == null)
			return null;
		
		return cclass.getCharacters();
	}

	public Set<Character> getCharacters() 
	{
		return characters;
	}

	public String getName() 
	{
		return name;
	}

}