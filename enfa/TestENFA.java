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
		
	}
}