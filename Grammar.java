import java.util.*;

/**
 * A class representing an LL(1) grammar
 *
 */
public class Grammar {
	
	private final String EPSILON = "EPSILON";
	Map<String, List<String[]>> rules;
	String start;
	Set<String> terminals;
	Set<String> nonterminals;
	Map<String, Set<String>> firstSets;
	Map<String, Set<String>> followSets;
	private int longestRule;
	
	public Grammar() {
		rules = new HashMap<String, List<String[]>>();
		terminals = new HashSet<String>();
		nonterminals = new HashSet<String>();
		firstSets = new HashMap<String, Set<String>>();
		followSets = new HashMap<String, Set<String>>();
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
	 * Calculate first sets.
	 */
	public void doFirstSets() {
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
	public void doFollowSets() {
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
		sb.append(String.format("--------Rules:\n"));
		for (String key : rules.keySet()) {
			sb.append(String.format("%s:\n", key));
			for (String[] rule : rules.get(key)) {
				sb.append(String.format("\t%s\n", Arrays.toString(rule)));
			}
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
}
