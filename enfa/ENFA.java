package enfa;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
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
	private String dfaInitialState = "";

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

	public void drawGraph() throws Exception {
		String outputFile = "automata.dotty";
		FileWriter fstream = new FileWriter(outputFile);
		BufferedWriter out = new BufferedWriter(fstream);
		out.write("digraph graphname {\n");

		// Accept states
		String initialState = get_initial_state();
		out.write("\tnode [shape = circle];\n");
		out.write("\tsize=\"8,5\"\n");
		for (String state : get_accept_states()) {
			out.write("\t" + state + "[shape=doublecircle];\n");
		}

		// Add transitions
		for (String transition : transitions.keySet()) {
			TreeSet<String> nextStates = transitions.get(transition);
			String[] elements = transition.split("\\.");
			String symbol = "EPSON";
			String state = elements[0];
			if (elements.length == 2) {
				symbol = elements[1];
			}
			// Add dotty transitions
			for (String nextState : nextStates) {
				out.write("\t " + state + " -> " + nextState + " [label=\""
						+ symbol + "\"];\n");
			}
		}
		out.write("}");
		out.close();

		// Invoke dotty to draw
		display_dotty(outputFile);
	}

	public void display_dotty(String fileName) {
		try {

			Process p = Runtime.getRuntime().exec(
					"cmd /C graphviz\\bin\\dotty.exe " + fileName);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				System.out.println(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
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

	public TreeSet<String> step_forward(TreeSet<String> states,
			String[] identifiers) {
		TreeSet<String> children = new TreeSet<String>();


		for (String state : states) {
			try {
				isEpsilon = false;
				TreeSet<String> nextStates = get_next_state(state, identifiers,
						0);
				if (isEpsilon) {
					children.addAll(nextStates);
					children.addAll(step_forward(nextStates, identifiers));
				} else {
					children.addAll(nextStates);
				}

			} catch (DeadState d) {
				
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
			System.out.println("Problems with initial transition AND");
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
				System.out
						.println("Problems between enfa1 and enfa2 transitions");
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
						.println("Problems between enfa2 and end state transitions");
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
			System.out.println("Problems with initial transition OR");
			e.printStackTrace();
		}

		String[] startTransition2 = { "q0", EPSILON,
				prefix2 + enfa2.initial_state };
		try {
			ret.add_transition(startTransition2);
		} catch (InvalidTransitionException e) {
			System.out.println("Problems with initial transition OR");
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
						.println("Problems between enfa1 and end state transitions");
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
						.println("Problems between enfa2 and end state transitions");
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
			System.out.println("Problems with initial transition OR");
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
						.println("Problems between enfa1 and end state transitions");
				e.printStackTrace();
			}

			// back
			String[] transition_t2 = { prefix1 + currentState, EPSILON,
					prefix1 + enfa1.initial_state };
			try {
				ret.add_transition(transition_t2);
			} catch (InvalidTransitionException e) {
				System.out
						.println("Problems between enfa1 and initial state transitions");
				e.printStackTrace();
			}
		}

		// add transition from first to last
		String[] startToEndTransition = { "q0", EPSILON, "q1" };
		try {
			ret.add_transition(startToEndTransition);
		} catch (InvalidTransitionException e) {
			System.out.println("Problems with initial transition OR");
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
			System.out.println("Problems with initial transition OR");
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
						.println("Problems between enfa1 and end state transitions");
				e.printStackTrace();
			}

			// back
			String[] transition_t2 = { prefix1 + currentState, EPSILON,
					prefix1 + enfa1.initial_state };
			try {
				ret.add_transition(transition_t2);
			} catch (InvalidTransitionException e) {
				System.out
						.println("Problems between enfa1 and initial state transitions");
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
			System.out.println("Problems with initial transition OR");
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
						.println("Problems between enfa1 and end state transitions");
				e.printStackTrace();
			}
		}

		// add transition from first to last
		String[] startToEndTransition = { "q0", EPSILON, "q1" };
		try {
			ret.add_transition(startToEndTransition);
		} catch (InvalidTransitionException e) {
			System.out.println("Problems with initial transition OR");
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
	
	public DFA optimize() throws Exception {
		HashMap<String, TreeSet<String>> eClose = get_e_close();
		HashMap<String,String> dfaTable = get_dfa_table(eClose);
		/* States */
		Set<String> dfaStates = new TreeSet<String>();
		
		for(String state : dfaTable.keySet()) {
			dfaStates.add(state.split("\\.") [0]);
			dfaStates.add(dfaTable.get(state));
		}

		/* Transitions*/
		ArrayList<String[]> dfaTransitions = new ArrayList<String[]>();
		String[] t;
		for(String state : dfaStates) {
			for(String letter : alphabet) {
				String transition = dfaTable.get(state + "." + letter);
				if(transition != null) {
					t = new String[3];
					t[0] = state;
					t[1] = letter;
					t[2] =  transition;
					dfaTransitions.add(t);
				}
			}
		}

		/* Accept States */
		Set<String> dfaAcceptStates = new TreeSet<String>();
		for(String dfaState: dfaStates) {
			for(String acceptState : accept_states)
			if(dfaState.matches(acceptState + "X.*") || dfaState.matches(acceptState) || dfaState.matches(".*X" + acceptState + "X.*") || dfaState.matches(".*X" + acceptState))
				dfaAcceptStates.add(dfaState);
		}
		
		/* Create DFA */
		DFA dfa = new DFA(dfaStates, alphabet, dfaTransitions, dfaInitialState, dfaAcceptStates);

		return dfa;
	}

	public HashMap<String, String> get_dfa_table(HashMap<String, TreeSet<String>> eClose) {
		HashMap<String, String> dfaTable = new HashMap<String, String>();
		TreeSet<String> statesKnown = new TreeSet<String>();
		LinkedList<String> statesToDo = new LinkedList<String>();
		String state;
		String newState = "";
		
		for(String st : eClose.get(initial_state)) {
			if(dfaInitialState.isEmpty())
				dfaInitialState = st;
			else
				dfaInitialState += "X" + st;
		}
		
		statesKnown.add(dfaInitialState);
		statesToDo.add(dfaInitialState);
		
		
		
		do {
			state = statesToDo.removeFirst();
			for(String letter: alphabet) {
				TreeSet<String> nextStates = new TreeSet<String>();
				for(String st : state.split("X")) {
					if(transitions.get(st + "." + letter) != null)
							nextStates.addAll(transitions.get(st + "." + letter));
				}
				
				for(String nextState : nextStates) {
					nextStates.addAll(eClose.get(nextState));
				}
				
				for(String nextState : nextStates) {
					if(newState.isEmpty())
						newState = nextState;
					else
						newState += "X" + nextState;
				}
				
				if(!statesKnown.contains(newState)) {
					statesKnown.add(newState);
					statesToDo.add(newState);
				}
					
				if(newState != "")
					dfaTable.put(state + "." + letter, newState);
				newState = "";
			}
			
		}while(!statesToDo.isEmpty());
		
		return dfaTable;
	}

	public HashMap<String, TreeSet<String>> get_e_close() {
		HashMap<String,TreeSet<String>> eClose = new HashMap<String,TreeSet<String>>();
		TreeSet<String> transition;
		TreeSet<String> st = new TreeSet<String>();
		for(String state : states) {
			st.clear();
			st.add(state);
			String[] identifiers = new String[0];
			transition = step_forward(st,identifiers);
			transition.add(state);
			eClose.put(state, transition);
		}
		return eClose;
	}
}
