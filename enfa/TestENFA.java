package enfa;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.*;

public class TestENFA extends TestCase {

	public void test_constructor_match() throws Exception{

		/* States */
		Set<String> states = new TreeSet<String>();
		states.add("q0");
		states.add("q1");
		states.add("q2");
		states.add("q3");
		states.add("q4");
		
		/* Alphabet */
		Set<String> alphabet = new TreeSet<String>();
		alphabet.add("a");
		alphabet.add("b");

		/* Transitions */
		ArrayList<String[]> transitions = new ArrayList<String[]>();
		String[] t1 = { "q0", "a", "q2" };
		String[] t2 = { "q0", "a", "q1" };
		String[] t3 = { "q2", "", "q3" };
		String[] t4 = { "q3", "b", "q4" };
		transitions.add(t1);
		transitions.add(t2);
		transitions.add(t3);
		transitions.add(t4);

		/* Initial State */
		String initial_state = "q0";
		
		/* Accept States */
		Set<String> accept_states = new TreeSet<String>();
		accept_states.add("q1");
		accept_states.add("q4");
		
		/* Create ENFA */
		ENFA d1 = new ENFA(states, alphabet, transitions, initial_state,
				accept_states);
		
		/* Test */
		assertTrue(d1.match("a.b"));

	}


}