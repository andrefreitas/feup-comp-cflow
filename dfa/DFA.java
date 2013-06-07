package dfa;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashMap;
import java.util.TreeSet;
public class DFA {

	private Set<String> states;
	private Set<String> alphabet;
	private HashMap<String, String> transitions;
	private String initial_state;
	private Set<String> accept_states;

	public DFA(Set<String> states, Set<String> alphabet,
			ArrayList<String[]> transitions, String initial_state,
			Set<String> accept_states) throws Exception {
		this.states = states;
		this.alphabet = alphabet;
		this.transitions = new HashMap<String, String>();
		set_transitions(transitions);
		set_initial_state(initial_state);
		set_accept_states(accept_states);
	}

	public DFA() {
		this.states = new TreeSet<String>();
		this.alphabet = new TreeSet<String>();
		this.transitions = new HashMap<String, String>();
		this.accept_states = new TreeSet<String>();

	}
	
	public void drawGraph() throws Exception {
		String outputFile = "automata.dotty";
		FileWriter fstream = new FileWriter(outputFile);
		BufferedWriter out = new BufferedWriter(fstream);
		out.write("digraph graphname {\n");

		// Accept states
		String initialState = initial_state;
		out.write("\tnode [shape = circle];\n");
		out.write("\tsize=\"8,5\"\n");
		for (String state : accept_states) {
			out.write("\t" + state + "[shape=doublecircle];\n");
		}

		// Add transitions
		for (String transition : transitions.keySet()) {
			String nextState = transitions.get(transition);
			String[] elements = transition.split("\\.");
			String symbol = "EPSON";
			String state = elements[0];
			if (elements.length == 2) {
				symbol = elements[1];
			}
		
			out.write("\t " + state + " -> " + nextState + " [label=\""
						+ symbol + "\"];\n");
			
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

		if (states.contains(state) && alphabet.contains(symbol)
				&& states.contains(result)) {
			transitions.put(state + "." + symbol, result);
		} else
			throw new InvalidTransitionException();

	}

	public boolean match(String string) {

		String[] identifiers = string.split("\\.");
		
	

		String state = initial_state;
		try {
			for (int i = 0; i < identifiers.length; i++) {
				state = get_next_state(state, identifiers[i]);

			}
		} catch (DeadState e) {
			return false;
		}

		if (accept_states.contains(state))
			return true;

		return false;
	}

	public String get_next_state(String q1, String symbol) throws DeadState {
		String key = q1 + "." + symbol;
		if (transitions.containsKey(key)) {
			return transitions.get(key);
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
	
	public String get_initial_state() {
		return initial_state;
	}

}