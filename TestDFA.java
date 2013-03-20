import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.*;

public class TestDFA extends TestCase {

	public void test_constructor_match() throws Exception{

		Set<String> states = new TreeSet<String>();
		states.add("q1");
		states.add("q2");

		Set<String> alphabet = new TreeSet<String>();
		alphabet.add("a");
		alphabet.add("b");

		ArrayList<String[]> transitions = new ArrayList<String[]>();
		String[] t1 = { "q1", "a", "q2" };
		String[] t2 = { "q2", "b", "q2" };
		transitions.add(t1);
		transitions.add(t2);

		String initial_state = "q1";

		Set<String> accept_states = new TreeSet<String>();
		accept_states.add("q2");
		DFA d1 = new DFA(states, alphabet, transitions, initial_state,
				accept_states);
		
		assertTrue(d1.match("a.b"));
		assertTrue(d1.match("a.b.b.b.b.b"));
		assertFalse(d1.match("a.c"));
		assertFalse(d1.match("j.h.g.r.u.i.g.h.e.r.u.i.h"));
		assertFalse(d1.match(""));
		assertFalse(d1.match("a.a.a.a.b.b.b.b.b.b"));
	}

	public void test_invalid_trans() throws Exception {

		Set<String> states = new TreeSet<String>();
		states.add("q1");
		states.add("q2");

		Set<String> alphabet = new TreeSet<String>();
		alphabet.add("a");
		alphabet.add("b");

		ArrayList<String[]> transitions = new ArrayList<String[]>();
		String[] t1 = { "q1", "c", "q2" };
		String[] t2 = { "q2", "b", "q2" };
		transitions.add(t1);
		transitions.add(t2);

		String initial_state = "q1";

		Set<String> accept_states = new TreeSet<String>();
		accept_states.add("q2");

		try {
			DFA d1 = new DFA(states, alphabet, transitions, initial_state,
					accept_states);
			fail();
		} catch (InvalidTransitionException e) {
			
		}

		

	}

	public void test_invalid_state() throws Exception{

		Set<String> states = new TreeSet<String>();
		states.add("q1");
		states.add("q2");

		Set<String> alphabet = new TreeSet<String>();
		alphabet.add("a");
		alphabet.add("b");

		ArrayList<String[]> transitions = new ArrayList<String[]>();
		String[] t1 = { "q1", "a", "q2" };
		String[] t2 = { "q3", "b", "q2" };
		transitions.add(t1);
		transitions.add(t2);

		String initial_state = "q1";

		Set<String> accept_states = new TreeSet<String>();
		accept_states.add("q2");

		try {
			DFA d1 = new DFA(states, alphabet, transitions, initial_state,
					accept_states);
			fail();
		} catch (InvalidTransitionException e) {
			
		}
		
	}

	public void test_empty_constructor() throws Exception{

		DFA d1 = new DFA();

		String[] t1 = { "q1", "a", "q2" };
		String[] t2 = { "q2", "b", "q2" };

		d1.add_state("q1");
		d1.add_state("q2");

		d1.add_to_alphabet("a");
		d1.add_to_alphabet("b");

		d1.add_transition(t1);
		d1.add_transition(t2);

		d1.set_initial_state("q1");
		d1.add_accept_state("q2");

		assertTrue(d1.match("a.b"));
		assertFalse(d1.match("a.c"));
		assertFalse(d1.match("j.h.g.r.u.i.g.h.e.r.u.i.h"));
		assertFalse(d1.match(""));
		assertFalse(d1.match("a.a.a.a.b.b.b.b.b.b"));
	}

	public void test_invalid_transition_state_added() {
		DFA d1 = new DFA();

		String[] t1 = { "q0", "c", "q2" };
		String[] t2 = { "q2", "b", "q2" };

		d1.add_state("q1");
		d1.add_state("q2");

		d1.add_to_alphabet("a");
		d1.add_to_alphabet("b");

		try {
			d1.add_transition(t1);
			d1.add_transition(t2);
			fail();

		} catch (InvalidTransitionException e) {
			
		}

		
	}

	public void test_invalid_states() throws Exception {
		DFA d1 = new DFA();

		String[] t1 = { "q1", "a", "q2" };
		String[] t2 = { "q2", "b", "q2" };

		d1.add_state("q1");
		d1.add_state("q2");

		d1.add_to_alphabet("a");
		d1.add_to_alphabet("b");

		d1.add_transition(t1);
		d1.add_transition(t2);

		try {
			d1.set_initial_state("q0");
			d1.add_accept_state("q2");
			fail();
		} catch (InvalidStateException e) {
			
		}

		
	}
}