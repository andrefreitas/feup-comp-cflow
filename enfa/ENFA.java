package enfa;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import dfa.DFA;

public class ENFA {
	private Set<String> states;
	private Set<String> alphabet;
	private HashMap<String, TreeSet<String>> transitions;
	private String initial_state;
	private Set<String> accept_states;
	private static final String EPSILON = "";
	private boolean isEpsilon;
	private static int prefix_index = 0;

	public ENFA(Set<String> states, Set<String> alphabet,
			ArrayList<String[]> transitions, String initial_state,
			Set<String> accept_states) throws Exception {
		this.states = states;
		this.alphabet = alphabet;
		this.transitions = new HashMap<String, TreeSet<String>>();
		set_transitions(transitions);
		set_initial_state(initial_state);
		set_accept_states(accept_states);
	}

	public ENFA() {
		this.states = new TreeSet<String>();
		this.alphabet = new TreeSet<String>();
		this.transitions = new HashMap<String, TreeSet<String>>();
		this.accept_states = new TreeSet<String>();
	}
	
	public void drawGraph() throws Exception{
		 FileWriter fstream = new FileWriter("automata.dotty");
		 BufferedWriter out = new BufferedWriter(fstream);
		 out.write("digraph graphname {\n");
		 for(String transition : transitions.keySet()){
			 TreeSet<String> nextStates = transitions.get(transition);
			 String [] elements = transition.split("\\.");
			 String symbol = "EPSON";
			 String state = elements[0];
			 if (elements.length==2){
				 symbol = elements[1];
			 }
			 // Add dotty transitions
			 for(String nextState: nextStates){
				 out.write("\t " + state + " -> " + nextState +" [label=\""+ symbol +"\"];\n");
			 }
		 }
		 out.write("}");
	
		 out.close();
	}

	public String get_initial_state() {
		return initial_state;
	}
	
	public Set<String> get_accept_states() {
		return accept_states;
	}
	
	private void set_accept_states(Set<String> accept_states)
			throws InvalidStateException {
		if (this.states.containsAll(accept_states)) {
			this.accept_states = accept_states;
		} else {
			throw new InvalidStateException();
		}

	}

	public void set_initial_state(String initial_state)
			throws InvalidStateException {
		if (states.contains(initial_state)) {
			this.initial_state = initial_state;
		} else {
			throw new InvalidStateException();
		}

	}

	private void set_transitions(ArrayList<String[]> transitions)
			throws InvalidTransitionException {
		for (int i = 0; i < transitions.size(); i++) {
			add_transition(transitions.get(i));
		}

	}

	public void add_transition(String[] transition)
			throws InvalidTransitionException {
		if (transition.length < 3) {
			throw new InvalidTransitionException();
		}
		String state = transition[0];
		String symbol = transition[1];
		String result = transition[2];

		if (states.contains(state)
				&& (alphabet.contains(symbol) || symbol == EPSILON)
				&& states.contains(result)) {
			if (transitions.get(state + "." + symbol) == null) {
				transitions.put(state + "." + symbol, new TreeSet<String>());
			}
			transitions.get(state + "." + symbol).add(result);
		} else
			throw new InvalidTransitionException();

	}

	public boolean match(String string) {
		String[] identifiers = string.split("\\.");
		String state = initial_state;
		return matchRecursive(state, identifiers, 0);
	}
	
	public boolean matchRecursive(String state, String[] identifiers, int index) {

		if (accept_states.contains(state) && index == identifiers.length)
			return true;

		try {
			isEpsilon = false;
			TreeSet<String> nextStates = get_next_state(state, identifiers,
					index);

			if (isEpsilon == false) {
				index = index + 1;
			}

			boolean result = false;
			for (String nextState : nextStates) {
				boolean match = matchRecursive(nextState, identifiers, index);
				if (match == true) {
					result = true;
					break;
				}
			}

			return result;
		} catch (DeadState e) {
			return false;
		}
	}

	public TreeSet<String> step_forward(TreeSet<String> states, String identifier) {
		TreeSet<String> children = new TreeSet<String>();
		
		String[] identifiers = {identifier};
		for (String state: states) {
			try {
				isEpsilon = false;
				TreeSet<String> nextStates = get_next_state(state, identifiers, 0);
				if (isEpsilon) {
					children.addAll(step_forward(nextStates, identifier));
				}
				else {
					children.addAll(nextStates);
				}
				
			}
			catch (DeadState d) {
				d.printStackTrace();
			}
		}
		
		return children;
		
	}

	private TreeSet<String> get_next_state(String state, String[] identifiers,
			int index) throws DeadState {

		if (index < identifiers.length) {
			String key = state + "." + identifiers[index];
			if (transitions.containsKey(key)) {
				return transitions.get(key);
			}
		}

		String keyEpson = state + "." + EPSILON;

		if (transitions.containsKey(keyEpson)) {
			isEpsilon = true;
			return transitions.get(keyEpson);
		}
		throw new DeadState();

	}

	public void add_state(String state) {
		this.states.add(state);

	}

	public void add_to_alphabet(String symbol) {
		this.alphabet.add(symbol);

	}

	public void add_accept_state(String state) throws InvalidStateException {
		if (states.contains(state)) {
			accept_states.add(state);
		} else {
			throw new InvalidStateException();
		}
	}

	/*
	 * AINDA N�O EST� TESTADO ... Mas d�em uma vista de olhos para ver se � isto
	 * que se tem que fazer :)
	 */
	public static ENFA operator_and(ENFA enfa1, ENFA enfa2) {

		ENFA ret = new ENFA();

		Set<String> alphabet_temp = new TreeSet<String>();
		alphabet_temp.addAll(enfa1.alphabet);
		alphabet_temp.addAll(enfa2.alphabet);
		ret.alphabet = alphabet_temp;

		String prefix1 = getNewPrefix();
		String prefix2 = getNewPrefix();

		Set<String> states_t = addPrefixStates(enfa1.states, prefix1);
		HashMap<String, TreeSet<String>> transitions_t = addPrefixTransitions(
				enfa1.transitions, prefix1);
		Set<String> states2 = addPrefixStates(enfa2.states, prefix2);
		HashMap<String, TreeSet<String>> transitions2 = addPrefixTransitions(
				enfa2.transitions, prefix2);

		states_t.addAll(states2);

		ret.states = states_t;

		transitions_t.putAll(transitions2);

		ret.transitions = transitions_t;

		ret.add_state("q0");
		ret.add_state("q1");

		ret.initial_state = "q0";
		Set<String> finalStates = new TreeSet<String>();
		finalStates.add("q1");
		ret.accept_states = finalStates;

		// add initial transition
		String[] startTransition = { "q0", EPSILON,
				prefix1 + enfa1.initial_state };
		try {
			ret.add_transition(startTransition);
		} catch (InvalidTransitionException e) {
			System.out.println("Problemas transição inicial AND!");
			e.printStackTrace();
		}

		// add transitions between enfa1 and enfa2
		Iterator<String> it = enfa1.accept_states.iterator();

		for (; it.hasNext();) {
			String[] transition_t = { prefix1 + it.next(), EPSILON,
					prefix2 + enfa2.initial_state };
			try {
				ret.add_transition(transition_t);
			} catch (InvalidTransitionException e) {
				System.out.println("Problemas transições entre enfa1 e enfa2");
				e.printStackTrace();
			}
		}

		// add final transition

		// add final transitions between enfa2 and end state
		it = enfa2.accept_states.iterator();

		for (; it.hasNext();) {
			String[] transition_t = { prefix2 + it.next(), EPSILON, "q1" };
			try {
				ret.add_transition(transition_t);
			} catch (InvalidTransitionException e) {
				System.out
						.println("Problemas transições entre enfa2 e end state");
				e.printStackTrace();
			}
		}

		return ret;
	}

	public static ENFA operator_or(ENFA enfa1, ENFA enfa2) {

		ENFA ret = new ENFA();

		Set<String> alphabet_temp = new TreeSet<String>();
		alphabet_temp.addAll(enfa1.alphabet);
		alphabet_temp.addAll(enfa2.alphabet);
		ret.alphabet = alphabet_temp;

		String prefix1 = getNewPrefix();
		String prefix2 = getNewPrefix();

		Set<String> states_t = addPrefixStates(enfa1.states, prefix1);
		HashMap<String, TreeSet<String>> transitions_t = addPrefixTransitions(
				enfa1.transitions, prefix1);
		Set<String> states2 = addPrefixStates(enfa2.states, prefix2);
		HashMap<String, TreeSet<String>> transitions2 = addPrefixTransitions(
				enfa2.transitions, prefix2);

		states_t.addAll(states2);

		ret.states = states_t;

		transitions_t.putAll(transitions2);

		ret.transitions = transitions_t;

		ret.add_state("q0");
		ret.add_state("q1");

		ret.initial_state = "q0";
		Set<String> finalStates = new TreeSet<String>();
		finalStates.add("q1");
		ret.accept_states = finalStates;

		// add initial transition to enfa1 and enfa2
		String[] startTransition1 = { "q0", EPSILON,
				prefix1 + enfa1.initial_state };
		try {
			ret.add_transition(startTransition1);
		} catch (InvalidTransitionException e) {
			System.out.println("Problemas transição inicial OR!");
			e.printStackTrace();
		}

		String[] startTransition2 = { "q0", EPSILON,
				prefix2 + enfa2.initial_state };
		try {
			ret.add_transition(startTransition2);
		} catch (InvalidTransitionException e) {
			System.out.println("Problemas transição inicial OR!");
			e.printStackTrace();
		}

		// add final transitions

		// add final transitions between enfa1 and end state
		Iterator<String> it = enfa1.accept_states.iterator();

		for (; it.hasNext();) {
			String[] transition_t = { prefix1 + it.next(), EPSILON, "q1" };
			try {
				ret.add_transition(transition_t);
			} catch (InvalidTransitionException e) {
				System.out
						.println("Problemas transições entre enfa1 e end state");
				e.printStackTrace();
			}
		}

		// add final transitions between enfa2 and end state
		it = enfa2.accept_states.iterator();

		for (; it.hasNext();) {
			String[] transition_t = { prefix2 + it.next(), EPSILON, "q1" };
			try {
				ret.add_transition(transition_t);
			} catch (InvalidTransitionException e) {
				System.out
						.println("Problemas transições entre enfa2 e end state");
				e.printStackTrace();
			}
		}

		return ret;
	}

	public static ENFA operator_star(ENFA enfa1) {

		ENFA ret = new ENFA();

		Set<String> alphabet_temp = new TreeSet<String>();
		alphabet_temp.addAll(enfa1.alphabet);
		ret.alphabet = alphabet_temp;

		String prefix1 = getNewPrefix();

		ret.states = addPrefixStates(enfa1.states, prefix1);
		ret.transitions = addPrefixTransitions(enfa1.transitions, prefix1);

		ret.add_state("q0");
		ret.add_state("q1");

		ret.initial_state = "q0";
		Set<String> finalStates = new TreeSet<String>();
		finalStates.add("q1");
		ret.accept_states = finalStates;

		// add initial transition
		String[] startTransition = { "q0", EPSILON,
				prefix1 + enfa1.initial_state };
		try {
			ret.add_transition(startTransition);
		} catch (InvalidTransitionException e) {
			System.out.println("Problemas transição inicial OR!");
			e.printStackTrace();
		}

		// add final transition

		// add final transitions between enfa1 and end state and epsilon to
		// return
		Iterator<String> it = enfa1.accept_states.iterator();

		for (; it.hasNext();) {
			String currentState = it.next();
			String[] transition_t = { prefix1 + currentState, EPSILON, "q1" };
			try {
				ret.add_transition(transition_t);
			} catch (InvalidTransitionException e) {
				System.out
						.println("Problemas transições entre enfa1 e end state");
				e.printStackTrace();
			}

			// back
			String[] transition_t2 = { prefix1 + currentState, EPSILON,
					prefix1 + enfa1.initial_state };
			try {
				ret.add_transition(transition_t2);
			} catch (InvalidTransitionException e) {
				System.out
						.println("Problemas transições entre enfa1 e initial state");
				e.printStackTrace();
			}
		}

		// add transition from first to last
		String[] startToEndTransition = { "q0", EPSILON, "q1" };
		try {
			ret.add_transition(startToEndTransition);
		} catch (InvalidTransitionException e) {
			System.out.println("Problemas transição inicial OR!");
			e.printStackTrace();
		}

		return ret;
	}

	// Nao testado ainda
	public static ENFA operator_plus(ENFA enfa1) {
		ENFA ret = new ENFA();

		Set<String> alphabet_temp = new TreeSet<String>();
		alphabet_temp.addAll(enfa1.alphabet);
		ret.alphabet = alphabet_temp;

		String prefix1 = getNewPrefix();

		ret.states = addPrefixStates(enfa1.states, prefix1);
		ret.transitions = addPrefixTransitions(enfa1.transitions, prefix1);

		ret.add_state("q0");
		ret.add_state("q1");

		ret.initial_state = "q0";
		Set<String> finalStates = new TreeSet<String>();
		finalStates.add("q1");
		ret.accept_states = finalStates;

		// add initial transition
		String[] startTransition = { "q0", EPSILON,
				prefix1 + enfa1.initial_state };
		try {
			ret.add_transition(startTransition);
		} catch (InvalidTransitionException e) {
			System.out.println("Problemas transição inicial OR!");
			e.printStackTrace();
		}

		// add final transition

		// add final transitions between enfa1 and end state and epsilon to
		// return
		Iterator<String> it = enfa1.accept_states.iterator();

		for (; it.hasNext();) {
			String currentState = it.next();
			String[] transition_t = { prefix1 + currentState, EPSILON, "q1" };
			try {
				ret.add_transition(transition_t);
			} catch (InvalidTransitionException e) {
				System.out
						.println("Problemas transições entre enfa1 e end state");
				e.printStackTrace();
			}

			// back
			String[] transition_t2 = { prefix1 + currentState, EPSILON,
					prefix1 + enfa1.initial_state };
			try {
				ret.add_transition(transition_t2);
			} catch (InvalidTransitionException e) {
				System.out
						.println("Problemas transições entre enfa1 e initial state");
				e.printStackTrace();
			}
		}

		return ret;
	}

	// Nao testado ainda
	public static ENFA operator_questionm(ENFA enfa1) {
		ENFA ret = new ENFA();

		Set<String> alphabet_temp = new TreeSet<String>();
		alphabet_temp.addAll(enfa1.alphabet);
		ret.alphabet = alphabet_temp;

		String prefix1 = getNewPrefix();

		ret.states = addPrefixStates(enfa1.states, prefix1);
		ret.transitions = addPrefixTransitions(enfa1.transitions, prefix1);

		ret.add_state("q0");
		ret.add_state("q1");

		ret.initial_state = "q0";
		Set<String> finalStates = new TreeSet<String>();
		finalStates.add("q1");
		ret.accept_states = finalStates;

		// add initial transition
		String[] startTransition = { "q0", EPSILON,
				prefix1 + enfa1.initial_state };
		try {
			ret.add_transition(startTransition);
		} catch (InvalidTransitionException e) {
			System.out.println("Problemas transição inicial OR!");
			e.printStackTrace();
		}

		// add final transition

		// add final transitions between enfa1 and end state and epsilon to
		// return
		Iterator<String> it = enfa1.accept_states.iterator();

		for (; it.hasNext();) {
			String currentState = it.next();
			String[] transition_t = { prefix1 + currentState, EPSILON, "q1" };
			try {
				ret.add_transition(transition_t);
			} catch (InvalidTransitionException e) {
				System.out
						.println("Problemas transições entre enfa1 e end state");
				e.printStackTrace();
			}
		}

		// add transition from first to last
		String[] startToEndTransition = { "q0", EPSILON, "q1" };
		try {
			ret.add_transition(startToEndTransition);
		} catch (InvalidTransitionException e) {
			System.out.println("Problemas transição inicial OR!");
			e.printStackTrace();
		}

		return ret;
	}

	public static ENFA operator_times(ENFA enfa1, int times) {

		ENFA current = enfa1;

		for (int i = 1; i < times; i++) {
			current = operator_and(current, enfa1);
		}

		return current;
	}

	public static ENFA operator_timesleft(ENFA enfa1, int times) {

		ENFA current = operator_times(enfa1, times);
		ENFA opt = operator_star(enfa1);

		return operator_and(current, opt);
	}

	public static ENFA operator_timesint(ENFA enfa1, int times_left,
			int times_right) {

		ENFA current = operator_times(enfa1, times_left);

		for (int i = times_left + 1; i <= times_right; i++)
			current = operator_or(current, operator_times(enfa1, i));

		return current;
	}

	public static HashMap<String, TreeSet<String>> addPrefixTransitions(
			HashMap<String, TreeSet<String>> transitions_t, String prefix) {

		HashMap<String, TreeSet<String>> temp = new HashMap<String, TreeSet<String>>();

		Iterator<String> it = transitions_t.keySet().iterator();

		for (; it.hasNext();) {
			String key = it.next();
			String newKey = prefix + key;

			TreeSet<String> new_dest = new TreeSet<String>();
			Iterator<String> it2 = transitions_t.get(key).iterator();

			for (; it2.hasNext();) {
				String state = it2.next();
				String new_state = prefix + state;
				new_dest.add(new_state);
			}

			temp.put(newKey, new_dest);
		}

		return temp;
	}

	public static Set<String> addPrefixStates(Set<String> states_t,
			String prefix) {

		Set<String> temp = new TreeSet<String>();

		Iterator<String> it = states_t.iterator();

		for (; it.hasNext();) {
			temp.add(prefix + it.next());
		}

		return temp;
	}

	public static String getNewPrefix() {
		String temp = "p" + prefix_index;
		prefix_index++;
		return temp;
	}
	
	public TreeSet<String> get_closure(Set<String> statesCombined, String identifier) {
		TreeSet<String> children = new TreeSet<String>();
		
		for(String state : statesCombined) {
			children.addAll(get_closure(state, identifier));
		}
		
		return children;
	}
	
	public TreeSet<String> get_closure(String state, String identifier) {
		TreeSet<String> children = new TreeSet<String>();
		children.add(state);
		
		TreeSet<String> transitionsState = transitions.get(state + "."
				+ identifier);
		TreeSet<String> transitionsEps = transitions.get(state + "." + "");

		for (String dest : transitionsState) {
			children.add(dest);
		}

		for (String dest : transitionsEps) {
			children.addAll(get_closure(dest, identifier));
		}

		return children;
	}
	
	TreeSet<TreeSet<String>> get_combinations(Set<String> statesToCombine, int n_elements) {
		
		if(statesToCombine.size() < n_elements)
			return null;
		
		else {
			TreeSet<TreeSet<String>> temp = new TreeSet<TreeSet<String>>();
			//TODO: Fazer combinacoes
			return temp;
		}
	}

	public DFA optimize() {
		
		DFA optimization = new DFA();
		
		//index by alphabet and then by state
		HashMap<String, HashMap<TreeSet<String>, TreeSet<String>>> table = new HashMap<String, HashMap<TreeSet<String>, TreeSet<String>>>();
		
		for(String symbol: alphabet) {
			HashMap<TreeSet<String>, TreeSet<String>> temp = new HashMap<TreeSet<String>, TreeSet<String>>();
			
			for(int i = 0; i < states.size(); i++) {
				TreeSet<TreeSet<String>> combs = get_combinations(states, i);
				
				for(TreeSet<String> statesCombined: combs) {
					TreeSet<String> destinations = get_closure(statesCombined, symbol);
					
					temp.put(statesCombined, destinations);
				}
				
				
			}
			
			table.put(symbol, temp);
		}

		
		return null;
	}
}