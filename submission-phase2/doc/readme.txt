CS 3240 Phase 2 Submission
--------------------------
Jeremy Grebner, Akbar Dhannani, Nathan Bayudan, Mahmoud Joudeh

-------------------------
Project Description
-------------------------

We are submitting a fully working version of the Recursive Descent Parser
solution to the MiniRE langage. We also have a semi-working version
of the LL(1) Parser.  It parses a grammar and input file and has a fully
working implementation of first sets, follow sets, the LL(1) table, and
the parser.  It does not, however, evaluate scripts.

Recursive Descent Parser Files:

MiniREVariable
	-Represents a variable that could exist in the parse tree (int, string list)
MiniREString
	-Represents a String list as defined by the project description.
MiniRERunner
	-Takes in an input file and calls the MiniREParser.
MiniREParser
	-The recursive descent parser that parses and evaluates the script.
MiniREFunctions
	-Implements functions of the language, such as union, replace, and find.

LL(1) Parser Files:

Grammar
	-Represents a grammar.  Contains first sets, follow sets, and a parse table
GrammarReader
	-Reads a grammar and generates a Grammar object.
LL1Parser
	-Takes in any grammar object and parses a file.  This will output the stack
	 at every step to show our program is functioning correctly.
LL1Runner
	-Takes in a grammar file, spec file, and input file, and shows how the
	 LL1Parser parrses the file correctly.
	 
Other files are the same as in Phase 1.

--------------------------
Assumptions
--------------------------

1) If a regular expression has a grouping with an optional character, it must be
the last element of the grouping.

Valid:    (a | b | c | )
Invalid:  ( | a | b | c)

2) In a language specification, any $IDENTIFIER defined before another
$IDENTIFIER takes precedence automatically.  For example, in our project, the 
$ID identifier must be defined after all reservered words, like $BEGIN.
Otherwise, there will be ambiguity.

3) In a regular expression inside of a script, all single quotes must be
escaped, even if they are already preceded by an escape character as
defined in the original Phase 1 description.

Valid Regex:          ['] (a | b | c | d | \') [']
Valid script regex:   '[\'] (a | b | c | d | \\') [\']'
Invalid script regex: '[\'] (a | b | c | d | \') [\']'

4) We are assuming all definitions from Phase 1 still stand.  For example, the
sample output is incorrect.  The regular expression
'([A-Z a-z])*ment([A-Z a-z])*' will match an entire line of alphabetic
characters and spaces, as long as that line contains the substring "ment".  
This is because [A-Z a-z] contains the space character, which is allowed in
the Phase 1 regex definitions.  Therefore, we consider the output to the
sample case incorrect.

--------------------------
Contributions
--------------------------
This section mainly focuses on Phase 2 contributions.

Jeremy Grebner;
MiniRE Recursive Descent Parser
Grammar and Grammar Reader
Various LL(1) conversion functions
Test Inputs

Akbar Dhannani:
MiniRE Helper Classes
All of the Runner classes
LL(1) Parser
NFA Bonus from Phase 1

Nathan Bayudan:
First and Follow Sets
DFAWalker
Test Inputs
Lots of Testing

Mahmoud Joudeh:
Original Recursive Descent Parser
Testing
