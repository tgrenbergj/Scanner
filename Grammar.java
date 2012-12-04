import java.util.*;

/**
 * A class representing an LL(1) grammar
 *
 */
public class Grammar {
	
	public static final String EPSILON = "<epsilon>";
	
	//All of the rules of the grammar from the grammar document
	Map<String, List<String[]>> rules;
	//The start non-terminal for the grammar
	String start;
	//The terminals of the grammar
	Set<String> terminals;
	//Dynamic terminals are those that aren't static, such as a REGEX or STRING
	Set<String> dynamicTerminals;
	//A match of a terminal's text to its terminal's name from the spec document
	Map<String, String> termToNameDict;
	Map<String, String> nameToTermDict;
	//The non terminals of the grammar
	Set<String> nonterminals;
	
	//First and follow sets
	Map<String, Set<String>> firstSets;
	Map<String, Set<String>> followSets;
	
	//The LL(1) table
	String[][][] table;
	//A mapping from a string to the col/row of the table 
	Map<String, Integer> nontermMap;
	Map<String, Integer> termMap;
	
	//The length of the longest rule, used to determine max iterations for
	//first and follow sets
	private int longestRule;
	
	public Grammar(String[] specialTerminals) {
		
		dynamicTerminals = new HashSet<String>();
		for (String s : specialTerminals) {
			dynamicTerminals.add(s);
		}
		
		rules = new HashMap<String, List<String[]>>();
		terminals = new HashSet<String>();
		terminals.add("$");
		nameToTermDict = new HashMap<String, String>();
		termToNameDict = new HashMap<String, String>();
		nonterminals = new HashSet<String>();
		firstSets = new HashMap<String, Set<String>>();
		followSets = new HashMap<String, Set<String>>();
		nontermMap = new HashMap<String, Integer>();
		termMap = new HashMap<String, Integer>();
	}
	
	/**
	 * Set the start rule.
	 * @param start The start non-terminal
	 */
	public void setStart(String start) {
		this.start = start;
	}
	
	/**
	 * Add a terminal string to the grammar
	 * @param term A terminal string
	 */
	public void addTerminal(String term) {
		if (!terminals.contains(term))
				terminals.add(term);
	}
	
	/**
	 * Add a non-terminal string to the grammar
	 * @param nonterm A non-terminal string
	 */
	public void addNonterminal(String nonterm) {
		if (!nonterminals.contains(nonterm)) {
			nonterminals.add(nonterm);
			rules.put(nonterm, new LinkedList<String[]>());
		}
	}
	
	/**
	 * Add a specific rule to a non-terminal eg (A -> B C D) in (A -> B C D | int)
	 * @param nonterm The non-terminal this rule applies to, eg A
	 * @param rule An array of strings, one for each token eg [B, C, D]
	 */
	public void addRule(String nonterm, String[] rule) {
		if (!nonterminals.contains(nonterm)) {
			addNonterminal(nonterm);
		}
		List<String[]> ruleList = rules.get(nonterm);
		ruleList.add(rule);
		longestRule = Math.max(longestRule, rule.length);
	}
	
	/**
	 * Given a non-terminal and a terminal literal, get the rule in the
	 * LL(1) table
	 * @param nonterm The non-terminal string literal of the form <nonterm>
	 * @param term The terminal literal text
	 * @return The rule requested, or null if there is no rule
	 */
	public String[] getRule(String nonterm, String term) {
		int row = nontermMap.get(nonterm);
		int col = termMap.get(nameToTermDict.get(term));
		return table[row][col];
	}
	
	/**
	 * Takes a terminal literal string, and gets returns its $NAME from
	 * the specification document
	 * @param term The literal terminal string
	 * @return The NAME from the specification document
	 */
	public String getTerminalName(String term) {
		return termToNameDict.get(term);
	}
	
	/**
	 * This method returns if s is terminal or not
	 * @param s the string to be checked
	 * @return
	 */
	public boolean isTerminal(String s){
		return terminals.contains(s);
	}
	
	/**
	 * This method returns if s is non terminal or not
	 * @param s
	 * @return
	 */
	public boolean isNonTerminal(String s){
		return nonterminals.contains(s);
	}
	
	/**
	 * Calculate first sets.
	 */
	public void makeFirstSets() {
		for (String term: terminals) {
			Set<String> set = new HashSet<String>();
			set.add(term);
			firstSets.put(term, set);
		}
		
		Set<String> eps = new HashSet<String>();
		eps.add(EPSILON);
		firstSets.put(EPSILON, eps);
		
		for (String nonterm: nonterminals) {
			firstSets.put(nonterm, new HashSet<String>());
		}
		
		for (int i = 0; i <= longestRule; i++) {
			for ( String key : rules.keySet()) {
				Set<String> first = firstSets.get(key);
				for (String[] rule : rules.get(key) ) {
					int j = 0;
					boolean cont = true;
					while (cont && j < rule.length) {
						Set<String> curFirst = firstSets.get(rule[j]);
						first.addAll(removeEpsilon(curFirst));
						if (!curFirst.contains(EPSILON))
							cont = false;
						j = j + 1;
					}
					if (cont) {
						first.add(EPSILON);
					}
				}
			}
		}
	}
	
	/**
	 * Calculate follow sets.
	 */
	public void makeFollowSets() {
		for (String nonterm : nonterminals) {
			followSets.put(nonterm, new HashSet<String>());
		}
		followSets.get(start).add("$");
		for (int i = 0; i <= longestRule; i++) {
			for ( String key : rules.keySet()) {
				for (String[] rule : rules.get(key) ) {
					for (int j = 0; j < rule.length; j++) {
						if (nonterminals.contains(rule[j])) {
							Set<String> tempSet = new HashSet<String>();
							int k = j+1;
							boolean cont = true;
							while (cont && k < rule.length) {
								Set<String> curFirst = firstSets.get(rule[k]);
								tempSet.addAll(removeEpsilon(curFirst));
								if (!curFirst.contains(EPSILON))
									cont = false;
								k = k + 1;
							}
							if (cont) {
								tempSet.add(EPSILON);
							}
							followSets.get(rule[j]).addAll(removeEpsilon(tempSet));
							if (containsEpsilon(tempSet)) {
								followSets.get(rule[j]).addAll(followSets.get(key));
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Generate the LL(1) parsing table
	 */
	public void makeTable() {
		table = new String[nonterminals.size()][terminals.size()][];
		
		//Create a mapping to the columns of the table
		int i = 0;
		for (String term : terminals) {
			termMap.put(term, i);
			i++;
		}
		
		//Create a mapping to the rows of the table
		i = 0;
		for (String nonterm : nonterminals) {
			nontermMap.put(nonterm, i);
			i++;
		}
		
		//Do the algorithm
		for (String key : nonterminals) {
			for (String[] rule: rules.get(key)) {
				Set<String> first = calculateFirstSet(rule);
				for (String token : first) {
					if (!token.equals(EPSILON))
						table[nontermMap.get(key)][termMap.get(token)] = rule;
				}
				if (containsEpsilon(first)) {
					for (String token : followSets.get(key)) {
						if (!token.equals(EPSILON))
							table[nontermMap.get(key)][termMap.get(token)] = rule;
					}
				}
			}
		}
	}
	
	/**
	 * This solves the issue of not knowing the name of a token.
	 * When a string matches a token, we have the string that matched
	 * and the name of the token type that matched it.
	 * 
	 * EG "hello there" will match STR given $STR \"[a-z ]*\" 
	 * EG "begin" will match BEGIN given $BEGIN begin
	 * 
	 * Unfortunately, the grammar file does not provide us with the names
	 * of terminals, so we must somehow match the back to the specification
	 * document.
	 * 
	 * This is what does that, and creates two maps
	 * 
	 * nameToTermDict: maps from BEGIN to begin
	 * termToNameDict: maps from begin to BEGIN
	 * 
	 * If a token is dynamic, aka we can't make a mapping from every
	 * possible "string" to STR, it is a special case and is added to itself
	 * 
	 * nameToTermDict: maps from STR to STR
	 * termToNameDict: maps from STR to STR
	 * 
	 * These special cases are given in the Grammar constructor
	 * 
	 * @param file The specification file
	 */
	public void makeTokenMap(String file) {
		try {
			SpecificationReader sr = new SpecificationReader(file);
			NFA nfa = sr.run();
			DFA dfa = NFAConverter.NFAtoDFA(nfa);
			DFAWalker walker;
			for (String term : terminals) {
				if (dynamicTerminals.contains(term)) {
					nameToTermDict.put(term, term);
					termToNameDict.put(term, term);
				} else {
					walker = new DFAWalker(term, dfa);
					Token token = walker.nextToken();
					if (token.getType().equals(Token.TokenType.VALID)) {
						nameToTermDict.put(token.getName(), term);
						termToNameDict.put(term, token.getName());
					} else {
						throw new NoSuchElementException();
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Could not read token specification file.");
		}
	}
	
	
	@Override
	public String toString() {
		Map<String, Set<String>> firstShort = new HashMap<String, Set<String>>();
		for ( String key : firstSets.keySet()) {
			if (nonterminals.contains(key)) {
				firstShort.put(key, firstSets.get(key));
			}
		}
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("Start rule: %s\n", start));
		sb.append(String.format("Terminals: %s\n", terminals));
		sb.append(String.format("Non-terminals: %s\n", nonterminals));
		sb.append(String.format("First sets: %s\n", firstShort));
		sb.append(String.format("Follow sets: %s\n", followSets));
		sb.append(String.format("\n--------Rules:\n"));
		for (String key : rules.keySet()) {
			sb.append(String.format("%s:\n", key));
			for (String[] rule : rules.get(key)) {
				sb.append(String.format("\t%s\n", Arrays.toString(rule)));
			}
		}
		sb.append("\n--------LL(1) Table\n");
		for (String nonterm: nonterminals) {
			sb.append(nonterm + ": ");
			for (String term: terminals) {
				sb.append("'" + term + "'->");
				sb.append(Arrays.toString(table[nontermMap.get(nonterm)][termMap.get(term)]));
				sb.append("\t");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	/**
	 * @param set The set to check for epsilon
	 * @return True if the set contains epsilon, false otherwise
	 */
	private boolean containsEpsilon(Set<String> set) {
		return set.contains(EPSILON);
	}
	
	/**
	 * Return a set with epsilons remove.
	 * 
	 * @param set The set to remove epsilon from
	 * @return The original set minus epsilon
	 */
	private Set<String> removeEpsilon(Set<String> set) {
		Set<String> newSet = new HashSet<String>();
		for (String s : set) {
			if ( !s.equals(EPSILON) )
				newSet.add(s);
		}
		return newSet;
	}
	
	/**
	 * Calculate the first set of a specific rule
	 * @param rule The rule to calculate the first set
	 * @return The first set of the rule
	 */
	private Set<String> calculateFirstSet(String[] rule) {
		Set<String> tempSet = new HashSet<String>();
		int i = 0;
		boolean cont = true;
		while (cont && i < rule.length) {
			Set<String> curFirst = firstSets.get(rule[i]);
			tempSet.addAll(removeEpsilon(curFirst));
			if (!curFirst.contains(EPSILON))
				cont = false;
			i = i + 1;
		}
		if (cont) {
			tempSet.add(EPSILON);
		}
		return tempSet;
	}
}
