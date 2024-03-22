import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
/**
 *
 * @author yaw
 */
public class RegularExpression {

    private String regularExpression;
    private NFA nfa;
    private int stateCounter;

    // You are not allowed to change the name of this class or this constructor at all.
    public RegularExpression(String regularExpression) {
        this.regularExpression = regularExpression.replaceAll("\\s+", "");
        nfa = generateNFA();
        stateCounter=0;
    }

    // TODO: Complete this method so that it returns the nfa resulting from unioning the two input nfas.
    private NFA union(NFA nfa1, NFA nfa2) {
       
    	//combine the states by putting both into a set
    	HashSet<String> newStates = new HashSet<>();

    	
        //combine the alphabets by putting both into a set, preventing repeats, then adding back to a new char list
    	HashSet<Character> newAlphSet = new HashSet<>();
    	for(char letter: nfa1.getAlphabet()) {
    		newAlphSet.add(letter);
    	}
    	for(char letter: nfa2.getAlphabet()) {
    		if(!newAlphSet.contains(letter)) {
        		newAlphSet.add(letter);
    		}
    	}
        char[] newAlphList = new char[newAlphSet.size()];
        int i=0;
        for(Character letter: newAlphSet) {
        	newAlphList[i] = letter;
        	i++;
        }
    	
    	
    	
    	//update transitions from original dfas to new transitions
        HashMap<String, HashMap<Character, HashSet<String>>> newTransitions = new HashMap<>(); //state -> (character -> states)
        //updater contains the original state as a key, and a new unique state name as a value 
        HashMap<String, String> stateUpdater1 = new HashMap<>();
        HashMap<String, String> stateUpdater2 = new HashMap<>();

        //renames original states to be unique
        for(String state: nfa1.getStates()) {
    		stateUpdater1.put(state, state+stateCounter);
    		newStates.add(stateUpdater1.get(state));
    		stateCounter++;
        }
        
        //renames internal states to match the way they were renamed above, then puts the whole complete transition into newTransitions
        for(String state: nfa1.getStates()) {
        	HashMap<Character, HashSet<String>> charToInternalStates = new HashMap<>();        		
        	for(char letter: newAlphList) {
        		HashSet<String> internalStates = new HashSet<>();
        		internalStates = nfa1.getTransitions().get(state).get(letter);
	        	for(String internalState: internalStates) {
	        		internalStates.add(stateUpdater1.get(internalState));
	        		internalStates.remove(internalState);
	        	}
	        	charToInternalStates.put(letter, internalStates);
        	}
        	newTransitions.put(stateUpdater1.get(state),charToInternalStates);
        }
        
        
        //repeats everything above with nfa2
        for(String state: nfa2.getStates()) {
    		stateUpdater2.put(state, state+stateCounter);
    		newStates.add(stateUpdater2.get(state));
    		stateCounter++;
        }
        
        for(String state: nfa2.getStates()) {
        	HashMap<Character, HashSet<String>> charToInternalStates = new HashMap<>();        		
        	for(char letter: newAlphList) {
        		HashSet<String> internalStates = new HashSet<>();
        		internalStates = nfa2.getTransitions().get(state).get(letter);
	        	for(String internalState: internalStates) {
	        		internalStates.add(stateUpdater2.get(internalState));
	        		internalStates.remove(internalState);
	        	}
	        	charToInternalStates.put(letter, internalStates);
        	}
        	newTransitions.put(stateUpdater2.get(state),charToInternalStates);
        }
    	
        
        
        //add new start state and transitions
    	String startState = "sUnion";
        newStates.add(startState);

    	//add transitions with e from S_Union to both nfa start states
        HashMap<Character, HashSet<String>> transition = new HashMap<>();
        transition.put('e', new HashSet<>(Arrays.asList(stateUpdater1.get(nfa1.getStartState()),stateUpdater2.get(nfa2.getStartState()))));
        newTransitions.put(startState, transition);
        
        //make newStates a list
        
        String[] newStatesList = new String[newStates.size()];
        i = 0;
        for(String state: newStates) {
        	newStatesList[i] = state;
        	i++;
        }
        
        //combine accept states, just by adding both to a new list
        String[] newAcceptStates = new String[nfa1.getAcceptStates().length+nfa2.getAcceptStates().length];
        i=0;
        for(String state: nfa1.getAcceptStates()) {
        	newAcceptStates[i] = stateUpdater1.get(state);
        	i++;
        }
        for(String state: nfa2.getAcceptStates()) {
        	newAcceptStates[i] = stateUpdater2.get(state);
        	i++;
        }
        
        //put it all together
        NFA nfaU = new NFA(newStatesList, newAlphList, newTransitions, startState, newAcceptStates);
    	return nfaU;
    }

    // TODO: Complete this method so that it returns the nfa resulting from concatenating the two input nfas.
    private NFA concatenate(NFA nfa1, NFA nfa2) {
    	//combine the states by putting both into a set
    	HashSet<String> newStates = new HashSet<>();

    	
        //combine the alphabets by putting both into a set, preventing repeats, then adding back to a new char list
    	HashSet<Character> newAlphSet = new HashSet<>();
    	for(char letter: nfa1.getAlphabet()) {
    		newAlphSet.add(letter);
    	}
    	for(char letter: nfa2.getAlphabet()) {
    		if(!newAlphSet.contains(letter)) {
        		newAlphSet.add(letter);
    		}
    	}
        char[] newAlphList = new char[newAlphSet.size()];
        int i=0;
        for(Character letter: newAlphSet) {
        	newAlphList[i] = letter;
        	i++;
        }	
    	
    	
    	//update transitions from original dfas to new transitions
        HashMap<String, HashMap<Character, HashSet<String>>> newTransitions = new HashMap<>(); //state -> (character -> states)
        //updater contains the original state as a key, and a new unique state name as a value 
        HashMap<String, String> stateUpdater1 = new HashMap<>();
        HashMap<String, String> stateUpdater2 = new HashMap<>();

        //renames original states to be unique
        for(String state: nfa1.getStates()) {
    		stateUpdater1.put(state, state+stateCounter);
    		newStates.add(stateUpdater1.get(state));
    		stateCounter++;
        }
        
        //renames internal states to match the way they were renamed above, then puts the whole complete transition into newTransitions
        for(String state: nfa1.getStates()) {
        	HashMap<Character, HashSet<String>> charToInternalStates = new HashMap<>();        		
        	for(char letter: newAlphList) {
        		HashSet<String> internalStates = new HashSet<>();
        		internalStates = nfa1.getTransitions().get(state).get(letter);
	        	for(String internalState: internalStates) {
	        		internalStates.add(stateUpdater1.get(internalState));
	        		internalStates.remove(internalState);
	        	}
	        	charToInternalStates.put(letter, internalStates);
        	}
        	newTransitions.put(stateUpdater1.get(state),charToInternalStates);
        }
        
        
        //repeats last 2 chunks with nfa2
        for(String state: nfa2.getStates()) {
    		stateUpdater2.put(state, state+stateCounter);
    		newStates.add(stateUpdater2.get(state));
    		stateCounter++;
        }
        
        for(String state: nfa2.getStates()) {
        	HashMap<Character, HashSet<String>> charToInternalStates = new HashMap<>();        		
        	for(char letter: newAlphList) {
        		HashSet<String> internalStates = new HashSet<>();
        		internalStates = nfa2.getTransitions().get(state).get(letter);
	        	for(String internalState: internalStates) {
	        		internalStates.add(stateUpdater2.get(internalState));
	        		internalStates.remove(internalState);
	        	}
	        	charToInternalStates.put(letter, internalStates);
        	}
        	newTransitions.put(stateUpdater2.get(state),charToInternalStates);
        }
        
       //set transitions from accept states in nfa 1 to start state in nfa2
        
        for(String state: newStates) {
        	for (int j = 0; j < nfa1.getAcceptStates().length; j++) {
				if(state.equals(stateUpdater1.get(nfa1.getAcceptStates()[j]))){
					if(newTransitions.containsKey(state)) {
						
						//if an e transition alr exists from an accept state
						if(newTransitions.get(state).keySet().contains('e')) {
							HashSet<String> internalStates = new HashSet<>();				        	
							internalStates = newTransitions.get(state).get('e');
							internalStates.add(stateUpdater2.get(nfa2.getStartState()));
							
				        	HashMap<Character, HashSet<String>> charToInternalStates = new HashMap<>();
				        	charToInternalStates = newTransitions.get(state);
				        	charToInternalStates.remove('e');
				        	charToInternalStates.put('e', internalStates);
				        	newTransitions.put(state, charToInternalStates);
							
							
						}
						
						//if a transition exists from an accept state but it isn't an e transition
						else {
							HashSet<String> internalStates = new HashSet<>();
							internalStates.add(stateUpdater2.get(nfa2.getStartState()));
				        	HashMap<Character, HashSet<String>> charToInternalStates = new HashMap<>();
				        	charToInternalStates = newTransitions.get(state);
				        	charToInternalStates.put('e', internalStates);
				        	newTransitions.put(state, charToInternalStates);
				        	
						}
					}
					
					else {
						//accept state doesn't have any transitions coming from it
						HashSet<String> internalStates = new HashSet<>();
			        	HashMap<Character, HashSet<String>> charToInternalStates = new HashMap<>();
			        	internalStates.add(stateUpdater2.get(nfa2.getStartState()));
			        	charToInternalStates.put('e',internalStates);
						newTransitions.put(state, charToInternalStates);
					}
				}
			}
        }
        
        
        //set start state
        String newStartState = nfa1.getStartState();
        
        //combine accept states, just by adding both to a new list
        String[] newAcceptStates = new String[nfa1.getAcceptStates().length+nfa2.getAcceptStates().length];
        i=0;
        for(String state: nfa1.getAcceptStates()) {
        	newAcceptStates[i] = stateUpdater1.get(state);
        	i++;
        }
        for(String state: nfa2.getAcceptStates()) {
        	newAcceptStates[i] = stateUpdater2.get(state);
        	i++;
        }
        
       
        //make newStates a list
        String[] newStatesList = new String[newStates.size()];
        i = 0;
        for(String state: newStates) {
        	newStatesList[i] = state;
        	i++;
        }
        
        //put it all together
        NFA nfaC = new NFA(newStatesList, newAlphList, newTransitions, newStartState, newAcceptStates);
    	return nfaC;        
    }

    // TODO: Complete this method so that it returns the nfa resulting from "staring" the input nfa.
    private NFA star(NFA nfa) {
        return null;
    }

    // TODO: Complete this method so that it returns the nfa resulting from "plussing" the input nfa.
    private NFA plus(NFA nfa) {
        return null;
    }

    // TODO: Complete this method so that it returns the nfa that only accepts the character c.
    private NFA singleCharNFA(char c) {
    	String[] states = {"S1","S2"};
    	char[] alphabet = {'c'};
    	HashMap<String, HashMap<Character, HashSet<String>>> transitions = new HashMap<>(); 
    	HashSet<String> internalStates = new HashSet<>();
    	HashMap<Character, HashSet<String>> charToInternalStates = new HashMap<>();
    	internalStates.add(states[1]);
    	charToInternalStates.put('c', internalStates);
    	transitions.put(states[0], charToInternalStates);
    	String[] acceptStates = {states[1]};
    	NFA nfaSingleChar = new NFA(states, alphabet, transitions, states[0],acceptStates);
    	return nfaSingleChar;
    }

    // You are not allowed to change this method's header at all.
    public boolean test(String string) {
        return nfa.accepts(string);
    }

    // Parser. I strongly recommend you do not change any code below this line.
    // Do not change any of the characters recognized in the regex (e.g., U, *, +, 0, 1)
    private int position = -1, ch;

    public NFA generateNFA() {
        nextChar();
        return parseExpression();
    }

    public void nextChar() {
        ch = (++position < regularExpression.length()) ? regularExpression.charAt(position) : -1;
    }

    public boolean eat(int charToEat) {
        if (ch == charToEat) {
            nextChar();
            return true;
        }
        return false;
    }

    public NFA parseExpression() {
        NFA nfa = parseTerm();
        while (true) {
            if (eat('U')) {
                // Create the nfa that is the union of the two passed nfas.
                nfa = union(nfa, parseTerm());
            } else {
                return nfa;
            }
        }
    }

    public NFA parseTerm() {
        NFA nfa = parseFactor();
        while (true) {
            // Concatenate NFAs.
            if (ch == '0' || ch == '1' || ch == '(') {
                // Create the nfa that is the concatentaion of the two passed nfas.
                nfa = concatenate(nfa, parseFactor());
            } else {
                return nfa;
            }
        }
    }

    public NFA parseFactor() {
        NFA nfa = null;
        if (eat('(')) {
            nfa = parseExpression();
            if (!eat(')')) {
               throw new RuntimeException("Missing ')'");
            }
        } else if (ch == '0' || ch == '1') {
            // Create the nfa that only accepts the character being passed (regularExpression.charAt(position) == '0' or '1').
            nfa = singleCharNFA(regularExpression.charAt(position));
            nextChar();
        }

        if (eat('*')) {
            // Create the nfa that is the star of the passed nfa.
            nfa = star(nfa);
        } else if (eat('+')) {
            // Create the nfa that is the plus of the passed nfa.
            nfa = plus(nfa);
        }

        return nfa;
    }
}
