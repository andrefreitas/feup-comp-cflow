package cflow;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashMap;
import java.util.TreeSet;

import cflow.exceptions.DeadState;
import cflow.exceptions.InvalidStateException;
import cflow.exceptions.InvalidTransitionException;

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
		
		for(int i = 0; i < identifiers.length; i++)
			System.out.println("Id " + identifiers[i]);

		String state = initial_state;
		try {
			for (int i = 0; i < identifiers.length; i++) {
				System.out.println("state: " + state);
				state = get_next_state(state, identifiers[i]);

			}
		} catch (DeadState e) {
			return false;
		}

		if (accept_states.contains(state))
			return true;

		return false;
	}

	private String get_next_state(String q1, String symbol) throws DeadState {
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

}