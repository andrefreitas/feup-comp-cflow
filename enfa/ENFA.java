package enfa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class ENFA {
	private Set<String> states;
	private Set<String> alphabet;
	private HashMap<String, TreeSet<String>> transitions;
	private String initial_state;
	private Set<String> accept_states;
	private static final String EPSILON = "";
	private boolean isEpsilon;

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
			TreeSet<String> nextStates = get_next_state(state,
					identifiers[index]);
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

	private TreeSet<String> get_next_state(String state, String symbol)
			throws DeadState {
		String key = state + "." + symbol;
		String keyEpson = state + "." + EPSILON;
		if (transitions.containsKey(key)) {
			return transitions.get(key);
		} else if (transitions.containsKey(keyEpson)) {
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
		Iterator<String> it, it1;

		// Adds all the identifiers on the alphabet of enfa1 and enfa2 to the
		// new enfa alphabet including the null(Epsilon)
		it = enfa1.alphabet.iterator();
		for (; it.hasNext();) {
			ret.alphabet.add(it.next());
		}

		it = enfa2.alphabet.iterator();
		for (; it.hasNext();) {
			ret.alphabet.add(it.next());
		}

		ret.alphabet.add(EPSILON); // Epsilon

		int i = 0;
		// Adds an initial state q0
		ret.add_state("q" + i);
		ret.initial_state = "q" + i;
		++i;

		// Adds all the states from enfa1 and enfa2
		it = enfa1.states.iterator();
		for (; it.hasNext(); i++) {
			ret.add_state("q" + i);
		}
		it = enfa2.states.iterator();
		for (; it.hasNext(); i++) {
			ret.add_state("q" + i);
		}
		// Adds the final state and turn it into an accept state
		String final_state = "q" + i;
		ret.add_state(final_state);
		try {
			ret.add_accept_state("q" + i);
		} catch (InvalidStateException e) {
			e.printStackTrace();
		}

		// Adds the first transition from the new initial state to the enfa1
		// initial state
		String[] transition = new String[3];
		transition[0] = "q0"; // state
		transition[1] = EPSILON; // symbol (Epsilon)
		transition[2] = "q1"; // result
		try {
			ret.add_transition(transition);
		} catch (InvalidTransitionException e) {
			e.printStackTrace();
		}

		// Adds the transitions from the enfa1
		it = enfa1.states.iterator();
		it1 = enfa1.states.iterator();

		for (int j = 1; it.hasNext(); j++) {
			String state1 = it.next();
			for (int k = 1; it1.hasNext(); k++) {
				String key = state1 + "." + it1.next();
				if (enfa1.transitions.containsKey(key)) {
					TreeSet<String> symbols = enfa1.transitions.get(key); // symbol
					// (Epsilon)
					for (String symbol : symbols) {
						String[] transitionAux = new String[3];
						transitionAux[0] = "q" + j;
						transitionAux[1] = symbol;
						transitionAux[2] = "q" + k;
						try {
							ret.add_transition(transition);
						} catch (InvalidTransitionException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		// Adds the transitions from the enfa2
		it = enfa2.states.iterator();
		it1 = enfa2.states.iterator();

		for (int j = enfa1.states.size(); it.hasNext(); j++) {
			String state1 = it.next();
			for (int k = enfa1.states.size(); it1.hasNext(); k++) {
				String key = state1 + "." + it1.next();
				if (enfa2.transitions.containsKey(key)) {
					TreeSet<String> symbols = enfa1.transitions.get(key); // symbol
					// (Epsilon)
					for (String symbol : symbols) {
						String[] transitionAux = new String[3];
						transitionAux[0] = "q" + j;
						transitionAux[1] = symbol;
						transitionAux[2] = "q" + k;
						try {
							ret.add_transition(transition);
						} catch (InvalidTransitionException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		// Connects all the accept states from the enfa1 to the initial state of
		// enfa2
		it = enfa1.accept_states.iterator();
		for (; it.hasNext();) {
			i = Integer.parseInt(it.next().substring(1)) + 1;
			transition[0] = "q" + i; // state
			transition[1] = EPSILON; // symbol (Epsilon)
			i = Integer.parseInt(enfa2.initial_state.substring(1))
					+ enfa2.states.size();
			transition[2] = "q" + i; // result
			try {
				ret.add_transition(transition);
			} catch (InvalidTransitionException e) {
				e.printStackTrace();
			}
		}

		// Connects all the accept states from the enfa2 to the final state of
		// the new enfa
		it = enfa2.accept_states.iterator();
		for (; it.hasNext();) {
			i = Integer.parseInt(it.next().substring(1)) + 1;
			transition[0] = "q" + i; // state
			transition[1] = EPSILON; // symbol (Epsilon)
			i = Integer.parseInt(final_state.substring(1));
			transition[2] = "q" + i; // result
			try {
				ret.add_transition(transition);
			} catch (InvalidTransitionException e) {
				e.printStackTrace();
			}
		}

		return ret;
	}

	public static ENFA operator_or(ENFA enfa1, ENFA enfa2) {
		ENFA ret = new ENFA();
		Iterator<String> it, it1;

		// Adds all the identifiers on the alphabet of enfa1 and enfa2 to the
		// new enfa alphabet including the null(Epsilon)
		it = enfa1.alphabet.iterator();
		for (; it.hasNext();) {
			ret.alphabet.add(it.next());
		}

		it = enfa2.alphabet.iterator();
		for (; it.hasNext();) {
			ret.alphabet.add(it.next());
		}

		ret.alphabet.add(EPSILON); // Epsilon

		int i = 0;
		// Adds an initial state q0
		ret.add_state("q" + i);
		ret.initial_state = "q" + i;
		++i;

		// Adds all the states from enfa1 and enfa2
		it = enfa1.states.iterator();
		for (; it.hasNext(); i++) {
			ret.add_state("q" + i);
			it.next();
		}
		it = enfa2.states.iterator();

		String initialState2 = new String("q" + i);

		for (; it.hasNext(); i++) {
			ret.add_state("q" + i);
			it.next();
		}
		// Adds the final state and turn it into an accept state
		String final_state = "q" + i;
		ret.add_state(final_state);
		try {
			ret.add_accept_state("q" + i);
		} catch (InvalidStateException e) {
			e.printStackTrace();
		}

		// Adds the first transition from the new initial state to the enfa1
		// initial state
		String[] transition = new String[3];
		transition[0] = "q0"; // state
		transition[1] = EPSILON; // symbol (Epsilon)
		transition[2] = "q1"; // result
		try {
			ret.add_transition(transition);
		} catch (InvalidTransitionException e) {
			e.printStackTrace();
		}

		// Adds the first transition from the new initial state to the enfa2
		// initial state
		String[] transition2 = new String[3];
		transition2[0] = "q0"; // state
		transition2[1] = EPSILON; // symbol (Epsilon)
		transition2[2] = initialState2; // result
		try {
			ret.add_transition(transition2);
		} catch (InvalidTransitionException e) {
			e.printStackTrace();
		}

		// Adds the transitions from the enfa1
		it = enfa1.states.iterator();
		it1 = enfa1.states.iterator();

		for (int j = 1; it.hasNext(); j++) {
			String state1 = it.next();
			for (int k = 1; it1.hasNext(); k++) {
				String key = state1 + "." + it1.next();
				if (enfa1.transitions.containsKey(key)) {
					TreeSet<String> symbols = enfa1.transitions.get(key); // symbol
					// (Epsilon)
					for (String symbol : symbols) {
						String[] transitionAux = new String[3];
						transitionAux[0] = "q" + j;
						transitionAux[1] = symbol;
						transitionAux[2] = "q" + k;
						try {
							ret.add_transition(transition);
						} catch (InvalidTransitionException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		// Adds the transitions from the enfa2
		it = enfa2.states.iterator();
		it1 = enfa2.states.iterator();

		for (int j = enfa1.states.size(); it.hasNext(); j++) {
			String state1 = it.next();
			for (int k = enfa1.states.size(); it1.hasNext(); k++) {
				String key = state1 + "." + it1.next();
				if (enfa1.transitions.containsKey(key)) {
					TreeSet<String> symbols = enfa1.transitions.get(key); // symbol
					// (Epsilon)
					for (String symbol : symbols) {
						String[] transitionAux = new String[3];
						transitionAux[0] = "q" + j;
						transitionAux[1] = symbol;
						transitionAux[2] = "q" + k;
						try {
							ret.add_transition(transition);
						} catch (InvalidTransitionException e) {
							e.printStackTrace();
						}
					}

				}
			}
		}

		// Connects all the accept states from the enfa1 to the final state of
		// the new enfa
		it = enfa1.accept_states.iterator();
		for (; it.hasNext();) {
			i = Integer.parseInt(it.next().substring(1)) + 1;
			transition[0] = "q" + i; // state
			transition[1] = EPSILON; // symbol (Epsilon)
			// i =
			// Integer.parseInt(enfa2.initial_state.substring(1))+enfa2.states.size();
			transition[2] = final_state; // result
			try {
				ret.add_transition(transition);
			} catch (InvalidTransitionException e) {
				e.printStackTrace();
			}
		}

		// Connects all the accept states from the enfa2 to the final state of
		// the new enfa
		it = enfa2.accept_states.iterator();
		for (; it.hasNext();) {
			i = Integer.parseInt(it.next().substring(1)) + 1;
			transition[0] = "q" + i; // state
			transition[1] = EPSILON; // symbol (Epsilon)
			// i = Integer.parseInt(final_state.substring(1));
			transition[2] = final_state; // result
			try {
				ret.add_transition(transition);
			} catch (InvalidTransitionException e) {
				e.printStackTrace();
			}
		}

		return ret;
	}

	// Nao testado ainda
	public static ENFA operator_star(ENFA enfa1) {
		ENFA ret = new ENFA();
		Iterator<String> it, it1;

		// Adds all the identifiers on the alphabet of enfa1 and enfa2 to the
		// new enfa alphabet including the null(Epsilon)
		it = enfa1.alphabet.iterator();
		for (; it.hasNext();) {
			ret.alphabet.add(it.next());
		}

		ret.alphabet.add(EPSILON); // Epsilon

		int i = 0;
		// Adds an initial state q0
		ret.add_state("q" + i);
		ret.initial_state = "q" + i;
		++i;

		// Adds all the states from enfa1 and enfa2
		it = enfa1.states.iterator();
		for (; it.hasNext(); i++) {
			ret.add_state("q" + i);
			it.next();
		}

		// Adds the final state and turn it into an accept state
		String final_state = "q" + i;
		ret.add_state(final_state);
		try {
			ret.add_accept_state("q" + i);
		} catch (InvalidStateException e) {
			e.printStackTrace();
		}

		// Adds the first transition from the new initial state to the enfa1
		// initial state
		String[] transition = new String[3];
		transition[0] = "q0"; // state
		transition[1] = EPSILON; // symbol (Epsilon)
		transition[2] = "q1"; // result
		try {
			ret.add_transition(transition);
		} catch (InvalidTransitionException e) {
			e.printStackTrace();
		}

		// Adds the transitions from the enfa1
		it = enfa1.states.iterator();
		it1 = enfa1.states.iterator();

		for (int j = 1; it.hasNext(); j++) {
			String state1 = it.next();
			for (int k = 1; it1.hasNext(); k++) {
				String key = state1 + "." + it1.next();
				if (enfa1.transitions.containsKey(key)) {
					TreeSet<String> symbols = enfa1.transitions.get(key); // symbol
					// (Epsilon)
					for (String symbol : symbols) {
						String[] transitionAux = new String[3];
						transitionAux[0] = "q" + j;
						transitionAux[1] = symbol;
						transitionAux[2] = "q" + k;
						try {
							ret.add_transition(transition);
						} catch (InvalidTransitionException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		// Connects all the accept states from the enfa1 to the final state of
		// the new enfa and to the initial_state
		it = enfa1.accept_states.iterator();
		for (; it.hasNext();) {
			i = Integer.parseInt(it.next().substring(1)) + 1;
			transition[0] = "q" + i; // state
			transition[1] = EPSILON; // symbol (Epsilon)
			// i =
			// Integer.parseInt(enfa2.initial_state.substring(1))+enfa2.states.size();
			transition[2] = final_state; // result
			try {
				ret.add_transition(transition);
			} catch (InvalidTransitionException e) {
				e.printStackTrace();
			}

			// to the initial one
			transition[2] = "q1"; // result
			try {
				ret.add_transition(transition);
			} catch (InvalidTransitionException e) {
				e.printStackTrace();
			}
		}

		// Transition between first and last

		String[] transition2 = new String[3];
		transition2[0] = "q0"; // state
		transition2[1] = EPSILON; // symbol (Epsilon)
		transition2[2] = final_state; // result
		try {
			ret.add_transition(transition2);
		} catch (InvalidTransitionException e) {
			e.printStackTrace();
		}

		return ret;
	}

	// Nao testado ainda
	public static ENFA operator_plus(ENFA enfa1) {
		ENFA ret = new ENFA();
		Iterator<String> it, it1;

		// Adds all the identifiers on the alphabet of enfa1 and enfa2 to the
		// new enfa alphabet including the null(Epsilon)
		it = enfa1.alphabet.iterator();
		for (; it.hasNext();) {
			ret.alphabet.add(it.next());
		}

		ret.alphabet.add(EPSILON); // Epsilon

		int i = 0;
		// Adds an initial state q0
		ret.add_state("q" + i);
		ret.initial_state = "q" + i;
		++i;

		// Adds all the states from enfa1 and enfa2
		it = enfa1.states.iterator();
		for (; it.hasNext(); i++) {
			ret.add_state("q" + i);
			it.next();
		}

		// Adds the final state and turn it into an accept state
		String final_state = "q" + i;
		ret.add_state(final_state);
		try {
			ret.add_accept_state("q" + i);
		} catch (InvalidStateException e) {
			e.printStackTrace();
		}

		// Adds the first transition from the new initial state to the enfa1
		// initial state
		String[] transition = new String[3];
		transition[0] = "q0"; // state
		transition[1] = EPSILON; // symbol (Epsilon)
		transition[2] = "q1"; // result
		try {
			ret.add_transition(transition);
		} catch (InvalidTransitionException e) {
			e.printStackTrace();
		}

		// Adds the transitions from the enfa1
		it = enfa1.states.iterator();
		it1 = enfa1.states.iterator();

		for (int j = 1; it.hasNext(); j++) {
			String state1 = it.next();
			for (int k = 1; it1.hasNext(); k++) {
				String key = state1 + "." + it1.next();
				if (enfa1.transitions.containsKey(key)) {
					TreeSet<String> symbols = enfa1.transitions.get(key); // symbol
					// (Epsilon)
					for (String symbol : symbols) {
						String[] transitionAux = new String[3];
						transitionAux[0] = "q" + j;
						transitionAux[1] = symbol;
						transitionAux[2] = "q" + k;
						try {
							ret.add_transition(transition);
						} catch (InvalidTransitionException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		// Connects all the accept states from the enfa1 to the final state of
		// the new enfa and to the initial_state
		it = enfa1.accept_states.iterator();
		for (; it.hasNext();) {
			i = Integer.parseInt(it.next().substring(1)) + 1;
			transition[0] = "q" + i; // state
			transition[1] = EPSILON; // symbol (Epsilon)
			// i =
			// Integer.parseInt(enfa2.initial_state.substring(1))+enfa2.states.size();
			transition[2] = final_state; // result
			try {
				ret.add_transition(transition);
			} catch (InvalidTransitionException e) {
				e.printStackTrace();
			}

			// to the initial one
			transition[2] = "q1"; // result
			try {
				ret.add_transition(transition);
			} catch (InvalidTransitionException e) {
				e.printStackTrace();
			}
		}

		return ret;
	}

	// Nao testado ainda
	public static ENFA operator_questionm(ENFA enfa1) {
		ENFA ret = new ENFA();
		Iterator<String> it, it1;

		// Adds all the identifiers on the alphabet of enfa1 and enfa2 to the
		// new enfa alphabet including the null(Epsilon)
		it = enfa1.alphabet.iterator();
		for (; it.hasNext();) {
			ret.alphabet.add(it.next());
		}

		ret.alphabet.add(EPSILON); // Epsilon

		int i = 0;
		// Adds an initial state q0
		ret.add_state("q" + i);
		ret.initial_state = "q" + i;
		++i;

		// Adds all the states from enfa1 and enfa2
		it = enfa1.states.iterator();
		for (; it.hasNext(); i++) {
			ret.add_state("q" + i);
			it.next();
		}

		// Adds the final state and turn it into an accept state
		String final_state = "q" + i;
		ret.add_state(final_state);
		try {
			ret.add_accept_state("q" + i);
		} catch (InvalidStateException e) {
			e.printStackTrace();
		}

		// Adds the first transition from the new initial state to the enfa1
		// initial state
		String[] transition = new String[3];
		transition[0] = "q0"; // state
		transition[1] = EPSILON; // symbol (Epsilon)
		transition[2] = "q1"; // result
		try {
			ret.add_transition(transition);
		} catch (InvalidTransitionException e) {
			e.printStackTrace();
		}

		// Adds the transitions from the enfa1
		it = enfa1.states.iterator();
		it1 = enfa1.states.iterator();

		for (int j = 1; it.hasNext(); j++) {
			String state1 = it.next();
			for (int k = 1; it1.hasNext(); k++) {
				String key = state1 + "." + it1.next();
				if (enfa1.transitions.containsKey(key)) {
					TreeSet<String> symbols = enfa1.transitions.get(key); // symbol
					// (Epsilon)
					for (String symbol : symbols) {
						String[] transitionAux = new String[3];
						transitionAux[0] = "q" + j;
						transitionAux[1] = symbol;
						transitionAux[2] = "q" + k;
						try {
							ret.add_transition(transition);
						} catch (InvalidTransitionException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		// Connects all the accept states from the enfa1 to the final state of
		// the new enfa
		it = enfa1.accept_states.iterator();
		for (; it.hasNext();) {
			i = Integer.parseInt(it.next().substring(1)) + 1;
			transition[0] = "q" + i; // state
			transition[1] = EPSILON; // symbol (Epsilon)
			// i =
			// Integer.parseInt(enfa2.initial_state.substring(1))+enfa2.states.size();
			transition[2] = final_state; // result
			try {
				ret.add_transition(transition);
			} catch (InvalidTransitionException e) {
				e.printStackTrace();
			}
		}

		// Transition between first and last

		String[] transition2 = new String[3];
		transition2[0] = "q0"; // state
		transition2[1] = EPSILON; // symbol (Epsilon)
		transition2[2] = final_state; // result
		try {
			ret.add_transition(transition2);
		} catch (InvalidTransitionException e) {
			e.printStackTrace();
		}

		return ret;
	}

	// Nao funciona - nao chamar
	public static ENFA operator_times(ENFA enfa1, int times) {
		ENFA ret = new ENFA();
		Iterator<String> it, it1;

		// Adds all the identifiers on the alphabet of enfa1 and enfa2 to the
		// new enfa alphabet including the null(Epsilon)
		it = enfa1.alphabet.iterator();
		for (; it.hasNext();) {
			ret.alphabet.add(it.next());
		}

		int i = 0;
		// Adds an initial state q0
		ret.add_state("q" + i);
		ret.initial_state = "q" + i;
		++i;

		ArrayList<String> last_states = new ArrayList<String>();

		// states to connect
		last_states.add(ret.initial_state);
		int previous_number = i;

		Set<String> accept_states_temp = enfa1.accept_states;

		for (int current_times = 0; current_times < times; current_times++) {

			ArrayList<String> last_states_temp = new ArrayList<String>();

			// Adds all the states from enfa1
			it = enfa1.states.iterator();
			for (; it.hasNext(); i++) {
				ret.add_state("q" + i);

				String current_state = it.next();

				if (current_state.equals(enfa1.initial_state)) {

				}

				if (accept_states_temp.contains(current_state)) {
					last_states_temp.add(current_state);
				}
			}

			last_states = last_states_temp;

		}

		// Adds the final state and turn it into an accept state
		String final_state = "q" + i;
		ret.add_state(final_state);
		try {
			ret.add_accept_state("q" + i);
		} catch (InvalidStateException e) {
			e.printStackTrace();
		}

		// Adds the first transition from the new initial state to the enfa1
		// initial state
		String[] transition = new String[3];
		transition[0] = "q0"; // state
		transition[1] = EPSILON; // symbol (Epsilon)
		transition[2] = "q1"; // result
		try {
			ret.add_transition(transition);
		} catch (InvalidTransitionException e) {
			e.printStackTrace();
		}

		// Adds the transitions from the enfa1
		it = enfa1.states.iterator();
		it1 = enfa1.states.iterator();

		for (int j = 1; it.hasNext(); j++) {
			String state1 = it.next();
			for (int k = 1; it1.hasNext(); k++) {
				String key = state1 + "." + it1.next();
				if (enfa1.transitions.containsKey(key)) {
					TreeSet<String> symbols = enfa1.transitions.get(key); // symbol
					// (Epsilon)
					for (String symbol : symbols) {
						String[] transitionAux = new String[3];
						transitionAux[0] = "q" + j;
						transitionAux[1] = symbol;
						transitionAux[2] = "q" + k;
						try {
							ret.add_transition(transition);
						} catch (InvalidTransitionException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		// Connects all the accept states from the enfa1 to the final state of
		// the new enfa
		it = enfa1.accept_states.iterator();
		for (; it.hasNext();) {
			i = Integer.parseInt(it.next().substring(1)) + 1;
			transition[0] = "q" + i; // state
			transition[1] = EPSILON; // symbol (Epsilon)
			// i =
			// Integer.parseInt(enfa2.initial_state.substring(1))+enfa2.states.size();
			transition[2] = final_state; // result
			try {
				ret.add_transition(transition);
			} catch (InvalidTransitionException e) {
				e.printStackTrace();
			}
		}

		// Transition between first and last

		String[] transition2 = new String[3];
		transition2[0] = "q0"; // state
		transition2[1] = EPSILON; // symbol (Epsilon)
		transition2[2] = final_state; // result
		try {
			ret.add_transition(transition2);
		} catch (InvalidTransitionException e) {
			e.printStackTrace();
		}

		return ret;
	}
}