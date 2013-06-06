package enfa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.*;

public class TestENFA extends TestCase {

	public void test_constructor_match() throws Exception {

		/* States */
		Set<String> states = new TreeSet<String>();
		states.add("q0");
		states.add("q1");
		states.add("q2");
		states.add("q3");
		states.add("q4");
		states.add("q5");
		states.add("q6");

		/* Alphabet */
		Set<String> alphabet = new TreeSet<String>();
		alphabet.add("a");
		alphabet.add("b");

		/* Transitions */
		ArrayList<String[]> transitions = new ArrayList<String[]>();

		
		String[] t1 = { "q0", "a", "q1" };
		String[] t2 = { "q0", "a", "q2" };
		String[] t3 = { "q2", "", "q3" };
		String[] t4 = { "q3", "b", "q4" };
		String[] t5 = { "q4", "", "q5" };
		String[] t7 = { "q5", "", "q6" };
		String[] t6 = { "q4", "", "q0" };
		transitions.add(t1);
		transitions.add(t2);
		transitions.add(t3);
		transitions.add(t4);
		transitions.add(t5);
		transitions.add(t6);
		transitions.add(t7);
		
		/* Initial State */
		String initial_state = "q0";

		/* Accept States */
		Set<String> accept_states = new TreeSet<String>();
		accept_states.add("q6");
		accept_states.add("q1");

		/* Create ENFA */
		ENFA d1 = new ENFA(states, alphabet, transitions, initial_state,
				accept_states);
		
		/* Test */
		assertTrue(d1.match("a.b"));
		assertTrue(d1.match("a.b.a.b.a.b.a"));
		assertTrue(d1.match("a"));
		
		ENFA d2 = new ENFA(states, alphabet, transitions, initial_state,
				accept_states);
		
		ENFA d3 = ENFA.operator_and(d1, d2);
		
		assertTrue(d3.match("a.b.a"));

		ENFA d4 = ENFA.operator_or(d1, d2);
		assertFalse(d4.match("a.b.a.c")); // confirmar
		assertTrue(d4.match("a.b"));
		assertTrue(d4.match("a"));
		assertTrue(d4.match("a.b.a.b.a.b.a"));
		
		ENFA d5 = ENFA.operator_star(d1);
		assertTrue(d5.match("a.b"));
		assertTrue(d5.match("a.b.a.b"));
		assertTrue(d5.match("a.b.a.b.a.b"));
		assertTrue(d5.match("a.b.a.b.a.b.a.b"));
		assertTrue(d5.match("a.a.a.a.a.a.a"));
		assertTrue(d5.match("a.a.b"));
		assertTrue(d5.match(""));
		
		ENFA d6 = ENFA.operator_plus(d1);
		assertTrue(d6.match("a.b"));
		assertTrue(d6.match("a.b.a.b"));
		assertTrue(d6.match("a.b.a.b.a.b"));
		assertTrue(d6.match("a.b.a.b.a.b.a.b"));
		assertTrue(d6.match("a.a.a.a.a.a.a"));
		assertTrue(d6.match("a.a.b"));
		assertFalse(d6.match(""));
		
		/* States */
		Set<String> states_1 = new TreeSet<String>();
		states_1.add("q0");
		states_1.add("q1");
		states_1.add("q2");
		states_1.add("q3");

		/* Alphabet */
		Set<String> alphabet_1 = new TreeSet<String>();
		alphabet_1.add("a");
		alphabet_1.add("b");
		alphabet_1.add("c");

		/* Transitions */
		ArrayList<String[]> transitions_1 = new ArrayList<String[]>();

		
		String[] a1 = { "q0", "a", "q1" };
		String[] a2 = { "q1", "b", "q2" };
		String[] a3 = { "q2", "c", "q3" };
		transitions_1.add(a1);
		transitions_1.add(a2);
		transitions_1.add(a3);
		
		/* Initial State */
		String initial_state_1 = "q0";

		/* Accept States */
		Set<String> accept_states_1 = new TreeSet<String>();
		accept_states_1.add("q3");

		/* Create ENFA */
		ENFA teste = new ENFA(states_1, alphabet_1, transitions_1, initial_state_1,
				accept_states_1);
		
		ENFA d7 = ENFA.operator_questionm(teste);
		assertFalse(d7.match("a.b"));
		assertTrue(d7.match(""));
		assertTrue(d7.match("a.b.c"));
		assertFalse(d7.match("a.b.c.a.b.c"));
		assertFalse(d7.match("a"));
		
		ENFA d8 = ENFA.operator_times(teste, 3);
		assertFalse(d8.match("a.b.c"));
		assertFalse(d8.match("a.b.c.a.b.c"));
		assertTrue(d8.match("a.b.c.a.b.c.a.b.c"));
		assertFalse(d8.match("a.b.c.a.b.c.a.b.c.a.b.c"));
		assertFalse(d8.match("a"));
		assertFalse(d8.match("a.a.b"));
		assertFalse(d8.match(""));
		
		ENFA d9 = ENFA.operator_timesleft(teste, 3);
		assertFalse(d9.match("a.b.c"));
		assertFalse(d9.match("a.b.c.a.b.c"));
		assertTrue(d9.match("a.b.c.a.b.c.a.b.c"));
		assertTrue(d9.match("a.b.c.a.b.c.a.b.c.a.b.c"));
		assertTrue(d9.match("a.b.c.a.b.c.a.b.c.a.b.c.a.b.c"));
		assertFalse(d9.match("a"));
		assertFalse(d9.match("a.a.b"));
		assertFalse(d9.match(""));
		
		ENFA d10 = ENFA.operator_timesint(teste, 3, 5);
		assertFalse(d10.match("a.b.c"));
		assertFalse(d10.match("a.b.c.a.b.c"));
		assertTrue(d10.match("a.b.c.a.b.c.a.b.c"));
		assertTrue(d10.match("a.b.c.a.b.c.a.b.c.a.b.c"));
		assertTrue(d10.match("a.b.c.a.b.c.a.b.c.a.b.c.a.b.c"));
		assertFalse(d10.match("a.b.c.a.b.c.a.b.c.a.b.c.a.b.c.a.b.c"));
		assertFalse(d10.match("a"));
		assertFalse(d10.match("a.a.b"));
		assertFalse(d10.match(""));
	}
	
	public void test_draw_graph() throws Exception {

		/* States */
		Set<String> states = new TreeSet<String>();
		states.add("q0");
		states.add("q1");
		states.add("q2");
		states.add("q3");
		states.add("q4");
		states.add("q5");
		states.add("q6");

		/* Alphabet */
		Set<String> alphabet = new TreeSet<String>();
		alphabet.add("a");
		alphabet.add("b");

		/* Transitions */
		ArrayList<String[]> transitions = new ArrayList<String[]>();

		String[] t1 = { "q0", "a", "q1" };
		String[] t2 = { "q0", "a", "q2" };
		String[] t3 = { "q2", "", "q3" };
		String[] t4 = { "q3", "b", "q4" };
		String[] t5 = { "q4", "", "q5" };
		String[] t7 = { "q5", "", "q6" };
		String[] t6 = { "q4", "", "q0" };
		transitions.add(t1);
		transitions.add(t2);
		transitions.add(t3);
		transitions.add(t4);
		transitions.add(t5);
		transitions.add(t6);
		transitions.add(t7);
		
		/* Initial State */
		String initial_state = "q0";

		/* Accept States */
		Set<String> accept_states = new TreeSet<String>();
		accept_states.add("q6");
		accept_states.add("q1");

		/* Create ENFA */
		ENFA d1 = new ENFA(states, alphabet, transitions, initial_state,
				accept_states);
		
		d1.drawGraph();
		
		/* Test File content */
	}
	
	public void test_get_table() throws Exception {
		/* States */
		Set<String> states = new TreeSet<String>();
		states.add("q0");
		states.add("q1");
		states.add("q2");
		states.add("q3");

		/* Alphabet */
		Set<String> alphabet = new TreeSet<String>();
		alphabet.add("a");

		/* Transitions */
		ArrayList<String[]> transitions = new ArrayList<String[]>();

		String[] t1 = { "q0", "", "q1" };
		String[] t2 = { "q1", "a", "q2" };
		String[] t3 = { "q2", "", "q3" };
		
		transitions.add(t1);
		transitions.add(t2);
		transitions.add(t3);
		
		/* Initial State */
		String initial_state = "q0";

		/* Accept States */
		Set<String> accept_states = new TreeSet<String>();
		accept_states.add("q3");

		/* Create ENFA */
		ENFA d1 = new ENFA(states, alphabet, transitions, initial_state,
				accept_states);
		System.out.println(d1.get_table().toString());
	}
	
	public void test_e_close() throws Exception {
		/* States */
		Set<String> states = new TreeSet<String>();
		states.add("q0");
		states.add("q1");
		states.add("q2");
		states.add("q3");

		/* Alphabet */
		Set<String> alphabet = new TreeSet<String>();
		alphabet.add("a");

		/* Transitions */
		ArrayList<String[]> transitions = new ArrayList<String[]>();

		String[] t1 = { "q0", "", "q1" };
		String[] t2 = { "q1", "a", "q2" };
		String[] t3 = { "q2", "", "q3" };
		
		transitions.add(t1);
		transitions.add(t2);
		transitions.add(t3);
		
		/* Initial State */
		String initial_state = "q0";

		/* Accept States */
		Set<String> accept_states = new TreeSet<String>();
		accept_states.add("q3");

		/* Create ENFA */
		ENFA d1 = new ENFA(states, alphabet, transitions, initial_state,
				accept_states);
		d1.drawGraph();
		System.out.println(d1.get_e_close().toString());
	}
	
	
}